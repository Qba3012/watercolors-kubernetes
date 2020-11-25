package ogorkiewicz.jakub.mail.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import ogorkiewicz.jakub.mail.dto.TemplateDataDto;
import ogorkiewicz.jakub.mail.dto.TemplateDto;
import ogorkiewicz.jakub.mail.exception.BadRequestException;
import ogorkiewicz.jakub.mail.exception.ErrorCode;
import ogorkiewicz.jakub.mail.model.Template;
import ogorkiewicz.jakub.mail.repository.TemplateRepository;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TemplateService {

    private TemplateRepository templateRepository;
    private MailService mailService;
    
    @Transactional
	public void saveNewTemplate(TemplateDto templateDto) {
        if(templateRepository.findByName(templateDto.getName()) == null ) {
            templateRepository.save(templateDto.toEntity());
        } else {
            throw new BadRequestException(ErrorCode.ALREADY_EXIST, Template.class);
        }
    }

    @Transactional
	public void deleteTemplate(String name) {
        templateRepository.deleteByName(name);
	}

    @Transactional(readOnly = true)
	public List<TemplateDto> getAllTemplates() {
		return templateRepository.findAll().stream().map(TemplateDto::new).collect(Collectors.toList());
	}

	public void testTemplate(String name, List<TemplateDataDto> data) {
        Template template = findTemplate(name);
        mailService.sendTestTemplate(template, data);
    }
    
    public Template findTemplate(String name ) {
        Template template = templateRepository.findByName(name);
        if(template != null) {
            return template;
        } else {
            throw new BadRequestException(ErrorCode.NOT_EXIST, Template.class);
        }
    }
       
}