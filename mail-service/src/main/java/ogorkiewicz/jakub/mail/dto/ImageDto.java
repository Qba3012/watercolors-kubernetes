package ogorkiewicz.jakub.mail.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.mail.model.Image;

@Getter
@NoArgsConstructor
public class ImageDto {

    private String url;
    private String name;
    
    public ImageDto(Image image) {
        this.url = image.getUrl().toString();
        this.name = image.getName().toString();
    }

}