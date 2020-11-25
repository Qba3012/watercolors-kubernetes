package ogorkiewicz.jakub.mail.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsletterDataDto {

    private String template;
    
    private String subject;

    private List<TemplateDataDto> data;

}