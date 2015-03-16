package org.hejki.heklasys;

import org.hejki.spring.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
@Configuration
@ComponentScan({
        "org.hejki.heklasys"
//        "org.hejki.heklasys.config",
//        "org.hejki.heklasys.service",
//        "org.hejki.heklasys.rest",
})
@EnableJdbcRepositories()
@EnableAutoConfiguration
public class Application extends RepositoryRestMvcAutoConfiguration {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);

        app.run(args);
    }
}
