package ogorkiewicz.jakub.image.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import ogorkiewicz.jakub.image.model.Image;

@Getter
public class ImageDto {

    private List<String> imageUrls;
    
    public ImageDto(List<Image> images) {
        this.imageUrls = images.stream().map(i -> i.getUrl().toString()).collect(Collectors.toList());
    }

}