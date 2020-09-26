package ogorkiewicz.jakub.catalogue.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ogorkiewicz.jakub.catalogue.model.Availability;
import ogorkiewicz.jakub.catalogue.model.ImageUrl;
import ogorkiewicz.jakub.catalogue.model.Painting;

@Getter
@Setter
@NoArgsConstructor
public class DetailedPaintingDto {
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

    public DetailedPaintingDto(Painting entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.availability = entity.getAvailability().toString();
        this.category = entity.getCategory();
        this.width = entity.getWidth();
        this.height = entity.getHeight();
        this.mainImage = entity.getMainImage();
        this.images = entity.getImages() != null ? entity.getImages().stream().map(ImageUrl::getUrl).collect(Collectors.toList()) : null;
        this.price = entity.getPrice();
        this.createDateTime = entity.getCreateDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
      }

    public Painting toEntity() {
        Painting painting = new Painting();
        painting.setTitle(this.title);
        painting.setDescription(this.description);
        painting.setCategory(this.category);
        painting.setAvailability(Availability.valueOf(this.availability));
        painting.setHeight(this.height);
        painting.setWidth(this.width);
        painting.setPrice(this.price);
        return painting;
    }

}