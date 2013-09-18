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

package nl.esciencecenter.cobalt.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import nl.esciencecenter.cobalt.IncompatibleVersionException;
import nl.esciencecenter.cobalt.InvalidCredentialException;
import nl.esciencecenter.cobalt.InvalidLocationException;
import nl.esciencecenter.cobalt.InvalidPropertyException;
import nl.esciencecenter.cobalt.NoSuchCobaltException;
import nl.esciencecenter.cobalt.CobaltException;
import nl.esciencecenter.cobalt.CobaltRuntimeException;
import nl.esciencecenter.cobalt.UnknownPropertyException;
import nl.esciencecenter.cobalt.adaptors.ssh.ConnectionLostException;
import nl.esciencecenter.cobalt.adaptors.ssh.EndOfFileException;
import nl.esciencecenter.cobalt.adaptors.ssh.NotConnectedException;
import nl.esciencecenter.cobalt.adaptors.ssh.PermissionDeniedException;
import nl.esciencecenter.cobalt.adaptors.ssh.UnsupportedIOOperationException;
import nl.esciencecenter.cobalt.engine.PropertyTypeException;
import nl.esciencecenter.cobalt.engine.util.BadParameterException;
import nl.esciencecenter.cobalt.engine.util.CommandNotFoundException;
import nl.esciencecenter.cobalt.files.AttributeNotSupportedException;
import nl.esciencecenter.cobalt.files.DirectoryNotEmptyException;
import nl.esciencecenter.cobalt.files.IllegalSourcePathException;
import nl.esciencecenter.cobalt.files.IllegalTargetPathException;
import nl.esciencecenter.cobalt.files.InvalidCopyOptionsException;
import nl.esciencecenter.cobalt.files.InvalidOpenOptionsException;
import nl.esciencecenter.cobalt.files.InvalidResumeTargetException;
import nl.esciencecenter.cobalt.files.NoSuchCopyException;
import nl.esciencecenter.cobalt.files.NoSuchPathException;
import nl.esciencecenter.cobalt.files.PathAlreadyExistsException;
import nl.esciencecenter.cobalt.jobs.IncompleteJobDescriptionException;
import nl.esciencecenter.cobalt.jobs.InvalidJobDescriptionException;
import nl.esciencecenter.cobalt.jobs.JobCanceledException;
import nl.esciencecenter.cobalt.jobs.NoSuchJobException;
import nl.esciencecenter.cobalt.jobs.NoSuchQueueException;
import nl.esciencecenter.cobalt.jobs.NoSuchSchedulerException;
import nl.esciencecenter.cobalt.jobs.UnsupportedJobDescriptionException;

import org.junit.Test;

/**
 * @author Jason Maassen <J.Maassen@esciencecenter.nl>
 * 
 */
public class ExceptionsTest {

    private void testException(Exception e, String name, String message, Throwable cause) {

        if (name == null) {
            assertEquals(message, e.getMessage());
        } else {
            assertEquals(name + " adaptor: " + message, e.getMessage());
        }
        assertTrue(e.getCause() == cause);
    }

    private void testException(Exception e, Throwable cause) {
        testException(e, "name", "message", cause);
    }

    private void testException(Exception e) {
        testException(e, "name", "message", null);
    }

    @Test
    public void testOctopusException1() throws Exception {
        testException(new CobaltException("name", "message"));
    }

    @Test
    public void testOctopusException2() throws Exception {
        Throwable t = new Throwable();
        testException(new CobaltException("name", "message", t), t);
    }

    @Test
    public void testOctopusException3() throws Exception {
        testException(new CobaltException(null, "message"), null, "message", null);
    }

    @Test
    public void testOctopusRuntimeException1() throws Exception {
        testException(new CobaltRuntimeException("name", "message"));
    }

    @Test
    public void testOctopusRuntimeException2() throws Exception {
        Throwable t = new Throwable();
        testException(new CobaltRuntimeException("name", "message", t), t);
    }

    @Test
    public void testOctopusIOException3() throws Exception {
        testException(new CobaltException(null, "message"), null, "message", null);
    }

    @Test
    public void testOctopusIOException1() throws Exception {
        testException(new CobaltException("name", "message"));
    }

    @Test
    public void testOctopusIOException2() throws Exception {
        Throwable t = new Throwable();
        testException(new CobaltException("name", "message", t), t);
    }

    @Test
    public void testOctopusRuntimeException3() throws Exception {
        testException(new CobaltRuntimeException(null, "message"), null, "message", null);
    }

    @Test
    public void testAttributeNotSupportedException1() throws Exception {
        testException(new AttributeNotSupportedException("name", "message"));
    }

    @Test
    public void testAttributeNotSupportedException2() throws Exception {
        Throwable t = new Throwable();
        testException(new AttributeNotSupportedException("name", "message", t), t);
    }

