package ogorkiewicz.jakub.catalogue.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ogorkiewicz.jakub.catalogue.client.ImageClient;
import ogorkiewicz.jakub.catalogue.dto.DetailedPaintingDto;
import ogorkiewicz.jakub.catalogue.dto.ImageDto;
import ogorkiewicz.jakub.catalogue.dto.PageDto;
import ogorkiewicz.jakub.catalogue.exception.BadRequestException;
import ogorkiewicz.jakub.catalogue.exception.ErrorCode;
import ogorkiewicz.jakub.catalogue.model.Availability;
import ogorkiewicz.jakub.catalogue.model.ImageUrl;
import ogorkiewicz.jakub.catalogue.model.Painting;
import ogorkiewicz.jakub.catalogue.repository.PaintingRepository;

@Service
public class PaintingService {
    
    private PaintingRepository paintingRepository;
    private ImageClient imageClient;

    @Autowired
    private DiscoveryClient discoveryClient; 

    @Autowired
    public PaintingService (PaintingRepository paintingRepository, ImageClient imageClient) {
        this.paintingRepository = paintingRepository;
        this.imageClient = imageClient;
    }
    
    public PageDto getPage(int page) {
        Page<Painting> pageData = paintingRepository.findAll(PageRequest.of(page - 1, 50));
        return new PageDto(page, pageData.getTotalPages(), pageData.getContent());
    }

    public DetailedPaintingDto getPaintingById(Long id) throws BadRequestException{
        return new DetailedPaintingDto(getPainting(id));
    }

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

	public void deletePaintingById(Long id) {
        paintingRepository.deleteById(id);
    }
    
    private Painting getPainting(Long id) {
        Optional<Painting> painting = paintingRepository.findById(id);
        if( painting.isPresent()) {
            return painting.get();
        } else {
            throw new BadRequestException(ErrorCode.NOT_FOUND, Painting.class);
        }
    }

	public DetailedPaintingDto savePainting(DetailedPaintingDto paintingDto) {
        Painting  painting = paintingRepository.save(paintingDto.toEntity());
        ImageDto imageDto = imageClient.getImagesData(painting.getId());
        if(imageDto != null && !imageDto.getImageUrls().isEmpty()) {
            List<ImageUrl> imageUrls = imageDto.toEntity();
            painting.setImages(imageUrls);
            painting.setMainImage(imageUrls.get(0).getUrl());
        }
		return new DetailedPaintingDto(painting);
	}

}