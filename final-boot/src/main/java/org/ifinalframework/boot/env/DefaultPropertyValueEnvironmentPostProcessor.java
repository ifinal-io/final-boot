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

package org.ifinalframework.boot.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultPropertyValueEnvironmentPostProcessor.
 *
 * @author likly
 * @version 1.2.1
 * @since 1.2.1
 */
@Deprecated
public interface DefaultPropertyValueEnvironmentPostProcessor extends EnvironmentPostProcessor {

    void initDefaultPropertyValues(Map<String, Object> properties);

    default String getPropertySourceName() {
        return getClass().getSimpleName();
    }

    @Override
    default void postProcessEnvironment(final ConfigurableEnvironment environment,
        final SpringApplication application) {
        final Logger logger = LoggerFactory.getLogger(getClass());
        final Map<String, Object> defaultPropertyValues = new LinkedHashMap<>();
        initDefaultPropertyValues(defaultPropertyValues);

        final Map<String, Object> map = defaultPropertyValues.entrySet()
            .stream()
            .filter(it -> !environment.containsProperty(it.getKey()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        if (map.size() > 0) {
            if (logger.isInfoEnabled()) {
                for (final Entry<String, Object> entry : map.entrySet()) {
                    logger.info("{}={}", entry.getKey(), entry.getValue());
                }
            }
            MapPropertySource propertySource = new MapPropertySource(getPropertySourceName(), map);
            environment.getPropertySources().addLast(propertySource);
        }

    }

}
