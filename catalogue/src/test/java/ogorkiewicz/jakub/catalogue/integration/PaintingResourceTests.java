package ogorkiewicz.jakub.catalogue.integration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import ogorkiewicz.jakub.catalogue.dto.DetailedPaintingDto;
import ogorkiewicz.jakub.catalogue.dto.PageDto;
import ogorkiewicz.jakub.catalogue.dto.SimplePaintingDto;
import ogorkiewicz.jakub.catalogue.exception.ErrorCode;
import ogorkiewicz.jakub.catalogue.exception.ErrorResponse;
import ogorkiewicz.jakub.catalogue.model.Painting;

@SpringBootTest
@AutoConfigureMockMvc
class PaintingResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void shouldGetPage() throws Exception {
		// given // when
		MvcResult mvcResult = mockMvc.perform(get("/paintings/page/{page}", 1))
			.andExpect(status().isOk())
			.andReturn();

		// then
		PageDto pageDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PageDto.class);

		then(pageDto)	
			.extracting(PageDto::getPage,
						PageDto::getTotalPages,
						p -> p.getPaintings().size())
			.containsOnly(1, 1, 1);

		then(pageDto.getPaintings().get(0))
			.isExactlyInstanceOf(SimplePaintingDto.class)
			.hasNoNullFieldsOrProperties();
	}

	@Test
	public void shouldGetPaintingById() throws Exception{
		// given // when
		MvcResult mvcResult = mockMvc.perform(get("/paintings/{id}", 1))
			.andExpect(status().isOk())
			.andReturn();

		// then
		DetailedPaintingDto testDetailedPaintingDto = new DetailedPaintingDto();
		testDetailedPaintingDto.setId(1L);
		testDetailedPaintingDto.setTitle("TEST TITLE");
		testDetailedPaintingDto.setDescription("TEST DESCIRPTION");
		testDetailedPaintingDto.setAvailability("AVAILABLE");
		testDetailedPaintingDto.setCategory("birds");
		testDetailedPaintingDto.setWidth(200);
		testDetailedPaintingDto.setHeight(300);
		testDetailedPaintingDto.setMainImage("http://localhost:8080/images/1");
		testDetailedPaintingDto.setPrice(25.6);

		DetailedPaintingDto detailedPaintingDto = 
			objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DetailedPaintingDto.class);

		then(detailedPaintingDto)
			.usingRecursiveComparison()
			.ignoringExpectedNullFields()
			.isEqualTo(testDetailedPaintingDto);

		then(detailedPaintingDto.getImages().size())
			.isEqualTo(3);
	}

	@Test
	public void shouldNotGetNotExistingPainting() throws Exception {
		// given // when
		MvcResult mvcResult = mockMvc.perform(get("/paintings/{id}", 2))
			.andExpect(status().isBadRequest())
			.andReturn();

		ErrorResponse errorResponse = 
			objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
		
		then(errorResponse)
			.extracting(ErrorResponse::getErrorCode,
						ErrorResponse::getMessage)
			.containsOnly(Painting.class.getSimpleName() + '.' + ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getDescription());

	}

}
