package ogorkiewicz.jakub.mail.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.mail.model.Template;

@Getter
@NoArgsConstructor
public class TemplateDto {
        
    private String name;

    private String html;

    private List<TemplateDataDto> data; 

    public TemplateDto(Template template) {
        this.name = template.getName();
        this.html = template.getHtml();
        this.data = template.getTemplateData().stream().map(TemplateDataDto::new).collect(Collectors.toList());

    }
    public Template toEntity() {
        Template template = new Template();
        template.setName(this.name);
        template.setHtml(this.html);
        template.setTemplateData(this.data.stream().map(d -> d.toEntity()).collect(Collectors.toList()));
        return template;
    }

}