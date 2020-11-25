package ogorkiewicz.jakub.image.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ogorkiewicz.jakub.image.dto.ImageDto;

@FeignClient(name = "watercolors-catalogue", path = "/api/v1/catalogue/paintings")
public interface PaintingClient {
    
    @PutMapping("/{id}")
    ResponseEntity<?> sendImagesInfoToCatalogue(@PathVariable("id") Long id, @RequestBody List<ImageDto> imageDtos);
}
