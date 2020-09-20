package ogorkiewicz.jakub.images.resource;

import static ogorkiewicz.jakub.images.resource.ImageController.IMAGE_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.io.InputStream;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import ogorkiewicz.jakub.images.exception.ErrorResponse;
import ogorkiewicz.jakub.images.service.ImageService;

@Api(tags = {"images"})
@RestController
@RequestMapping(IMAGE_PATH)
public class ImageController {

    public static final String IMAGE_PATH = "/images";

    private ImageService imageService;

    @Autowired
    public ImageController (ImageService imageService) {
        this.imageService = imageService;
    }

    // /**
    //  * GET image
    //  */

    @ApiOperation(value = "Return image file")
    @ApiResponses(
        value = {
            @ApiResponse(
                code = 400,
                message = "Requested image does not exist", 
                response = ErrorResponse.class, 
                examples = @Example(
                    value = 
                        @ExampleProperty(
                            mediaType = APPLICATION_JSON_VALUE,
                            value ="{\n \"errorCode\": \"Image.NOT_FOUND\",\n \"message\": \"Requested entity not found\"\n}"
                            )
                    )
            )
        }
    )
    @GetMapping(path = "/{id}/{fileName}", produces = {APPLICATION_OCTET_STREAM_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<InputStreamResource> getPaintingByFilename(@ApiParam(value = "Painting id") @PathVariable("id") Long paintingId, 
                                                @ApiParam(value = "File name") @PathVariable("fileName") String fileName) {
        InputStream inputStream = imageService.getImageByFilename(fileName, paintingId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", APPLICATION_OCTET_STREAM_VALUE);
        headers.set("Content-Disposition", "attachment; filename=" + fileName);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inputStream));
    }

    
    // /**
    //  * POST - create new painting record
    //  */

    @ApiOperation(value = "Upload images")
    @PostMapping(path = "/{id}", produces= APPLICATION_JSON_VALUE, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@RequestPart(value = "file", required = true) MultipartFile files[], 
    @ApiParam(value = "Painting id") @PathVariable("id") Long paintingId) {
        imageService.saveImages(files, paintingId);
        return ResponseEntity.ok().build();
    }
    
    // /**
    //  * DELETE - delete painting by id
    //  */
    
    // @ApiOperation(value = "Delete painting with given id")
    // @ResponseStatus(NO_CONTENT)
    // @DeleteMapping(path = "/{id}")
    // public ResponseEntity<?> deletePainting(@ApiParam(value = "Painting id") @PathVariable("id") Long id) {
    //     imageService.deletePaintingById(id);
    //     return ResponseEntity.noContent().build();
    // }
}