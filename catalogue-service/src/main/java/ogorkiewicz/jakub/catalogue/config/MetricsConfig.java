package ogorkiewicz.jakub.catalogue.config;

import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    
    /**
     *  Http trace
     */

    @Bean
    public InMemoryHttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }

}
