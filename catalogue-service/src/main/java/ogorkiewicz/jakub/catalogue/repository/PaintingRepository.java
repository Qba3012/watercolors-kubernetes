package ogorkiewicz.jakub.catalogue.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ogorkiewicz.jakub.catalogue.model.Painting;

@Repository
public interface PaintingRepository extends PagingAndSortingRepository<Painting, Long> {
}