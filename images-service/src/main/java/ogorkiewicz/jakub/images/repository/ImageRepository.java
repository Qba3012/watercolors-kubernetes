package ogorkiewicz.jakub.images.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ogorkiewicz.jakub.images.model.Image;


@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

    public Optional<Image> findByFileNameAndPaintingId(String fileName, Long paintingId);
}