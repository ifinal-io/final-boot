package org.ifinalframework.boot.env;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PropertySourceLoadersTest.
 *
 * @author ilikly
 * @version 1.3.0
 * @since 1.3.0
 */
class PropertySourceLoadersTest {

    @Test
    @SneakyThrows
    void load() {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PropertySourceLoadersTest.class);
        PropertySourceLoaders loaders = new PropertySourceLoaders(context);
        List<PropertySource<?>> sources = loaders.load("classpath:application-ha*");
        for (PropertySource<?> source : sources) {
            context.getEnvironment().getPropertySources().addLast(source);
        }
        assertFalse(CollectionUtils.isEmpty(sources));
        assertEquals("SELECT 1",context.getEnvironment().getProperty("spring.datasource.druid.validation-query"));

    }
}