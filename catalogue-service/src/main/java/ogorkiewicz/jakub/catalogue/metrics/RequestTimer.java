package ogorkiewicz.jakub.catalogue.metrics;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;

@Component
public class RequestTimer {
    
    private final String timerName = "api-painting-resource-timer";
    private final String timerDescription = "Painting resource timer";
    private final String requestTagName = "request";
    private final String endpointTagName = "endpoint";
    private MeterRegistry registry;

    @Autowired
    public RequestTimer(MeterRegistry registry) {
        this.registry = registry;
    }

    public Timer getTimer(String requestTagValue, String endpointTagValue) {
        Tag requestTag = new ImmutableTag(requestTagName, requestTagValue);
        Tag endpointTag = new ImmutableTag(endpointTagName, endpointTagValue);
        return Timer.builder(timerName)
                                .description(timerDescription)
                                .tags(List.of(requestTag, endpointTag))
                                .register(registry);
    }

}