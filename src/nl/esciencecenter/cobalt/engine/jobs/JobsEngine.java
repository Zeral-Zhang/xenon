/*
 * Copyright 2013 Netherlands eScience Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.esciencecenter.cobalt.engine.jobs;

import java.util.HashSet;
import java.util.Map;

import nl.esciencecenter.cobalt.CobaltException;
import nl.esciencecenter.cobalt.credentials.Credential;
import nl.esciencecenter.cobalt.engine.Adaptor;
import nl.esciencecenter.cobalt.engine.CobaltEngine;
import nl.esciencecenter.cobalt.jobs.Job;
import nl.esciencecenter.cobalt.jobs.JobDescription;
import nl.esciencecenter.cobalt.jobs.JobStatus;
import nl.esciencecenter.cobalt.jobs.Jobs;
import nl.esciencecenter.cobalt.jobs.QueueStatus;
import nl.esciencecenter.cobalt.jobs.Scheduler;
import nl.esciencecenter.cobalt.jobs.Streams;

public class JobsEngine implements Jobs {

    private final CobaltEngine octopusEngine;

    public JobsEngine(CobaltEngine octopusEngine) {
        this.octopusEngine = octopusEngine;
    }

    private Adaptor getAdaptor(Scheduler scheduler) throws CobaltException {
        return octopusEngine.getAdaptor(scheduler.getAdaptorName());
    }

    public Scheduler newScheduler(String scheme, String location, Credential credential, Map<String, String> properties) 
            throws CobaltException {

        Adaptor adaptor = octopusEngine.getAdaptorFor(scheme);
        return adaptor.jobsAdaptor().newScheduler(scheme, location, credential, properties);
    }

    @Override
    public void close(Scheduler scheduler) throws CobaltException {
        getAdaptor(scheduler).jobsAdaptor().close(scheduler);
    }

    @Override
    public boolean isOpen(Scheduler scheduler) throws CobaltException {
        return getAdaptor(scheduler).jobsAdaptor().isOpen(scheduler);
    }

    @Override
    public String getDefaultQueueName(Scheduler scheduler) throws CobaltException {
        return getAdaptor(scheduler).jobsAdaptor().getDefaultQueueName(scheduler);
    }

    @Override
    public JobStatus getJobStatus(Job job) throws CobaltException {
        return getAdaptor(job.getScheduler()).jobsAdaptor().getJobStatus(job);
    }

    private String[] getAdaptors(Job[] in) {

        HashSet<String> result = new HashSet<String>();

        for (int i = 0; i < in.length; i++) {
            if (in[i] != null) {
                result.add(in[i].getScheduler().getAdaptorName());
            }
        }

        return result.toArray(new String[result.size()]);
    }

    private void selectJobs(String adaptorName, Job[] in, Job[] out) {
        for (int i = 0; i < in.length; i++) {
            if (in[i] != null && adaptorName.equals(in[i].getScheduler().getAdaptorName())) {
                out[i] = in[i];
            } else {
                out[i] = null;
            }
        }
    }

    private void getJobStatus(String adaptor, Job[] in, JobStatus[] out) {

        JobStatus[] result = null;
        CobaltException exception = null;

        try {
            result = octopusEngine.getAdaptor(adaptor).jobsAdaptor().getJobStatuses(in);
        } catch (CobaltException e) {
            exception = e;
        }

        for (int i = 0; i < in.length; i++) {
            if (in[i] != null) {
                if (result != null) {
                    out[i] = result[i];
                } else {
                    out[i] = new JobStatusImplementation(in[i], null, null, exception, false, false, null);
                }
            }
        }
    }

    @Override
    public JobStatus[] getJobStatuses(Job... jobs) {

        // First check for the three simple cases; null, no jobs or 1 job.
        if (jobs == null || jobs.length == 0) {
            return new JobStatus[0];
        }

        if (jobs.length == 1) {

            if (jobs[0] == null) {
                return new JobStatus[1];
            }

            try {
                return new JobStatus[] { getJobStatus(jobs[0]) };
            } catch (Exception e) {
                return new JobStatus[] { new JobStatusImplementation(jobs[0], null, null, e, false, false, null) };
            }
        }

        // If we have more than one job, we first collect all adaptor names. 
        String[] adaptors = getAdaptors(jobs);

        // Next we traverse over the names, and get the JobStatus for each adaptor individually, merging the result into the 
        // overall result on the fly.
        JobStatus[] result = new JobStatus[jobs.length];
        Job[] tmp = new Job[jobs.length];

        for (int i = 0; i < adaptors.length; i++) {
            selectJobs(adaptors[i], jobs, tmp);
            getJobStatus(adaptors[i], tmp, result);
        }

        return result;
    }

    @Override
    public JobStatus waitUntilDone(Job job, long timeout) throws CobaltException {
        return getAdaptor(job.getScheduler()).jobsAdaptor().waitUntilDone(job, timeout);
    }

    @Override
    public JobStatus waitUntilRunning(Job job, long timeout) throws CobaltException {
        return getAdaptor(job.getScheduler()).jobsAdaptor().waitUntilRunning(job, timeout);
    }

    @Override
    public JobStatus cancelJob(Job job) throws CobaltException {
        return getAdaptor(job.getScheduler()).jobsAdaptor().cancelJob(job);
    }

    @Override
    public Job[] getJobs(Scheduler scheduler, String... queueNames) throws CobaltException {
        return getAdaptor(scheduler).jobsAdaptor().getJobs(scheduler, queueNames);
    }

    @Override
    public Job submitJob(Scheduler scheduler, JobDescription description) throws CobaltException {
        return getAdaptor(scheduler).jobsAdaptor().submitJob(scheduler, description);
    }

    @Override
    public QueueStatus getQueueStatus(Scheduler scheduler, String queueName) throws CobaltException {
        return getAdaptor(scheduler).jobsAdaptor().getQueueStatus(scheduler, queueName);
    }

    @Override
    public QueueStatus[] getQueueStatuses(Scheduler scheduler, String... queueNames) throws CobaltException {
        return getAdaptor(scheduler).jobsAdaptor().getQueueStatuses(scheduler, queueNames);
    }

    @Override
    public Streams getStreams(Job job) throws CobaltException {
        return getAdaptor(job.getScheduler()).jobsAdaptor().getStreams(job);
    }

    @Override
    public String toString() {
        return "JobsEngine [octopusEngine=" + octopusEngine + "]";
    }

}
