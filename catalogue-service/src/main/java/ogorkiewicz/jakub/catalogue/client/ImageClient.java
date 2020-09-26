package ogorkiewicz.jakub.catalogue.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ogorkiewicz.jakub.catalogue.dto.ImageDto;

@FeignClient("watercolors-images")
public interface ImageClient {
    
    @GetMapping("/images/{id}")
    ImageDto getImagesData(@PathVariable("id") Long id);
}
