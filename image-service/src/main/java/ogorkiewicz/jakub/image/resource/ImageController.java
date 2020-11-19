package ogorkiewicz.jakub.image.resource;

import static ogorkiewicz.jakub.image.resource.ImageController.IMAGE_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.io.InputStream;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import ogorkiewicz.jakub.image.dto.ImageDto;
import ogorkiewicz.jakub.image.exception.ErrorResponse;
import ogorkiewicz.jakub.image.metrics.RequestTimer;
import ogorkiewicz.jakub.image.service.ImageService;

@Api(tags = {"images"})
@RestController
@RequestMapping(IMAGE_PATH)
public class ImageController {

    public static final String IMAGE_PATH = "/paintings";

    private ImageService imageService;
    private RequestTimer requestTimer;

    @Autowired
    public ImageController (ImageService imageService, RequestTimer requestTimer) {
        this.imageService = imageService;
        this.requestTimer = requestTimer;
    }

    /**
     * GET image
     */

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
    public ResponseEntity<InputStreamResource> getImageByFilename(@ApiParam(value = "Painting id") @PathVariable("id") Long paintingId, 
                                                @ApiParam(value = "File name") @PathVariable("fileName") String fileName) {
        InputStream inputStream = requestTimer.getTimer("GET", "getImageFile")
                                    .record(() -> imageService.getImageByFilename(fileName, paintingId));
        if(inputStream != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", APPLICATION_OCTET_STREAM_VALUE);
            headers.set("Content-Disposition", "attachment; filename=" + fileName);
            return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inputStream));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * GET small image
     */

    @ApiOperation(value = "Return small image file")
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
    @GetMapping(path = "/{id}/small/{fileName}", produces = {APPLICATION_OCTET_STREAM_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<InputStreamResource> getSmallImageByFilename(@ApiParam(value = "Painting id") @PathVariable("id") Long paintingId, 
                                                @ApiParam(value = "File name") @PathVariable("fileName") String fileName) {
        InputStream inputStream = requestTimer.getTimer("GET", "getSmallImageFile")
                                    .record(() -> imageService.getSmallImageByFilename(fileName, paintingId));
        if(inputStream != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", APPLICATION_OCTET_STREAM_VALUE);
            headers.set("Content-Disposition", "attachment; filename=" + fileName);
            return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inputStream));
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    
    /**
     * GET images data
     */

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ImageDto>> getImagesByPaintingId(@PathVariable("id") Long paintingId) {
        List<ImageDto> imageDtos = requestTimer.getTimer("GET", "getImageFile").record(() -> imageService.getImagesByPaintingId(paintingId));
        return ResponseEntity.ok().body(imageDtos);
    }

    
    /**
     * POST - upload new images
     */

    @ApiOperation(value = "Upload images")
    @PostMapping(path = "/{id}", produces= APPLICATION_JSON_VALUE, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@RequestPart(value = "file", required = true) MultipartFile files[], 
    @ApiParam(value = "Painting id") @PathVariable("id") Long paintingId) {
        imageService.saveImages(files, paintingId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * DELETE - delete image by file name
     */
    
    @ApiOperation(value = "Delete image by file name and painting id")
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping(path = "/{id}/{fileName}")
    public ResponseEntity<?> deleteImage(@ApiParam(value = "Painting id") @PathVariable("id") Long paintingId, 
                                    @ApiParam(value = "File name") @PathVariable("fileName") String fileName) {
        imageService.deleteImage(paintingId, fileName);
        return ResponseEntity.noContent().build();
    }
        
    /**
     * DELETE - delete all painting images
     */
    
    @ApiOperation(value = "Delete all painting images")
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping(path = "/{id}/all")
    public ResponseEntity<?> deleteAllImages(@ApiParam(value = "Painting id") @PathVariable("id") Long paintingId) {
        imageService.deleteAllImage(paintingId);
        return ResponseEntity.noContent().build();
    }
}