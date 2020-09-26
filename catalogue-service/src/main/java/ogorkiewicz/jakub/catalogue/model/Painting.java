package ogorkiewicz.jakub.catalogue.model;

import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "painting_id")
    private List<ImageUrl> images;

    @PrePersist
    private void initCreateDate() {
        this.createDateTime = OffsetDateTime.now();
    }

}