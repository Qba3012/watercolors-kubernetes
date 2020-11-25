package ogorkiewicz.jakub.catalogue.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ogorkiewicz.jakub.catalogue.model.ImageUrl;

@Repository
public interface ImageUrlRepository extends CrudRepository<ImageUrl, Long>{
}