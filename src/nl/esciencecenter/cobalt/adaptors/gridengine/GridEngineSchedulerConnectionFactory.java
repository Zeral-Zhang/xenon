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
package nl.esciencecenter.cobalt.adaptors.gridengine;

import nl.esciencecenter.cobalt.CobaltException;
import nl.esciencecenter.cobalt.adaptors.scripting.SchedulerConnection;
import nl.esciencecenter.cobalt.adaptors.scripting.SchedulerConnectionFactory;
import nl.esciencecenter.cobalt.adaptors.scripting.ScriptingAdaptor;
import nl.esciencecenter.cobalt.credentials.Credential;
import nl.esciencecenter.cobalt.engine.CobaltEngine;
import nl.esciencecenter.cobalt.engine.CobaltProperties;

/**
 * Simple Factory class to create scheduler connections
 * 
 * @author Niels Drost
 * 
 */
public class GridEngineSchedulerConnectionFactory implements SchedulerConnectionFactory {

    @Override
    public SchedulerConnection newSchedulerConnection(ScriptingAdaptor adaptor, String scheme, String location,
            Credential credential, CobaltProperties properties, CobaltEngine engine) throws CobaltException {
        return new GridEngineSchedulerConnection(adaptor, scheme, location, credential, properties, engine);
    }

}
