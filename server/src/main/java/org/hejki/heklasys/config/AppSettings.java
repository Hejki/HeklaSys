package org.hejki.heklasys.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppSettings {
    private static final Logger log = LoggerFactory.getLogger(AppSettings.class);

    private int udpPort = 32123;
    private int messageDefaultTimeoutSeconds = 10;

    @PostConstruct
    public void printInfo() {
        if (log.isInfoEnabled()) {
            log.info("Application settings:");
            log.info("  udpPort={}", udpPort);
            log.info("  messageDefaultTimeoutSeconds={}", messageDefaultTimeoutSeconds);
        }
    }
}
