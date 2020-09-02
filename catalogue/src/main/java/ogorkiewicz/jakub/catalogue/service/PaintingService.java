package ogorkiewicz.jakub.catalogue.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ogorkiewicz.jakub.catalogue.dto.PageDto;
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

}