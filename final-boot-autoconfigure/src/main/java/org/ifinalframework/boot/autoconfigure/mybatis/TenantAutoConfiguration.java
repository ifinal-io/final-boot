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

package org.ifinalframework.boot.autoconfigure.mybatis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import org.ifinalframework.data.core.TenantSupplier;
import org.ifinalframework.data.core.TenantTableService;
import org.ifinalframework.data.mybatis.core.FinalTenantLineHandler;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;

/**
 * TenantAutoConfiguration.
 *
 * @author ilikly
 * @version 1.4.3
 * @since 1.4.3
 */
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = TenantProperties.PREFIX, name = "enable", havingValue = "true")
@ConditionalOnBean(TenantSupplier.class)
@EnableConfigurationProperties(TenantProperties.class)
public class TenantAutoConfiguration {
    private final TenantProperties tenantProperties;

    @Bean
    @ConditionalOnMissingBean(TenantLineHandler.class)
    public TenantLineHandler tenantLineHandler(TenantSupplier<Expression> tenantSupplier, TenantTableService tenantTableService) {
        return new FinalTenantLineHandler(tenantProperties.getColumn(), tenantSupplier, tenantTableService);
    }

    @Bean
    @ConditionalOnBean(TenantLineHandler.class)
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantLineHandler tenantLineHandler) {
        return new TenantLineInnerInterceptor(tenantLineHandler);
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 1000)
    public MybatisPlusInterceptor mybatisPlusInterceptor(@Autowired List<InnerInterceptor> interceptors) {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.setInterceptors(interceptors);
        return mybatisPlusInterceptor;
    }


}