    @Test
    public void testBadParameterException1() throws Exception {
        testException(new BadParameterException("name", "message"));
    }

    @Test
    public void testBadParameterException2() throws Exception {
        Throwable t = new Throwable();
        testException(new BadParameterException("name", "message", t), t);
    }

    @Test
    public void testCommandNotFoundException1() throws Exception {
        testException(new CommandNotFoundException("name", "message"));
    }

    @Test
    public void testCommandNotFoundException2() throws Exception {
        Throwable t = new Throwable();
        testException(new CommandNotFoundException("name", "message", t), t);
    }

    @Test
    public void testConnectionLostException1() throws Exception {
        testException(new ConnectionLostException("name", "message"));
    }

    @Test
    public void testConnectionLostException2() throws Exception {
        Throwable t = new Throwable();
        testException(new ConnectionLostException("name", "message", t), t);
    }

    @Test
    public void testDirectoryIteratorException1() throws Exception {
        testException(new InvalidCopyOptionsException("name", "message"));
    }

    @Test
    public void testDirectoryIteratorException2() throws Exception {
        Throwable t = new Throwable();
        testException(new InvalidCopyOptionsException("name", "message", t), t);
    }

    @Test
    public void testDirectoryNotEmptyException1() throws Exception {
        testException(new DirectoryNotEmptyException("name", "message"));
    }

    @Test
    public void testDirectoryNotEmptyException2() throws Exception {
        Throwable t = new Throwable();
        testException(new DirectoryNotEmptyException("name", "message", t), t);
    }

    @Test
    public void testEndOfFileException1() throws Exception {
        testException(new EndOfFileException("name", "message"));
    }

    @Test
    public void testEndOfFileException2() throws Exception {
        Throwable t = new Throwable();
        testException(new EndOfFileException("name", "message", t), t);
    }

    @Test
    public void testFileAlreadyExistsException1() throws Exception {
        testException(new PathAlreadyExistsException("name", "message"));
    }

    @Test
    public void testFileAlreadyExistsException2() throws Exception {
        Throwable t = new Throwable();
        testException(new PathAlreadyExistsException("name", "message", t), t);
    }

    @Test
    public void testIllegalSourcePathException1() throws Exception {
        testException(new IllegalSourcePathException("name", "message"));
    }

    @Test
    public void testIllegalSourcePathException2() throws Exception {
        Throwable t = new Throwable();
        testException(new IllegalSourcePathException("name", "message", t), t);
    }

    @Test
    public void testIllegalTargetPathException1() throws Exception {
        testException(new IllegalTargetPathException("name", "message"));
    }

    @Test
    public void testIllegalTargetPathException2() throws Exception {
        Throwable t = new Throwable();
        testException(new IllegalTargetPathException("name", "message", t), t);
    }

    @Test
    public void testIncompatibleVersionException1() throws Exception {
        testException(new IncompatibleVersionException("name", "message"));
    }

    @Test
    public void testIncompatibleVersionException2() throws Exception {
        Throwable t = new Throwable();
        testException(new IncompatibleVersionException("name", "message", t), t);
    }

    @Test
    public void testIncompleteJobDescriptionException1() throws Exception {
        testException(new IncompleteJobDescriptionException("name", "message"));
    }

    @Test
    public void testIncompleteJobDescriptionException2() throws Exception {
        Throwable t = new Throwable();
        testException(new IncompleteJobDescriptionException("name", "message", t), t);
    }

    @Test
    public void testInvalidCredentialException1() throws Exception {
        testException(new InvalidCredentialException("name", "message"));
    }

    @Test
    public void testInvalidCredentialException2() throws Exception {
        Throwable t = new Throwable();
        testException(new InvalidCredentialException("name", "message", t), t);
    }

    @Test
    public void testInvalidDataException1() throws Exception {
        testException(new InvalidResumeTargetException("name", "message"));
    }

    @Test
    public void testInvalidDataException2() throws Exception {
        Throwable t = new Throwable();
        testException(new InvalidResumeTargetException("name", "message", t), t);
    }

    @Test
    public void testInvalidJobDescriptionException1() throws Exception {
        testException(new InvalidJobDescriptionException("name", "message"));
    }

    @Test
    public void testInvalidJobDescriptionException2() throws Exception {
        Throwable t = new Throwable();
        testException(new InvalidJobDescriptionException("name", "message", t), t);
    }

    @Test
    public void testInvalidLocationException1() throws Exception {
        testException(new InvalidLocationException("name", "message"));
    }

    @Test
    public void testInvalidLocationException2() throws Exception {
        Throwable t = new Throwable();
        testException(new InvalidLocationException("name", "message", t), t);
    }

    @Test
    public void testInvalidOpenOptionsException1() throws Exception {
        testException(new InvalidOpenOptionsException("name", "message"));
    }

