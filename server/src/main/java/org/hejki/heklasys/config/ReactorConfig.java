package org.hejki.heklasys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
@Configuration
@EnableReactor
@ComponentScan("org.hejki.heklasys.consumer.message")
public class ReactorConfig {
    public static final String REACTOR = "rootReactor";
    public static final String REACTOR_REF = "@rootReactor";

    @Bean(name = REACTOR)
    public Reactor rootReactor(Environment env) {
        return Reactors
                .reactor()
                .env(env)
//                .dispatcher(Environment.EVENT_LOOP)
                .get();
    }
}
