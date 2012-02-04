/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ivory.entity.parser;

import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.apache.ivory.Util;
import org.apache.ivory.entity.store.StoreAccessException;
import org.apache.ivory.entity.v0.EntityType;
import org.apache.ivory.entity.v0.cluster.Cluster;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class ClusterEntityParser extends EntityParser<Cluster>{
	
	private static final Logger LOG = Logger
			.getLogger(ProcessEntityParser.class);

	private static final String SCHEMA_FILE = "/schema/cluster/cluster-0.1.xsd";

	protected ClusterEntityParser(EntityType entityType,
			Class<Cluster> clazz) {
		super(entityType, clazz);
	}

	/**
	 * Applying Schema Validation during Unmarshalling Instead of using
	 * Validator class JAXB 2.0 supports this out-of-the-box
	 * 
	 * @throws JAXBException
	 * @throws SAXException
	 */
	@Override
	public Cluster doParse(InputStream xmlStream) throws JAXBException,
			SAXException {

		Cluster clusterDefinitionElement = null;
		Unmarshaller unmarshaller;

		unmarshaller = EntityUnmarshaller.getInstance(this.getEntityType(),
				this.getClazz());
		// Validate against schema
		synchronized (SCHEMA_FILE) {
			Schema schema = Util.getSchema(ClusterEntityParser.class
					.getResource(SCHEMA_FILE));
			unmarshaller.setSchema(schema);
			clusterDefinitionElement = (Cluster) unmarshaller
					.unmarshal(xmlStream);
		}

		return clusterDefinitionElement;
	}

	@Override
	public void applyValidations(Cluster entity)
			throws StoreAccessException, ValidationException {
//		ConfigurationStore store = ConfigurationStore.get();
//		Cluster existingEntity = store.get(EntityType.DATASET,
//				entity.getName());
		// if (existingEntity != null) {
		// throw new ValidationException("Entity: " + entity.getName()
		// + " already submitted");
		// }
		// TODO check if dependent Feed and Datastore exists
		fieldValidations(entity);
	}

	private void fieldValidations(Cluster entity)
			throws ValidationException {
		// TODO

	}

}