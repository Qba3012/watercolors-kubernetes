package ogorkiewicz.jakub.catalogue.config;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class CustomCircuitBreakerConfig {
    
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerConfig() {

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
	        .waitDurationInOpenState(Duration.ofMillis(30000))
	        .build();
    	TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
	        .timeoutDuration(Duration.ofSeconds(2))
            .build();
      
      return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .timeLimiterConfig(timeLimiterConfig)
            .circuitBreakerConfig(circuitBreakerConfig)
            .build());
    }
}