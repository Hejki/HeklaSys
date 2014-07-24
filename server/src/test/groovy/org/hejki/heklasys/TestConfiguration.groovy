package org.hejki.heklasys

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator

import javax.sql.DataSource

@Configuration
public class TestConfiguration {
    @Value('${jdbc.url}') String url
    @Value('${jdbc.username}') String user
    @Value('${jdbc.password}') String pass

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(url, user, pass)
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource())
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        def initializer = new DataSourceInitializer()
        def populator = new ResourceDatabasePopulator()

        populator.scripts = [new ClassPathResource("test_data.sql")]
        initializer.setDataSource(dataSource())
        initializer.setDatabasePopulator(populator)
        return initializer
    }

    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        def configurer = new PropertyPlaceholderConfigurer()

        configurer.searchSystemEnvironment = true

        def configPath = System.properties['configPath']
        if (!configPath) { // only from IDE
            configPath = new File('.', 'app-config.xml')
            if (!configPath.exists()) {
                throw new IllegalStateException("Cannot load application context, because config file "
                    + configPath.absolutePath + " does not exist.")
            }
        } else if (!new File(configPath).exists()) {
            throw new IllegalStateException("Cannot load application context, because config file "
                    + configPath + " does not exist.")
        }

        configurer.location = new FileSystemResource(configPath)
        return configurer
    }
}
