package ogorkiewicz.jakub.catalogue.model;

import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "painting")
public class Painting {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String collection;
    private String description;
    private String category;
    private Availability availability;
    private int width;
    private int height;
    private Double price;
    @Column(name ="create_date_time")
    private OffsetDateTime createDateTime;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "painting_id")
    private List<ImageUrl> images;
    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Tag> tags;

    @PrePersist
    private void initCreateDate() {
        this.createDateTime = OffsetDateTime.now();
    }

}