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

package org.ifinalframework.boot.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DruidDataSourceFactoryTest.
 *
 * @author likly
 * @version 1.3.0
 * @since 1.3.0
 */
@SpringBootTest
@SpringBootApplication
class DruidDataSourceFactoryTest {

    private final DruidDataSourceFactory druidDataSourceFactory = new DruidDataSourceFactory();

    @Resource
    private Environment environment;
    @Resource
    private DataSourceProperties properties;

    @Test
    void create() throws SQLException {
        final DruidDataSource dataSource = druidDataSourceFactory.create(properties, environment, "spring.datasource");
        assertEquals("SELECT 1", dataSource.getValidationQuery());
    }
}
