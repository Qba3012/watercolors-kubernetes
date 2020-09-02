package ogorkiewicz.jakub.catalogue.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.catalogue.model.ImageUrl;
import ogorkiewicz.jakub.catalogue.model.Painting;

@Getter
@NoArgsConstructor
public class PaintingDto {
    private Long id;
    private String title;
    private String description;
    private String availability;
    private String category;
    private int width;
    private int height;
    private String mainImage;
    private Double price;
    private List<String> images;
    private String createDateTime;

    public PaintingDto(Painting entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.availability = entity.getAvailability().toString();
        this.category = entity.getCategory();
        this.width = entity.getWidth();
        this.height = entity.getHeight();
        this.mainImage = entity.getMainImage();
        this.images = entity.getImages().stream().map(ImageUrl::getUrl).collect(Collectors.toList());
        this.price = entity.getPrice();
        this.createDateTime = entity.getCreateDateTime().toString();
    }

}