package ogorkiewicz.jakub.catalogue.resource;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.HttpStatus.NO_CONTENT;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import ogorkiewicz.jakub.catalogue.dto.DetailedPaintingDto;
import ogorkiewicz.jakub.catalogue.dto.PageDto;
import ogorkiewicz.jakub.catalogue.exception.ErrorResponse;
import ogorkiewicz.jakub.catalogue.service.PaintingService;

@Api(tags = {"paintings"})
@RestController
@RequestMapping("/paintings")
public class PaintingController {

    private PaintingService paintingService;

    @Autowired
    public PaintingController (PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @ApiOperation(value = "Return page of 50 paintings")
    @GetMapping("/page/{page}")
    public ResponseEntity<PageDto> getPage(@ApiParam(value = "Page number") @PathVariable("page") int page) {
        return ResponseEntity.ok().body(paintingService.getPage(page));
    }

    @ApiOperation(value = "Return details about painting by given id")
    @ApiResponses(
        value = {
            @ApiResponse(
                code = 400,
                message = "Painting with requested id does not exist", 
                response = ErrorResponse.class, 
                examples = @Example(
                    value = 
                        @ExampleProperty(
                            mediaType = APPLICATION_JSON_VALUE,
                            value ="{\n \"errorCode\": \"Painting.NOT_FOUND\",\n \"message\": \"Requested entity not found\"\n}"
                            )
                    )
            )
        }
    )
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DetailedPaintingDto> getPaintingById(@ApiParam(value = "Painting id") @PathVariable("id") Long id) {
        DetailedPaintingDto detailedPaintingDto = paintingService.getPaintingById(id);
        return ResponseEntity.ok().body(detailedPaintingDto);
    }


    // @PutMapping(path = "/{id}")
    // public ResponseEntity<DetailedPaintingDto> updatePainting(@ApiParam(value = "Painting id") @PathVariable("id") Long id) {

    // } 

    @ApiOperation(value = "Delete painting with given id")
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deletePainting(@ApiParam(value = "Painting id") @PathVariable("id") Long id) {
        paintingService.deletePaintingById(id);
        return ResponseEntity.noContent().build();
    }
}