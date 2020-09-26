package ogorkiewicz.jakub.image.model;

import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Image {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private Long paintingId;
    private URI localUri;
    private URL url;
    private OffsetDateTime createDateTime;

    @PrePersist
    private void initCreateDate() {
        this.createDateTime = OffsetDateTime.now();
    }

}