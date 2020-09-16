package ogorkiewicz.jakub.catalogue.resource;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static ogorkiewicz.jakub.catalogue.resource.PaintingController.PAINTING_PATH;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import ogorkiewicz.jakub.catalogue.exception.BadRequestException;
import ogorkiewicz.jakub.catalogue.exception.ErrorCode;
import ogorkiewicz.jakub.catalogue.exception.ErrorResponse;
import ogorkiewicz.jakub.catalogue.model.Painting;
import ogorkiewicz.jakub.catalogue.service.PaintingService;

@Api(tags = {"paintings"})
@RestController
@RequestMapping(PAINTING_PATH)
public class PaintingController {

    public static final String PAINTING_PATH = "/paintings";

    private PaintingService paintingService;

    @Autowired
    public PaintingController (PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    /**
     * GET page 
     */

    @ApiOperation(value = "Return page of 50 paintings")
    @GetMapping("/page/{page}")
    public ResponseEntity<PageDto> getPage(@ApiParam(value = "Page number") @PathVariable("page") int page) {
        return ResponseEntity.ok().body(paintingService.getPage(page));
    }

    /**
     * GET painting details 
     */

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

    
    /**
     * POST - create new painting record
     */

    @ApiOperation(value = "Create painting")
    @ApiResponses(
        value = {
            @ApiResponse(
                code = 400,
                message = "Id must be null", 
                response = ErrorResponse.class, 
                examples = @Example(
                    value = 
                        @ExampleProperty(
                            mediaType = APPLICATION_JSON_VALUE,
                            value ="{\n \"errorCode\": \"Painting.ID_NOT_NULL\",\n \"message\": \"Id must be null\"\n}"
                            )
                    )
            )
        }
    )
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DetailedPaintingDto> createPainting(@RequestBody DetailedPaintingDto painting) {
        if(painting.getId() != null) {
            throw new BadRequestException(ErrorCode.ID_NOT_NULL, Painting.class);
        }
        DetailedPaintingDto newPaintingDto = paintingService.savePainting(painting);
        return ResponseEntity.ok().body(newPaintingDto);
    } 

    /**
     * PUT - update painting details
     */

    @ApiOperation(value = "Update painting")
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
    @PutMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DetailedPaintingDto> updatePainting(@RequestBody DetailedPaintingDto painting) {
        DetailedPaintingDto updatedPaintingDto = paintingService.updatePainting(painting);
        return ResponseEntity.ok().body(updatedPaintingDto);
    } 

    
    /**
     * DELETE - delete painting by id
     */
    
    @ApiOperation(value = "Delete painting with given id")
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deletePainting(@ApiParam(value = "Painting id") @PathVariable("id") Long id) {
        paintingService.deletePaintingById(id);
        return ResponseEntity.noContent().build();
    }
}