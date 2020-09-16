package ogorkiewicz.jakub.catalogue.integration;

import static ogorkiewicz.jakub.catalogue.resource.PaintingController.PAINTING_PATH;
import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
		MvcResult mvcResult = mockMvc.perform(get(PAINTING_PATH + "/page/{page}", 1))
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
		MvcResult mvcResult = mockMvc.perform(get(PAINTING_PATH + "/{id}", 1))
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
		MvcResult mvcResult = mockMvc.perform(get(PAINTING_PATH + "/{id}", 2))
			.andExpect(status().isBadRequest())
			.andReturn();

		// then
		ErrorResponse errorResponse = 
			objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
		
		then(errorResponse)
			.extracting(ErrorResponse::getErrorCode,
						ErrorResponse::getMessage)
			.containsOnly(Painting.class.getSimpleName() + '.' + ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getDescription());

	}

	@Test
	public void shouldCreateNewPainting() throws Exception {
		createPainting();
	}

	@Test
	public void shouldNotCreatePainitngWithSetId() throws Exception {
		// given 
		DetailedPaintingDto paintingDto = new DetailedPaintingDto();
		paintingDto.setId(1L);
		paintingDto.setTitle("TEST TITLE");
		paintingDto.setDescription("TEST DESCIRPTION");
		paintingDto.setAvailability("AVAILABLE");
		paintingDto.setCategory("birds");
		paintingDto.setWidth(200);
		paintingDto.setHeight(300);
		paintingDto.setPrice(25.6);

		// when
		MvcResult mvcResult = mockMvc.perform(post(PAINTING_PATH)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(paintingDto)))
			.andExpect(status().isBadRequest())
			.andReturn();
		
		// then
		ErrorResponse errorResponse = 
			objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

		then(errorResponse)
			.extracting(ErrorResponse::getErrorCode,
						ErrorResponse::getMessage)
			.containsOnly(Painting.class.getSimpleName() + '.' + ErrorCode.ID_NOT_NULL, ErrorCode.ID_NOT_NULL.getDescription());
	}

	@Test
	public void shouldUpdatePainting() throws Exception{
		// given 
		DetailedPaintingDto paintingDto = createPainting();
		paintingDto.setTitle("TEST TITLE1");
		paintingDto.setDescription("TEST DESCIRPTION1");
		paintingDto.setAvailability("TO_ORDER");
		paintingDto.setCategory("dogs");
		paintingDto.setWidth(300);
		paintingDto.setHeight(400);
		paintingDto.setPrice(38.6);

		// when
		MvcResult mvcResult = mockMvc.perform(put(PAINTING_PATH)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(paintingDto)))
			.andExpect(status().isOk())
			.andReturn();
		
		// then
		DetailedPaintingDto detailedPaintingDto = 
			objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DetailedPaintingDto.class);


		then(detailedPaintingDto)
			.usingRecursiveComparison()
			.ignoringExpectedNullFields()
			.isEqualTo(paintingDto);
	}

	@Test
	public void shouldNotUpdateNotExistingPainting() throws Exception{
		// given 
		DetailedPaintingDto paintingDto = new DetailedPaintingDto();
		paintingDto.setId(0L);
		paintingDto.setTitle("TEST TITLE1");
		paintingDto.setDescription("TEST DESCIRPTION1");
		paintingDto.setAvailability("TO_ORDER");
		paintingDto.setCategory("dogs");
		paintingDto.setWidth(300);
		paintingDto.setHeight(400);
		paintingDto.setPrice(38.6);

		// when
		MvcResult mvcResult = mockMvc.perform(put(PAINTING_PATH)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(paintingDto)))
			.andExpect(status().isBadRequest())
			.andReturn();
		
		// then
		ErrorResponse errorResponse = 
			objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
		
		then(errorResponse)
			.extracting(ErrorResponse::getErrorCode,
						ErrorResponse::getMessage)
			.containsOnly(Painting.class.getSimpleName() + '.' + ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getDescription());
	}

	@Test
	public void shouldDeletePainting() throws Exception{
		// given 
		DetailedPaintingDto paintingDto = createPainting();

		// when // then
		mockMvc.perform(delete(PAINTING_PATH + "/{id}", paintingDto.getId()))
			.andExpect(status().isNoContent());
	}

	private DetailedPaintingDto createPainting() throws Exception{
		// given 
		DetailedPaintingDto paintingDto = new DetailedPaintingDto();
		paintingDto.setTitle("TEST TITLE");
		paintingDto.setDescription("TEST DESCIRPTION");
		paintingDto.setAvailability("AVAILABLE");
		paintingDto.setCategory("birds");
		paintingDto.setWidth(200);
		paintingDto.setHeight(300);
		paintingDto.setPrice(25.6);

		// when
		MvcResult mvcResult = mockMvc.perform(post(PAINTING_PATH)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(paintingDto)))
			.andExpect(status().isOk())
			.andReturn();
		
		// then
		DetailedPaintingDto detailedPaintingDto = 
			objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DetailedPaintingDto.class);

		then(detailedPaintingDto)
			.usingRecursiveComparison()
			.ignoringExpectedNullFields()
			.isEqualTo(paintingDto);

		then(detailedPaintingDto.getImages())
			.isNull();
		
		return detailedPaintingDto;
	}
}