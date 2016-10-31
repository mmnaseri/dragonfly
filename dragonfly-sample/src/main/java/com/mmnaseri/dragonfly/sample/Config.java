/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.sample;

import com.mmnaseri.dragonfly.dialect.DatabaseDialect;
import com.mmnaseri.dragonfly.dialect.impl.Mysql5Dialect;
import com.mmnaseri.dragonfly.runtime.analysis.JpaApplicationDesignAdvisor;
import com.mmnaseri.dragonfly.runtime.config.JpaDataConfiguration;
import com.mmnaseri.dragonfly.runtime.ext.audit.api.UserContext;
import com.mmnaseri.dragonfly.runtime.ext.audit.impl.AuditInterceptor;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/13 AD, 16:39)
 */
@Configuration
@Import(JpaDataConfiguration.class)
@ComponentScan("com.mmnaseri.dragonfly.sample")
public class Config {
    
    @Bean
    public DatabaseDialect databaseDialect() {
        return new Mysql5Dialect();
    }
    
    @Bean
    public DataSource dataSource(DatabaseDialect dialect, @Value("${db.username}") String username, @Value("${db.password}") String password, @Value("${db.database}") String database) {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(dialect.getDriverClassName());
        dataSource.setUrl("jdbc:mysql://localhost/" + database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaxActive(200);
        dataSource.setMaxIdle(100);
        dataSource.setMaxWait(1500);
        return dataSource;
    }

    @Bean
    public JpaApplicationDesignAdvisor designAdvisor() {
        return new JpaApplicationDesignAdvisor();
    }

    @Bean
    public PropertyPlaceholderConfigurer placeholderConfigurer() {
        final PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("db.properties"));
        return configurer;
    }

    @Bean
    public AuditInterceptor defaultAuditInterceptor(UserContext userContext) {
        return new AuditInterceptor(userContext);
    }

}
