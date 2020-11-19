package ogorkiewicz.jakub.catalogue.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "watercolors-images", path = "/api/v1/images")
public interface ImageClient {
    
    @DeleteMapping("/paintings/{id}/all")
    ResponseEntity<?> deletePaintingsImages(@PathVariable("id") Long id);
}
