package com.github.ldriscoll.ektorplucene;

/**
 * Copyright 2011 Luke Driscoll
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpResponse;
import org.ektorp.http.ResponseCallback;
import org.ektorp.http.RestTemplate;
import org.ektorp.http.StdResponseHandler;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.util.Assert;

/**
 * Simple override of the base StdCouchDbConnector that allows us to run queries against couchdb
 */
public class LuceneAwareCouchDbConnector extends StdCouchDbConnector {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LuceneAwareCouchDbConnector(String databaseName, CouchDbInstance dbInstance) {
        super(databaseName, dbInstance, new EktorpLuceneObjectMapperFactory());
        this.restTemplate = new RestTemplate(dbInstance.getConnection());
        this.objectMapper = new EktorpLuceneObjectMapperFactory().createObjectMapper();

    }

    public LuceneResult queryLucene(LuceneQuery query) {
        Assert.notNull(query, "query cannot be null");
        query.setDbPath(this.path());
        ResponseCallback<LuceneResult> rh = new StdResponseHandler<LuceneResult>() {

            public LuceneResult success(HttpResponse hr) throws Exception {
                return objectMapper.readValue(hr.getContent(), LuceneResult.class);
            }

        };
        return restTemplate.get(query.buildQuery(), rh);
    }

    public CustomLuceneResult queryLucene(LuceneQuery query, final TypeReference type) {
        Assert.notNull(query, "query cannot be null");
        query.setDbPath(this.path());
        ResponseCallback<CustomLuceneResult> rh = new StdResponseHandler<CustomLuceneResult>() {

            public CustomLuceneResult success(HttpResponse hr) throws Exception {
                return (CustomLuceneResult) objectMapper.readValue(hr.getContent(), type);
            }

        };
        return restTemplate.get(query.buildQuery(), rh);
    }
}
