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

import ogorkiewicz.jakub.catalogue.dto.PageDto;

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
		MvcResult mvcResult = mockMvc.perform(get("/paintings/{page}", 1))
			.andExpect(status().isOk())
			.andReturn();

		// then
		PageDto pageDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PageDto.class);

		then(pageDto)	
			.isExactlyInstanceOf(PageDto.class)
			.extracting(PageDto::getPage,
						PageDto::getTotalPages,
						p -> p.getPaintings().size())
			.containsOnly(1, 1, 1);

		then(pageDto.getPaintings().get(0))
			.hasNoNullFieldsOrProperties();
	}

}
