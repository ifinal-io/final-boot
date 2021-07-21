/*
 * Copyright 2020-2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ifinalframework.boot.autoconfigure.query;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import org.ifinalframework.query.Query;

import lombok.extern.slf4j.Slf4j;

/**
 * QueryAutoConfiguration.
 *
 * @author likly
 * @version 1.2.1
 * @since 1.2.1
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Query.class)
@EnableConfigurationProperties(QueryProperties.class)
public class QueryAutoConfiguration implements InitializingBean {

    private final QueryProperties properties;

    public QueryAutoConfiguration(QueryProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
