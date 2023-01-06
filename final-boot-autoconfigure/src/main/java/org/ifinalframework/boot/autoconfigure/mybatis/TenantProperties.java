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

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * TenantProperties.
 *
 * @author ilikly
 * @version 1.4.3
 * @since 1.4.3
 */
@Setter
@Getter
@ConfigurationProperties(prefix = TenantProperties.PREFIX)
public class TenantProperties {

    final static String PREFIX = "final.data.tenant";
    private Boolean enable = false;

    private String column = "tenant";


}
