package ogorkiewicz.jakub.mail.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ogorkiewicz.jakub.mail.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {   

    public Optional<Image> findByName(String name);
}