/*
 * Copyright 2020-2023 the original author or authors.
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

package org.ifinalframework.boot.autoconfigure.validation;

import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.validation.beanvalidation.FilteredMethodValidationPostProcessor;
import org.springframework.boot.validation.beanvalidation.MethodValidationExcludeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import org.ifinalframework.validation.ValidationGroupsProvider;
import org.ifinalframework.validation.beanvalidation.FinalFilteredMethodValidationPostProcessor;

/**
 * FinalValidationAutoConfiguration.
 *
 * @author ilikly
 * @version 1.5.0
 * @see ValidationAutoConfiguration
 * @since 1.5.0
 */
@AutoConfiguration
@ConditionalOnBean(ValidationGroupsProvider.class)
@ConditionalOnClass(ExecutableValidator.class)
@ConditionalOnResource(resources = "classpath:META-INF/services/javax.validation.spi.ValidationProvider")
@AutoConfigureBefore(ValidationAutoConfiguration.class)
public class FinalValidationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    public static MethodValidationPostProcessor methodValidationPostProcessor(Environment environment,
                                                                              @Lazy Validator validator,
                                                                              ObjectProvider<MethodValidationExcludeFilter> excludeFilters,
                                                                              ValidationGroupsProvider validationGroupsProvider) {
        FilteredMethodValidationPostProcessor processor = new FinalFilteredMethodValidationPostProcessor(
                excludeFilters.orderedStream(), validationGroupsProvider);
        boolean proxyTargetClass = environment.getProperty("spring.aop.proxy-target-class", Boolean.class, true);
        processor.setProxyTargetClass(proxyTargetClass);
        processor.setValidator(validator);
        return processor;
    }
}
