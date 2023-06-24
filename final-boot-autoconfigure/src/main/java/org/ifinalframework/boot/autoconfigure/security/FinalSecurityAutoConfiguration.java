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

package org.ifinalframework.boot.autoconfigure.security;

import lombok.extern.slf4j.Slf4j;
import org.ifinalframework.boot.autoconfigure.web.cors.CorsProperties;
import org.ifinalframework.core.result.R;
import org.ifinalframework.core.result.Result;
import org.ifinalframework.json.Json;
import org.ifinalframework.security.config.HttpSecurityConfigurer;
import org.ifinalframework.security.web.authentication.ResultAuthenticationFailureHandler;
import org.ifinalframework.security.web.authentication.ResultAuthenticationSuccessHandler;
import org.ifinalframework.security.web.authentication.www.BearerAuthenticationFilter;
import org.ifinalframework.util.CompositeProxies;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * SecurityAutoConfiguration.
 *
 * <h2>Http Basic</h2>
 *
 *
 * <pre class="code">
 *      http.httpBasic();
 * </pre>
 *
 * @author ilikly
 * @version 1.5.0
 * @see org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
 * @see org.springframework.security.web.authentication.www.BasicAuthenticationFilter
 * @see org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter
 * @since 1.5.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({CorsProperties.class, SecurityProperties.class})
@ConditionalOnClass(DefaultAuthenticationEventPublisher.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class FinalSecurityAutoConfiguration {

    private final HttpSecurityConfigurer httpSecurityConfigurer;

    public FinalSecurityAutoConfiguration(ObjectProvider<List<HttpSecurityConfigurer>> httpSecurityConfigurer) {
        this.httpSecurityConfigurer = CompositeProxies.composite(HttpSecurityConfigurer.class, httpSecurityConfigurer.getIfAvailable());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        final PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        if (delegatingPasswordEncoder instanceof DelegatingPasswordEncoder) {
            ((DelegatingPasswordEncoder) delegatingPasswordEncoder).setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        }
        return delegatingPasswordEncoder;
    }

    @Bean
    public SecurityFilterChain corsSecurityFilterChain(ApplicationContext applicationContext, HttpSecurity http, CorsProperties corsProperties, SecurityProperties securityProperties) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        if (Objects.nonNull(corsProperties.getAllowedHeaders())) {
            corsConfiguration.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders()));
        }
        if (Objects.nonNull(corsProperties.getAllowedMethods())) {
            corsConfiguration.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods()));
        }
        if (Objects.nonNull(corsProperties.getAllowedOrigins())) {
            corsConfiguration.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins()));
        }
        if (Objects.nonNull(corsProperties.getAllowCredentials())) {
            corsConfiguration.setAllowCredentials(corsProperties.getAllowCredentials());
        }
        if (Objects.nonNull(corsProperties.getMaxAge())) {
            corsConfiguration.setMaxAge(corsProperties.getMaxAge());
        }
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsProperties.getMapping(), corsConfiguration);
        http.cors().configurationSource(source);


        session(http, securityProperties.getSession());


        basic(http, securityProperties.getBasic());
        rememberMe(http, securityProperties.getRememberMe());
        anonymous(http, securityProperties.getAnonymous());

        http.csrf().disable();

        http.logout().logoutUrl(securityProperties.getLogout().getUrl());

        applicationContext.getBeanProvider(BearerAuthenticationFilter.class).ifAvailable(filter -> {
            logger.info("addFilterBefore UsernamePasswordAuthenticationFilter: {}", filter.getClass().getName());
            http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        });

        applicationContext.getBeanProvider(UsernamePasswordAuthenticationFilter.class).ifAvailable(filter -> {
            logger.info("addFilterAt UsernamePasswordAuthenticationFilter: {}", filter.getClass().getName());
            http.addFilterAt(filter, UsernamePasswordAuthenticationFilter.class);
        });


        FormLoginConfigurer<HttpSecurity> formLoginConfigurer = http.formLogin().loginPage("/api/login").permitAll();
        applicationContext.getBeanProvider(ResultAuthenticationSuccessHandler.class).ifAvailable(formLoginConfigurer::successHandler);
        applicationContext.getBeanProvider(ResultAuthenticationFailureHandler.class).ifAvailable(formLoginConfigurer::failureHandler);

        httpSecurityConfigurer.authorizeRequests(http.authorizeRequests());

        http.exceptionHandling().accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                final Result<Object> result = R.failure(403, "您没有权限访问：" + request.getMethod() + " " + request.getRequestURI());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(Json.toJson(result));
            }
        });


        return http.build();
    }

    private void session(HttpSecurity http, SecurityProperties.SessionProperties sessionProperties) throws Exception {
        final SessionManagementConfigurer<HttpSecurity> sessionManagement = http.sessionManagement();
        if (Objects.nonNull(sessionProperties.getCreationPolicy())) {
            sessionManagement.sessionCreationPolicy(sessionProperties.getCreationPolicy());
        }

    }

    private void basic(HttpSecurity http, SecurityProperties.BasicProperties basicProperties) throws Exception {
        if (Objects.isNull(basicProperties) || !Boolean.TRUE.equals(basicProperties.getEnable())) {
            return;
        }
        http.httpBasic();
    }

    private void rememberMe(HttpSecurity http, SecurityProperties.RememberMeProperties rememberMeProperties) throws Exception {
        if (Objects.isNull(rememberMeProperties) || !Boolean.TRUE.equals(rememberMeProperties.getEnable())) {
            return;
        }
        http.rememberMe().alwaysRemember(Boolean.TRUE.equals(rememberMeProperties.getAlwaysRemember()));

    }

    private void anonymous(HttpSecurity http, SecurityProperties.AnonymousProperties anonymousProperties) throws Exception {
        if (Objects.isNull(anonymousProperties) || !Boolean.TRUE.equals(anonymousProperties.getEnable())) {
            return;
        }
        http.anonymous();
    }


}
