package ogorkiewicz.jakub.mail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "newsletter")
public class NewsletterConfig {

    private String subject;
    
    private String email;

    private String host;

    private String home;

    private String server;
    
    private WelcomeConfig welcome;
}