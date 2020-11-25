package ogorkiewicz.jakub.mail.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ogorkiewicz.jakub.mail.model.Subscriber;

@Repository
public interface NewsletterRepository extends JpaRepository<Subscriber,Long> {
    
    public void deleteByEmail(String email);

    public Optional<Subscriber> findByEmail(String email);

}