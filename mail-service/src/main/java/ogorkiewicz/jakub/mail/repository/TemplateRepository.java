package ogorkiewicz.jakub.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ogorkiewicz.jakub.mail.model.Template;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {  

    public Template findByName(String name);

    public void deleteByName(String name);
    
}