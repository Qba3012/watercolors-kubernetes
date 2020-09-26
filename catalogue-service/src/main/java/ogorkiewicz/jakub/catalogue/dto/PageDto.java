package ogorkiewicz.jakub.catalogue.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.catalogue.model.Painting;

@Getter
@NoArgsConstructor
public class PageDto {
    
    private int page;
    private int totalPages;
    private List<SimplePaintingDto> paintings;

    public PageDto (int page, int totalPages, List<Painting> paintings) {
        this.page = page;
        this.totalPages = totalPages;
        this.paintings = paintings.stream().map(SimplePaintingDto::new).collect(Collectors.toList());
    }

}