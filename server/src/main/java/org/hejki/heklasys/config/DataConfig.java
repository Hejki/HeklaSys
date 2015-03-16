package org.hejki.heklasys.config;

import org.hejki.spring.data.jdbc.repository.config.EnableJdbcRepositories;
import org.hejki.spring.data.jdbc.repository.sql.PostgreSqlGenerator;
import org.hejki.spring.data.jdbc.repository.sql.SqlGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Configuration
@EnableJdbcRepositories
@ComponentScan(basePackages = "org.hejki.heklasys.repository")
public class DataConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SqlGenerator jdbcSqlGenerator() {
        return new PostgreSqlGenerator();
    }
}