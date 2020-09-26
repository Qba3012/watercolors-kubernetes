package ogorkiewicz.jakub.image.service;

import static ogorkiewicz.jakub.image.resource.ImageController.IMAGE_PATH;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.jbosslog.JBossLog;
import ogorkiewicz.jakub.image.dto.ImageDto;
import ogorkiewicz.jakub.image.exception.BadRequestException;
import ogorkiewicz.jakub.image.exception.ErrorCode;
import ogorkiewicz.jakub.image.model.Image;
import ogorkiewicz.jakub.image.repository.ImageRepository;

@Service
@JBossLog
public class ImageService {

    @Value("${images.server}")
    private String homeUrl;

    private ImageRepository imageRepository;
    private FileService fileService;

    @Autowired
    public ImageService(ImageRepository imageRepository, FileService fileService) {
        this.imageRepository = imageRepository;
        this.fileService = fileService;
    }

    public void saveImages(MultipartFile files[], Long paintingId) {
        for (MultipartFile mpf : files) {
            Path filePath = fileService.saveFile(mpf, paintingId);
            Optional<Image> optionalImage = imageRepository.findByFileNameAndPaintingId(mpf.getOriginalFilename(), paintingId);
            if(optionalImage.isEmpty()){
                Image image = new Image();
                image.setPaintingId(paintingId);
                image.setFileName(mpf.getOriginalFilename());
                image.setLocalUri(filePath.toUri());
                try {
                    image.setUrl(new URL(homeUrl + IMAGE_PATH + "/" + mpf.getOriginalFilename()));
                } catch (MalformedURLException e) {
                    log.error("Unable to create file url" + e.getMessage());
                }
            imageRepository.save(image);
            }
        }
    }

	public InputStream getImageByFilename(String fileName, Long paintingId) {
        Optional<Image> image = imageRepository.findByFileNameAndPaintingId(fileName, paintingId);
        if(image.isPresent()){
            return fileService.readFile(image.get().getLocalUri());
        } else {
            throw new BadRequestException(ErrorCode.NOT_FOUND, Image.class);
        }
	}

	public ImageDto getImagesByPaintingId(Long paintingId) {
        List<Image> images = imageRepository.findByPaintingId(paintingId);
        if( images != null && !images.isEmpty()) {
            return new ImageDto(images);
        }
        return null;

	}

}
