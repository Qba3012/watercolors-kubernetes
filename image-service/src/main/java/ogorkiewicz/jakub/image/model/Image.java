package ogorkiewicz.jakub.image.model;

import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "image")
public class Image {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "painting_id")
    private Long paintingId;
    @Column(name = "local_uri")
    private URI localUri;
    private URL url;
    @Column(name = "small_file_name")
    private String smallFileName;
    @Column(name = "small_local_uri")
    private URI smallLocalUri;
    @Column(name = "small_url")
    private URL smallUrl;
    @Column(name = "create_date_time")
    private OffsetDateTime createDateTime;

    @PrePersist
    private void initCreateDate() {
        this.createDateTime = OffsetDateTime.now();
    }

}