package ogorkiewicz.jakub.mail.resource;

import static ogorkiewicz.jakub.mail.resource.NewsletterController.NEWSLETTER_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;
import ogorkiewicz.jakub.mail.dto.NewsletterDataDto;
import ogorkiewicz.jakub.mail.model.Subscriber;
import ogorkiewicz.jakub.mail.service.NewsletterService;

@Api(tags = {"newsletter"})
@RestController
@RequestMapping(NEWSLETTER_PATH)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NewsletterController {
    
    public static final String NEWSLETTER_PATH = "/newsletter";

    private NewsletterService newsletterService;

    /**
     * POST - add new address to newsletter
     */
     
    @ApiOperation(value = "Add new email to newsletter")
    @PostMapping("/join")
    public ResponseEntity<?> addNewEmail(@RequestParam String email) {
        newsletterService.addNewEmail(email);
        return ResponseEntity.ok().build();
    } 

    /**
     * POST - send email to subscribers
     */
     
    @ApiOperation(value = "Send email to subscribers")
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendEmailToSubscribers(@RequestBody NewsletterDataDto newsletterData) {
        newsletterService.sendNewsletter(newsletterData);
        return ResponseEntity.ok().build();
    } 

    /**
     * GET - get all newsletter addresses
     */
     
    @ApiOperation(value = "Get all emails")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Subscriber>> getAllEmails() {
        List<Subscriber> emails = newsletterService.getAll();
        return ResponseEntity.ok().body(emails);
    } 

    /**
     * DELETE - remove email from newsletter by id
     */
     
    @ApiOperation(value = "Remove email from newsletter by id")
    @DeleteMapping
    public ResponseEntity<?> removeEmailById(@RequestParam("id") Long id) {
        newsletterService.deleteEmailById(id);
        return ResponseEntity.noContent().build();
    } 

}