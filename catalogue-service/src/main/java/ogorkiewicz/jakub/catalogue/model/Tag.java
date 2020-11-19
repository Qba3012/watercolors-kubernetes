package ogorkiewicz.jakub.catalogue.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;

@Entity
@Getter
@Table(name = "tag")
public class Tag {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Tag (String name) {
        this.name = name;
    }
    
}