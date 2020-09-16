package ogorkiewicz.jakub.catalogue.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;

@Entity
public class ImageUrl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Getter
    String url;
    
}