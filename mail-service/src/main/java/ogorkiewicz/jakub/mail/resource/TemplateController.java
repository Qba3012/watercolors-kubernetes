package ogorkiewicz.jakub.mail.resource;

import static ogorkiewicz.jakub.mail.resource.TemplateController.TEMPLATE_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import lombok.AllArgsConstructor;
import ogorkiewicz.jakub.mail.dto.TemplateDataDto;
import ogorkiewicz.jakub.mail.dto.TemplateDto;
import ogorkiewicz.jakub.mail.exception.ErrorResponse;
import ogorkiewicz.jakub.mail.service.TemplateService;


@Api(tags = {"templates"})
@RestController
@RequestMapping(TEMPLATE_PATH)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TemplateController {
    
    public static final String TEMPLATE_PATH = "/templates";

    private TemplateService templateService;

    /**
     * POST - upload new template
     */

    @ApiOperation(value = "Upload new template")
    @ApiResponses(
        value = {
            @ApiResponse(
                code = 400,
                message = "Template already exists", 
                response = ErrorResponse.class, 
                examples = @Example(
                    value = 
                        @ExampleProperty(
                            mediaType = APPLICATION_JSON_VALUE,
                            value ="{\n \"errorCode\": \"Template.ALREADY_EXIST\",\n \"message\": \"Entity already exist\"\n}"
                            )
                    )
            )
        }
    )
    @PostMapping(produces= APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadTemplate(@RequestBody TemplateDto templateDto) {
        templateService.saveNewTemplate(templateDto);
        return ResponseEntity.ok().build();
    }

    /**
     * POST - test new template
     */

    @ApiOperation(value = "Test new template")
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
                            value ="{\n \"errorCode\": \"Template.ALREADY_EXIST\",\n \"message\": \"Entity already exist\"\n}"
                            )
                    )
            )
        }
    )
    @PostMapping(path = "/test", produces= APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> testTemplate(@RequestParam("name") String name, @RequestBody List<TemplateDataDto> data) {
        templateService.testTemplate(name, data);
        return ResponseEntity.ok().build();
    }

    /**
     * GET - get all email templates
     */
    
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TemplateDto>> getAllTemplates() {
        List<TemplateDto> templates = templateService.getAllTemplates();
        return ResponseEntity.ok().body(templates);
    }

    /**
     * DELETE - remove email from newsletter
     */
     
    @ApiOperation(value = "Remove template")
    @DeleteMapping
    public ResponseEntity<?> removeTemplate(@RequestParam String name) {
        templateService.deleteTemplate(name);
        return ResponseEntity.noContent().build();
    } 


}
