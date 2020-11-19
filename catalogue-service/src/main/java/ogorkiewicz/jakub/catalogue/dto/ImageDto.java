package ogorkiewicz.jakub.catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.catalogue.model.ImageUrl;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {

    private String original;
    private String small;
    
    public ImageUrl toEntity() {
        return new ImageUrl(this.original, this.small);
    }

    public ImageDto(ImageUrl imageUrl) {
        this.original = imageUrl.getUrl();
        this.small = imageUrl.getSmallUrl();
    }
}