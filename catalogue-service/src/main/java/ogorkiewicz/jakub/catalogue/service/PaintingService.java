package ogorkiewicz.jakub.catalogue.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;
import lombok.extern.jbosslog.JBossLog;
import ogorkiewicz.jakub.catalogue.client.ImageClient;
import ogorkiewicz.jakub.catalogue.dto.DetailedPaintingDto;
import ogorkiewicz.jakub.catalogue.dto.ImageDto;
import ogorkiewicz.jakub.catalogue.dto.PageDto;
import ogorkiewicz.jakub.catalogue.exception.BadRequestException;
import ogorkiewicz.jakub.catalogue.exception.ErrorCode;
import ogorkiewicz.jakub.catalogue.exception.ServiceException;
import ogorkiewicz.jakub.catalogue.model.Availability;
import ogorkiewicz.jakub.catalogue.model.ImageUrl;
import ogorkiewicz.jakub.catalogue.model.Painting;
import ogorkiewicz.jakub.catalogue.repository.ImageUrlRepository;
import ogorkiewicz.jakub.catalogue.repository.PaintingRepository;

@Service
@JBossLog
public class PaintingService {
    
    private PaintingRepository paintingRepository;
    private ImageUrlRepository imageUrlRepository;
    private ImageClient imageClient;
    private CircuitBreakerFactory<?,?> circuitBreakerFactory;

    @Autowired
    public PaintingService (PaintingRepository paintingRepository, ImageUrlRepository imageUrlRepository, ImageClient imageClient, CircuitBreakerFactory<?,?> circuitBreakerFactory) {
        this.paintingRepository = paintingRepository;
        this.imageUrlRepository = imageUrlRepository;
        this.imageClient = imageClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Transactional(readOnly = true)
    public PageDto getPage(int page) {
        if(page < 1) {
            page = 1;
        }
        Page<Painting> pageData = paintingRepository.findAll(PageRequest.of(page - 1, 50));
        return new PageDto(page, pageData.getTotalPages(), pageData.getContent());
    }

    public DetailedPaintingDto getPaintingById(Long id) throws BadRequestException{
        return new DetailedPaintingDto(getPainting(id));
    }

    @Transactional
    public DetailedPaintingDto updatePainting(DetailedPaintingDto detailedPaintingDto) {
        Painting painting = getPainting(detailedPaintingDto.getId());
        painting.setTitle(detailedPaintingDto.getTitle());
        painting.setDescription(detailedPaintingDto.getDescription());
        painting.setAvailability(Availability.valueOf(detailedPaintingDto.getAvailability()));
        painting.setCategory(detailedPaintingDto.getCategory());
        painting.setHeight(detailedPaintingDto.getHeight());
        painting.setWidth(detailedPaintingDto.getWidth());
        painting.setPrice(detailedPaintingDto.getPrice());
        return new DetailedPaintingDto(painting);
    }

    @Transactional
	public void deletePaintingById(Long id) {
        if(paintingRepository.existsById(id)) {
            CircuitBreaker circuitBreaker = circuitBreakerFactory.create("delete-painting");
            circuitBreaker.run(() -> imageClient.deletePaintingsImages(id), (error) -> {
                if(error instanceof FeignException) {
                    throw (FeignException) error;
                } else {
                    log.error("Problem with connecting to catalogue service " + error.getMessage());
                    throw new ServiceException(ErrorCode.SERVICE_NOT_READY, ImageClient.class);
                }
            });
            paintingRepository.deleteById(id);
        }
    }
    
    @Transactional(readOnly = true)
    private Painting getPainting(Long id) {
        Optional<Painting> painting = paintingRepository.findById(id);
        if( painting.isPresent()) {
            return painting.get();
        } else {
            throw new BadRequestException(ErrorCode.NOT_FOUND, Painting.class);
        }
    }

    @Transactional
	public DetailedPaintingDto savePainting(DetailedPaintingDto paintingDto) {
        Painting painting = paintingRepository.save(paintingDto.toEntity());
		return new DetailedPaintingDto(painting);
	}

    @Transactional
	public void updateImages(Long id, List<ImageDto> imageDtos) {
        Optional<Painting> paintingOptional = paintingRepository.findById(id);
        if(paintingOptional.isPresent()) {
            Painting painting = paintingOptional.get();
            imageUrlRepository.deleteAll(painting.getImages());
            List<ImageUrl> imageUrls = imageDtos.stream().map(dto -> new ImageUrl(dto.getOriginal(), dto.getSmall())).collect(Collectors.toList());
            painting.setImages(imageUrls);
        } else {
            throw new BadRequestException(ErrorCode.NOT_FOUND, Painting.class);
        }
    }

}