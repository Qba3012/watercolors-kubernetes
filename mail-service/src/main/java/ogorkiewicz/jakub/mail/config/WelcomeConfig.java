package ogorkiewicz.jakub.mail.config;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class WelcomeConfig {

    private String template;

    private String subject;
    
}