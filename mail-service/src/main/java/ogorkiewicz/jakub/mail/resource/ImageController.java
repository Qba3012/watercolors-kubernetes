package ogorkiewicz.jakub.mail.resource;

import static ogorkiewicz.jakub.mail.resource.ImageController.IMAGE_PATH;
import static ogorkiewicz.jakub.mail.resource.NewsletterController.NEWSLETTER_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.io.InputStream;
import java.util.List;

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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import ogorkiewicz.jakub.mail.dto.ImageDto;
import ogorkiewicz.jakub.mail.service.ImageService;

@Api(tags = {"image"})
@RestController
@RequestMapping(IMAGE_PATH)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ImageController {
    
    public static final String IMAGE_PATH = NEWSLETTER_PATH  + "/images";

    private ImageService imageService;
    
    /**
     * POST - upload new images
     */

    @ApiOperation(value = "Upload images")
    @PostMapping(produces= APPLICATION_JSON_VALUE, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@RequestPart(value = "file", required = true) MultipartFile file) {
        imageService.saveImage(file);
        return ResponseEntity.ok().build();
    }
    
    /**
     * GET image
     */
    
    @ApiOperation(value = "Return image file")
    @ApiResponses(
        value = {
            @ApiResponse(
                code = 204,
                message = "Image not found"
            )
        }
    )
    @GetMapping(path = "/{fileName}", produces = {APPLICATION_OCTET_STREAM_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<InputStreamResource> getImageByFilename(@ApiParam(value = "File name") @PathVariable("fileName") String fileName) {
        InputStream inputStream = imageService.getImageByFilename(fileName);
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
     * GET all images data
     */

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ImageDto>> getAllImages() {
        List<ImageDto> imageDtos = imageService.getAllImages();
        return ResponseEntity.ok().body(imageDtos);
    }
        
    /**
     * DELETE - delete image by file name
     */
    
    @ApiOperation(value = "Delete image by file name")
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping(path = "/{fileName}")
    public ResponseEntity<?> deleteImage( @ApiParam(value = "File name") @PathVariable("fileName") String fileName) {
        imageService.deleteImage(fileName);
        return ResponseEntity.noContent().build();
    }

}