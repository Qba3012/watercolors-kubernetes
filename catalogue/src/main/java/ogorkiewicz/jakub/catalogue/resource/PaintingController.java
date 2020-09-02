package ogorkiewicz.jakub.catalogue.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ogorkiewicz.jakub.catalogue.dto.PageDto;
import ogorkiewicz.jakub.catalogue.service.PaintingService;

@RestController
@RequestMapping("/paintings")
public class PaintingController {

    private PaintingService paintingService;

    @Autowired
    public PaintingController (PaintingService paintingService) {
        this.paintingService = paintingService;
    }
    
    @GetMapping("/{page}")
    public ResponseEntity<PageDto> getHello(@PathVariable("page") int page) {
        return ResponseEntity.ok().body(paintingService.getPage(page));
    }
}