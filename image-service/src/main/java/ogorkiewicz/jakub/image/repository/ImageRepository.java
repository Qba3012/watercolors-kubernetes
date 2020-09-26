package ogorkiewicz.jakub.image.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ogorkiewicz.jakub.image.model.Image;


@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

    public Optional<Image> findByFileNameAndPaintingId(String fileName, Long paintingId);

    public List<Image> findByPaintingId(Long paintingId);
}