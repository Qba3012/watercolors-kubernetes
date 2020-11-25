package ogorkiewicz.jakub.catalogue.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "image_url")
public class ImageUrl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    private String url;
    @Getter
    @Column(name = "small_url")
    private String smallUrl;
    
    public ImageUrl(String url, String smallUrl) {
        this.url = url;
        this.smallUrl = smallUrl;
    }
}