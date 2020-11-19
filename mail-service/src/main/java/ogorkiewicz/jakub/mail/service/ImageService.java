package ogorkiewicz.jakub.mail.service;

import static ogorkiewicz.jakub.mail.resource.ImageController.IMAGE_PATH;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.jbosslog.JBossLog;
import ogorkiewicz.jakub.mail.config.NewsletterConfig;
import ogorkiewicz.jakub.mail.dto.ImageDto;
import ogorkiewicz.jakub.mail.model.Image;
import ogorkiewicz.jakub.mail.repository.ImageRepository;

@Service
@JBossLog
public class ImageService {

    private FileService fileService;
    private ImageRepository imageRepository;
    private NewsletterConfig newsletterConfig;

    @Autowired
    public ImageService(FileService fileService, ImageRepository imageRepository, NewsletterConfig newsletterConfig) {
        this.fileService = fileService;
        this.imageRepository = imageRepository;
        this.newsletterConfig = newsletterConfig;
    }

    @Value("${spring.mvc.servlet.path}")
    private String api;
    
    @Transactional
    public void saveImage(MultipartFile file) {

        Optional<Image> imageDb = imageRepository.findByName(file.getOriginalFilename());

        // Saving new images
        Path filePath = fileService.saveFile(file);
        
        if(!imageDb.isPresent() && filePath != null) {
            Image image = new Image();
            image.setLocalUri(filePath.toUri());
            image.setName(file.getOriginalFilename());
            try {
                image.setUrl(new URL(newsletterConfig.getServer() + api + IMAGE_PATH + "/" + file.getOriginalFilename()));
            } catch (MalformedURLException e) {
                log.error("Unable to create file url" + e.getMessage());
            }
            imageRepository.save(image);
        }
    }
    
    @Transactional(readOnly = true)
	public InputStream getImageByFilename(String fileName) {
        Optional<Image> image = imageRepository.findByName(fileName);
        if(image.isPresent()){
            return fileService.readFile(image.get().getLocalUri());
        } else {
            return null;
        }
    }
    
    @Transactional
	public void deleteImage(String fileName) {
        Optional<Image> image = imageRepository.findByName(fileName);
        if(image.isPresent()){
            Image imageEntity = image.get();
            fileService.deleteFile(Paths.get(imageEntity.getLocalUri()));
            imageRepository.delete(imageEntity);
        }
    }

	public List<ImageDto> getAllImages() {
		return imageRepository.findAll().stream().map(ImageDto::new).collect(Collectors.toList());
	}

}