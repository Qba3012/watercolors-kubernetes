package ogorkiewicz.jakub.image.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.image.model.Image;

@Getter
@NoArgsConstructor
public class ImageDto {

    private String original;
    private String small;
    
    public ImageDto(Image image) {
        this.original = image.getUrl().toString();
        this.small = image.getSmallUrl().toString();
    }

}