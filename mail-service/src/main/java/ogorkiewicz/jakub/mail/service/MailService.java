package ogorkiewicz.jakub.mail.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import ogorkiewicz.jakub.mail.config.NewsletterConfig;
import ogorkiewicz.jakub.mail.dto.TemplateDataDto;
import ogorkiewicz.jakub.mail.model.Subscriber;
import ogorkiewicz.jakub.mail.model.Template;

@Service
@JBossLog
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MailService {

    private JavaMailSender emailSender;
    private NewsletterConfig newsletterConfig;

    @Async("threadPoolTaskExecutor")
    public void sendNewsletterWelcome(Subscriber subscriber, Template template, long subscribers) {
        TemplateDataDto data = new TemplateDataDto("id", subscriber.getId().toString());
        sendNewsletter(List.of(subscriber), newsletterConfig.getWelcome().getSubject(), template, List.of(data));
        sendNewsletterNotificaiton(subscriber.getEmail(), subscribers, true);
    }

    @Async
	public void sendTestTemplate(Template template, List<TemplateDataDto> data) {
        Subscriber subscriber = new Subscriber();
        subscriber.setEmail(newsletterConfig.getHost());
        subscriber.setId(0L);
        sendNewsletter(List.of(subscriber), "Test email", template, data);
    }

    @Async("threadPoolTaskExecutor")
    public void sendNewsletterNotificaiton(String email, long subscribers, boolean joined) {
        String text = joined ? "Nowy użytkownik dołączył do newslettera: " : "Użytkownik opuścił newslettera: ";
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(newsletterConfig.getEmail());
        mailMessage.setTo(newsletterConfig.getHost());
        mailMessage.setSubject("Newsletter notification");
        mailMessage.setText(text + email + ", subskrybentów: " + subscribers);
        emailSender.send(mailMessage);
    }

    @Async("threadPoolTaskExecutor")
    public void sendNewsletter(List<Subscriber> subscribers, String subject, Template template, List<TemplateDataDto> data) {

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        String htmlTemplate = fillUpTemplate(template, data);

        for(Subscriber sub : subscribers) {
            String html = addId(htmlTemplate, sub.getId());

            try {
                message.setFrom(newsletterConfig.getEmail());
                message.setSubject(subject);
                message.setText(html, true);
                message.setTo(sub.getEmail());
                emailSender.send(mimeMessage);
                log.info("Newsletter " + "\"" + subject + "\"" + " send to: " + sub.getEmail());
            } catch (MessagingException e) {
                log.error("Problem with sending newsletter email" + e.getMessage());
            }
        }
    }
    
    private String fillUpTemplate(Template template, List<TemplateDataDto> data) {
        String htmlString = template.getHtml();

        if(data != null) {
            for(TemplateDataDto td : data) {
                String tagRegex = "<" + td.getTag() + ">.*<\\/" + td.getTag() + ">";
                Optional<TemplateDataDto> paramOptional = data.stream().filter(d -> d.getTag().equals(td.getTag())).findFirst();
                if(paramOptional.isPresent()) {
                   htmlString = htmlString.replaceAll(tagRegex, paramOptional.get().getValue());
                }
            };
        }
        return htmlString;
    }

    private String addId(String html, Long id) {
        return html.replaceAll("<id>.*<\\/id>", id.toString());
    }
    
}