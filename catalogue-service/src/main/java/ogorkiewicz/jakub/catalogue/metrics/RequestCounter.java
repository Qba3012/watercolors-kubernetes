package ogorkiewicz.jakub.catalogue.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

@Component
public class RequestCounter {
    
    private final String counterName = "api-painting-resource-counter";
    private final String requestTagName = "request";
    private final String endpointTagName = "endpoint";
    private MeterRegistry registry;

    @Autowired
    public RequestCounter(MeterRegistry registry) {
        this.registry = registry;
    }

    public void increment(String requestTagValue, String endpointTagValue) {
        Tag requestTag = new ImmutableTag(requestTagName, requestTagValue);
        Tag endpointTag = new ImmutableTag(endpointTagName, endpointTagValue);
        Counter counter = Counter.builder(counterName).tags(List.of(requestTag, endpointTag)).register(registry);
        counter.increment();
    }

    public void addPaintingToStatistics(Long id, String paintingName) {
        Tag idTag = new ImmutableTag("painting-id", String.valueOf(id));
        Tag nameTag = new ImmutableTag("painting-name", paintingName);
        Counter counter = Counter.builder(counterName).tags(List.of(idTag, nameTag)).register(registry);
        counter.increment();
    }

}