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
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import org.ifinalframework.auto.spring.factory.annotation.SpringFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * LoggerEnvironmentPostProcessor.
 *
 * @author likly
 * @version 1.2.1
 * @see LoggingApplicationListener
 * @since 1.2.1
 */
@Slf4j
@SpringFactory(EnvironmentPostProcessor.class)
public class LoggerEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * 默认的控制台日志格式
     */
    private static final String DEFAULT_LOGGING_PATTERN_CONSOLE = "%clr(%d{${LOG_DATEFORMAT_PATTERN:-yyyy-MM-dd HH:mm:ss.SSS}}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %clr(%-36X{trace}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}";

    /**
     * 默认的文件日志格式
     */
    private static final String DEFAULT_LOGGING_PATTERN_FILE = "%d{${LOG_DATEFORMAT_PATTERN:-yyyy-MM-dd HH:mm:ss.SSS}} ${LOG_LEVEL_PATTERN:-%5p} ${PID:- } --- [%t] %-40.40logger{39} : %-36X{trace} : %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}";

    @Override
    public void postProcessEnvironment(final ConfigurableEnvironment environment, final SpringApplication application) {

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("logging.pattern.console", DEFAULT_LOGGING_PATTERN_CONSOLE);
        properties.put("logging.pattern.file", DEFAULT_LOGGING_PATTERN_FILE);

        environment.getPropertySources()
            .addLast(new MapPropertySource("defaultLoggingProperties", properties));
    }

}
