package ogorkiewicz.jakub.image.service;
import static ogorkiewicz.jakub.image.resource.ImageController.IMAGE_PATH;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import feign.FeignException;
import lombok.extern.jbosslog.JBossLog;
import ogorkiewicz.jakub.image.client.PaintingClient;
import ogorkiewicz.jakub.image.dto.ImageDto;
import ogorkiewicz.jakub.image.exception.BadRequestException;
import ogorkiewicz.jakub.image.exception.ErrorCode;
import ogorkiewicz.jakub.image.exception.ServiceException;
import ogorkiewicz.jakub.image.model.Image;
import ogorkiewicz.jakub.image.repository.ImageRepository;

@Service
@JBossLog
public class ImageService {

    @Value("${images.server}")
    private String homeUrl;

    @Value("${spring.mvc.servlet.path}")
    private String api;

    private ImageRepository imageRepository;
    private FileService fileService;
    private PaintingClient paintingClient;
    private CircuitBreakerFactory<?,?> circuitBreakerFactory;

    @Autowired
    public ImageService(ImageRepository imageRepository, FileService fileService, PaintingClient paintingClient, CircuitBreakerFactory<?,?> circuitBreakerFactory) {
        this.imageRepository = imageRepository;
        this.fileService = fileService;
        this.paintingClient = paintingClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Transactional
    public void saveImages(MultipartFile files[], Long paintingId) {

        List<Image> images = imageRepository.findByPaintingId(paintingId);
        List<Image> newImages = new ArrayList<>();
        List<String> imagesNames = images.stream().map(Image::getFileName).collect(Collectors.toList());

        // Saving new images
        for (MultipartFile mpf : files) {
            Path filePath = fileService.saveFile(mpf, paintingId);
            Path smallFilePath = fileService.resizeFile(filePath, paintingId);
            if(!imagesNames.contains(mpf.getOriginalFilename()) && filePath != null) {
                Image image = new Image();
                image.setPaintingId(paintingId);
                image.setFileName(mpf.getOriginalFilename());
                image.setLocalUri(filePath.toUri());
                image.setSmallLocalUri(smallFilePath.toUri());
                String smallFileName = FilenameUtils.getName(smallFilePath.toString());
                image.setSmallFileName(smallFileName);
                try {
                    image.setUrl(new URL(homeUrl + api + IMAGE_PATH + "/" + paintingId + "/" + mpf.getOriginalFilename()));
                    image.setSmallUrl(new URL(homeUrl + api + IMAGE_PATH + "/" + paintingId + "/small/" + smallFileName));
                } catch (MalformedURLException e) {
                    log.error("Unable to create file url" + e.getMessage());
                }
            newImages.add(image);
            images.add(image);
            }
        }

        // Sending new images data to catalogue service
        sendImageData(newImages, images, paintingId);
    }

    @Transactional(readOnly = true)
	public InputStream getImageByFilename(String fileName, Long paintingId) {
        Optional<Image> image = imageRepository.findByFileNameAndPaintingId(fileName, paintingId);
        if(image.isPresent()){
            return fileService.readFile(image.get().getLocalUri());
        } else {
            throw new BadRequestException(ErrorCode.NOT_FOUND, Image.class);
        }
	}

    @Transactional(readOnly = true)
	public InputStream getSmallImageByFilename(String fileName, Long paintingId) {
        Optional<Image> image = imageRepository.findBySmallFileNameAndPaintingId(fileName, paintingId);
        if(image.isPresent()){
            return fileService.readFile(image.get().getSmallLocalUri());
        } else {
            throw new BadRequestException(ErrorCode.NOT_FOUND, Image.class);
        }
    }
    
    @Transactional(readOnly = true)
	public List<ImageDto> getImagesByPaintingId(Long paintingId) {
        List<Image> images = imageRepository.findByPaintingId(paintingId);
        if(images == null) {
            images = Collections.emptyList();
        }
        return images.stream().map(ImageDto::new).collect(Collectors.toList());

	}

    @Transactional
	public void deleteImage(Long paintingId, String fileName) {
        Optional<Image> image = imageRepository.findByFileNameAndPaintingId(fileName, paintingId);
        if(image.isPresent()){
            Image imageEntity = image.get();
            fileService.deleteFile(Paths.get(imageEntity.getLocalUri()));
            fileService.deleteFile(Paths.get(imageEntity.getSmallLocalUri()));
            imageRepository.delete(imageEntity);
        }
    }
    
    @Transactional
	public void deleteAllImage(Long paintingId) {
        fileService.deleteAllPaintingImages(paintingId);
        imageRepository.deleteByPaintingId(paintingId);
	}

    private void sendImageData(List<Image> newImages, List<Image> images, Long paintingId) {
        if(!newImages.isEmpty()){
            List<ImageDto> imageDtos = images.stream().map(ImageDto::new).collect(Collectors.toList());
            CircuitBreaker circuitBreaker = circuitBreakerFactory.create("get-painting");
            circuitBreaker.run(() -> paintingClient.sendImagesInfoToCatalogue(paintingId, imageDtos), (error) -> {
                newImages.forEach(i -> fileService.deleteFile(Paths.get(i.getLocalUri())));
                if(error instanceof FeignException) {
                    throw (FeignException) error;
                } else {
                    log.error("Problem with connecting to catalogue service " + error.getMessage());
                    throw new ServiceException(ErrorCode.SERVICE_NOT_READY, PaintingClient.class);
                }
            });
            newImages.forEach(i -> imageRepository.save(i));
        }
    }

}