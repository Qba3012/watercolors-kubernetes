package ogorkiewicz.jakub.catalogue.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.catalogue.model.Painting;
import ogorkiewicz.jakub.catalogue.model.Tag;

@Getter
@NoArgsConstructor
public class SimplePaintingDto {
    private Long id;
    private String title;
    private String description;
    private String availability;
    private String category;
    private ImageDto mainImage;
    private Double price;
    private String createDateTime;
    private List<String> tags;

    public SimplePaintingDto(Painting entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.availability = entity.getAvailability().toString();
        this.category = entity.getCategory(); 
        if(entity.getImages() != null && !entity.getImages().isEmpty()) {
            this.mainImage = new ImageDto(entity.getImages().get(0));
        }
        this.tags = entity.getTags() != null ? entity.getTags().stream().map(Tag::getName).collect(Collectors.toList()) : null;
        this.price = entity.getPrice();
        this.createDateTime = entity.getCreateDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
