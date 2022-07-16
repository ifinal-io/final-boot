/*
 * Copyright 2020-2022 the original author or authors.
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

package org.springframework.boot.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.ActiveProfiles;

import org.ifinalframework.auto.spring.factory.annotation.SpringFactory;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * SpringApplicationEnvironmentTest.
 *
 * @author ilikly
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@ActiveProfiles("haha")
@SpringBootTest
@SpringBootApplication
@SpringFactory(EnvironmentPostProcessor.class)
public class SpringApplicationEnvironmentTest implements EnvironmentPostProcessor {

    @Value("${spring.application.name}")
    private String springApplicationName;

    @Test
    void test() {
        logger.info("spring.application.name={}", springApplicationName);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            logger.info("name={}", propertySource.getName());
        }
    }
}


