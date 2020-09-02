package ogorkiewicz.jakub.catalogue.model;

import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Getter;

@Entity
@Getter
public class Painting {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String category;
    private Availability availability;
    private int width;
    private int height;
    private Double price;
    private String mainImage;
    private OffsetDateTime createDateTime;
    @OneToMany
    @JoinColumn(name = "painting_id")
    private List<ImageUrl> images;

}