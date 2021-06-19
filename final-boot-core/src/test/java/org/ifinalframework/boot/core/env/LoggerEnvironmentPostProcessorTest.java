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

package org.ifinalframework.boot.core.env;

import static org.mockito.Mockito.*;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

/**
 * LoggerEnvironmentPostProcessorTest.
 *
 * @author likly
 * @version 1.2.1
 * @since 1.2.1
 */
@ExtendWith(MockitoExtension.class)
class LoggerEnvironmentPostProcessorTest {

    @InjectMocks
    private LoggerEnvironmentPostProcessor postProcessor;

    @Mock
    private ConfigurableEnvironment environment;

    @Mock
    private MutablePropertySources mutablePropertySources;

    @Test
    void postProcessEnvironment() {

        when(environment.containsProperty(anyString())).thenReturn(false);

        when(environment.getPropertySources()).thenReturn(mutablePropertySources);

        postProcessor.postProcessEnvironment(environment, null);

        verify(mutablePropertySources, atLeastOnce()).addLast(any());

    }

}
