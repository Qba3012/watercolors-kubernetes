package ogorkiewicz.jakub.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.mail.model.TemplateData;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDataDto {
    
    private String tag;

    private String value;

    public TemplateDataDto(TemplateData templateData) {
        this.tag = templateData.getTag();
    }

    public TemplateData toEntity() {
        TemplateData templateData = new TemplateData();
        templateData.setTag(this.tag);
        return templateData;
    }
}