    @Test
    public void testInvalidOpenOptionsException2() throws Exception {
        Throwable t = new Throwable();
        testException(new InvalidOpenOptionsException("name", "message", t), t);
    }

    @Test
    public void testInvalidPropertyException1() throws Exception {
        testException(new InvalidPropertyException("name", "message"));
    }

    @Test
    public void testInvalidPropertyException2() throws Exception {
        Throwable t = new Throwable();
        testException(new InvalidPropertyException("name", "message", t), t);
    }

    @Test
    public void testNoSuchCopyException1() throws Exception {
        testException(new NoSuchCopyException("name", "message"));
    }

    @Test
    public void testNoSuchCopyException2() throws Exception {
        Throwable t = new Throwable();
        testException(new NoSuchCopyException("name", "message", t), t);
    }

    @Test
    public void testNoSuchFileException1() throws Exception {
        testException(new NoSuchPathException("name", "message"));
    }

    @Test
    public void testNoSuchFileException2() throws Exception {
        Throwable t = new Throwable();
        testException(new NoSuchPathException("name", "message", t), t);
    }

    @Test
    public void testNoSuchJobException1() throws Exception {
        testException(new NoSuchJobException("name", "message"));
    }

    @Test
    public void testNoSuchJobException2() throws Exception {
        Throwable t = new Throwable();
        testException(new NoSuchJobException("name", "message", t), t);
    }

    @Test
    public void testNoSuchOctopusException1() throws Exception {
        testException(new NoSuchCobaltException("name", "message"));
    }

    @Test
    public void testNoSuchOctopusException2() throws Exception {
        Throwable t = new Throwable();
        testException(new NoSuchCobaltException("name", "message", t), t);
    }

    @Test
    public void testNoSuchQueueException1() throws Exception {
        testException(new NoSuchQueueException("name", "message"));
    }

    @Test
    public void testNoSuchQueueException2() throws Exception {
        Throwable t = new Throwable();
        testException(new NoSuchQueueException("name", "message", t), t);
    }

    @Test
    public void testNoSuchSchedulerException1() throws Exception {
        testException(new NoSuchSchedulerException("name", "message"));
    }

    @Test
    public void testNoSuchSchedulerException2() throws Exception {
        Throwable t = new Throwable();
        testException(new NoSuchSchedulerException("name", "message", t), t);
    }

    @Test
    public void testNotConnectedException1() throws Exception {
        testException(new NotConnectedException("name", "message"));
    }

    @Test
    public void testNotConnectedException2() throws Exception {
        Throwable t = new Throwable();
        testException(new NotConnectedException("name", "message", t), t);
    }

    @Test
    public void testPermissionDeniedException1() throws Exception {
        testException(new PermissionDeniedException("name", "message"));
    }

    @Test
    public void testPermissionDeniedException2() throws Exception {
        Throwable t = new Throwable();
        testException(new PermissionDeniedException("name", "message", t), t);
    }

    @Test
    public void testUnknownPropertyException1() throws Exception {
        testException(new UnknownPropertyException("name", "message"));
    }

    @Test
    public void testUnknownPropertyException2() throws Exception {
        Throwable t = new Throwable();
        testException(new UnknownPropertyException("name", "message", t), t);
    }

    @Test
    public void testUnsupportedIOOperationException1() throws Exception {
        testException(new UnsupportedIOOperationException("name", "message"));
    }

    @Test
    public void testUnsupportedIOOperationException2() throws Exception {
        Throwable t = new Throwable();
        testException(new UnsupportedIOOperationException("name", "message", t), t);
    }

    @Test
    public void testUnsupportedJobDescriptionException1() throws Exception {
        testException(new UnsupportedJobDescriptionException("name", "message"));
    }

    @Test
    public void testUnsupportedJobDescriptionException2() throws Exception {
        Throwable t = new Throwable();
        testException(new UnsupportedJobDescriptionException("name", "message", t), t);
    }

    @Test
    public void testUnsupportedOperationException1() throws Exception {
        testException(new InvalidCopyOptionsException("name", "message"));
    }

    @Test
    public void testUnsupportedOperationException2() throws Exception {
        Throwable t = new Throwable();
        testException(new InvalidCopyOptionsException("name", "message", t), t);
    }

    @Test
    public void testJobCanceledException1() throws Exception {
        testException(new JobCanceledException("name", "message"));
    }

    @Test
    public void testJobCanceledException2() throws Exception {
        Throwable t = new Throwable();
        testException(new JobCanceledException("name", "message", t), t);
    }

    @Test
    public void testPropertyTypeException1() throws Exception {
        testException(new PropertyTypeException("name", "message"));
    }
    
    @Test
    public void testPropertyTypeException2() throws Exception {
        Throwable t = new Throwable();
        testException(new PropertyTypeException("name", "message", t), t);
    }

    
}
