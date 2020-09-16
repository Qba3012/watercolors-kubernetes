package ogorkiewicz.jakub.catalogue.dto;

import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.catalogue.model.Painting;

@Getter
@NoArgsConstructor
public class SimplePaintingDto {
    private Long id;
    private String title;
    private String description;
    private String availability;
    private String category;
    private String mainImage;
    private Double price;
    private String createDateTime;

    public SimplePaintingDto(Painting entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.availability = entity.getAvailability().toString();
        this.category = entity.getCategory();
        this.mainImage = entity.getMainImage();
        this.price = entity.getPrice();
        this.createDateTime = entity.getCreateDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
