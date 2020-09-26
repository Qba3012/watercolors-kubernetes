package ogorkiewicz.jakub.catalogue.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.catalogue.model.ImageUrl;

@Getter
@NoArgsConstructor
public class ImageDto {

    private List<String> imageUrls;
    
    public List<ImageUrl> toEntity() {
        return this.imageUrls.stream().map(i -> new ImageUrl(i)).collect(Collectors.toList());
    }
}