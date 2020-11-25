package ogorkiewicz.jakub.mail.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import ogorkiewicz.jakub.mail.config.NewsletterConfig;
import ogorkiewicz.jakub.mail.dto.NewsletterDataDto;
import ogorkiewicz.jakub.mail.model.Subscriber;
import ogorkiewicz.jakub.mail.model.Template;
import ogorkiewicz.jakub.mail.repository.NewsletterRepository;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NewsletterService {

    private NewsletterRepository newsletterRepository;
    private MailService mailService;
    private TemplateService templateService;
    private NewsletterConfig newsletterConfig;

    @Transactional
	public void addNewEmail(String email) {
        if(!newsletterRepository.findByEmail(email).isPresent()) {
            Template template = templateService.findTemplate(newsletterConfig.getWelcome().getTemplate());
            Subscriber newsletter = new Subscriber();
            newsletter.setEmail(email);
            newsletterRepository.save(newsletter);
            long subscribers = newsletterRepository.count();
            mailService.sendNewsletterWelcome(newsletter, template, subscribers);
        }
    }
    
    @Transactional(readOnly = true)
	public List<Subscriber> getAll() {
		return newsletterRepository.findAll();
	}

    public void sendNewsletter(NewsletterDataDto newsletterData) {
        Template template = templateService.findTemplate(newsletterData.getTemplate());
        List<Subscriber> subscribers = newsletterRepository.findAll();
        mailService.sendNewsletter(subscribers, newsletterData.getSubject(), template, newsletterData.getData());
    }
    
    @Transactional
	public void deleteEmailById(Long id) {
        Optional<Subscriber> newsletterOptional = newsletterRepository.findById(id);
        if(newsletterOptional.isPresent()) {
            long subscribers = newsletterRepository.count();
            mailService.sendNewsletterNotificaiton(newsletterOptional.get().getEmail(), subscribers, false);
            newsletterRepository.deleteById(id);
        }
	}
    
}