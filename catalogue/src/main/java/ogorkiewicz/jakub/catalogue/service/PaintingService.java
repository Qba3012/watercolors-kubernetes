package ogorkiewicz.jakub.catalogue.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ogorkiewicz.jakub.catalogue.dto.DetailedPaintingDto;
import ogorkiewicz.jakub.catalogue.dto.PageDto;
import ogorkiewicz.jakub.catalogue.exception.BadRequestException;
import ogorkiewicz.jakub.catalogue.exception.ErrorCode;
import ogorkiewicz.jakub.catalogue.model.Painting;
import ogorkiewicz.jakub.catalogue.repository.PaintingRepository;

@Service
public class PaintingService {
    
    private PaintingRepository paintingRepository;

    @Autowired
    public PaintingService (PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }
    
    public PageDto getPage(int page) {
        Page<Painting> pageData = paintingRepository.findAll(PageRequest.of(page - 1, 50));
        return new PageDto(page, pageData.getTotalPages(), pageData.getContent());
    }

    public DetailedPaintingDto getPaintingById(Long id) throws BadRequestException{
        return new DetailedPaintingDto(getPainting(id));
    }

    // public DetailedPaintingDto updatePainting(DetailedPaintingDto detailedPaintingDto) {
    //     Painting painting = getPainting(detailedPaintingDto.getId());
    //     painting.setTitle(detailedPaintingDto.getTitle());
    //     painting.setDescription(detailedPaintingDto.getDescription());
    //     painting.setAvailability(detailedPaintingDto.getAvailability());
    // }

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

}