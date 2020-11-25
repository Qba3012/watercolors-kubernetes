package ogorkiewicz.jakub.image.integration;

import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static ogorkiewicz.jakub.image.resource.ImageController.IMAGE_PATH;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import ogorkiewicz.jakub.image.client.PaintingClient;
import ogorkiewicz.jakub.image.dto.ImageDto;
import ogorkiewicz.jakub.image.exception.ErrorCode;
import ogorkiewicz.jakub.image.exception.ErrorResponse;
import ogorkiewicz.jakub.image.exception.ServiceException;
import ogorkiewicz.jakub.image.model.Image;
import ogorkiewicz.jakub.image.repository.ImageRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ImageResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ImageRepository imageRepository;
	
	@MockBean
    private PaintingClient paintingClient;

    @Value("${images.server}")
    private String homeUrl;

    @Value("${images.homePath}")
    private String path;

    private final Long paintingId = 1L;
    private final static String FILE_NAME = "test.jpg";
    private final static String SMALL_FILE_NAME = "test-small.jpg";

    @AfterEach
    public void cleanUp() throws Exception {
        mockMvc.perform(delete(IMAGE_PATH + "/{id}/all", paintingId))
        .andExpect(status().isNoContent());

        if(Files.exists(Paths.get(path))) {
            Files.walk(Paths.get(path))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }        
    }
    
    @Test
    public void shouldUploadImage() throws Exception {
        // given // when
        createImage();

        // then
        Image image = imageRepository.findByFileNameAndPaintingId(FILE_NAME, paintingId).orElse(null);

        then(image)
            .isNotNull();
        then(image)
            .extracting(Image::getFileName,
                        Image::getPaintingId)
            .containsOnly(FILE_NAME,
                        paintingId);
        then(Paths.get(image.getLocalUri()).toFile())
            .exists()
            .hasBinaryContent(getTestImage(FILE_NAME).readAllBytes());
    }

    @Test
    public void shouldNotUploadImageForNotExistingPainting() throws Exception {
        // given 
        Mockito.when(paintingClient.sendImagesInfoToCatalogue(anyLong(), anyList()))
            .thenThrow(new ServiceException(ErrorCode.SERVICE_NOT_READY, FeignClient.class));
            
        // when
        MockMultipartFile file = new MockMultipartFile("file", FILE_NAME, APPLICATION_OCTET_STREAM, getTestImage(FILE_NAME));
        
        mockMvc.perform(multipart(IMAGE_PATH + "/{id}", paintingId)
        .file(file))
        .andExpect(status().is5xxServerError());

        // then
        Image image = imageRepository.findByFileNameAndPaintingId(FILE_NAME, paintingId).orElse(null);

        then(image)
            .isNull();

        then(Paths.get(path, "painting" + paintingId, FILE_NAME).toFile())
            .doesNotExist();
    }

    @Test
    public void shouldGetSmallImageFile() throws Exception { 
        // given 
        createImage();

        // when
        MvcResult mvcResult = mockMvc.perform(get(IMAGE_PATH + "/{id}/small/{fileName}", paintingId, SMALL_FILE_NAME))
            .andExpect(status().isOk())
            .andReturn();

        // then
        byte[] data = mvcResult.getResponse().getContentAsByteArray();

        then(data)
            .isEqualTo(getTestImage(SMALL_FILE_NAME).readAllBytes());
    }

    @Test
    public void shouldGetImageFile() throws Exception { 
        // given 
        createImage();

        // when
        MvcResult mvcResult = mockMvc.perform(get(IMAGE_PATH + "/{id}/{fileName}", paintingId, FILE_NAME))
            .andExpect(status().isOk())
            .andReturn();

        // then
        byte[] data = mvcResult.getResponse().getContentAsByteArray();

        then(data)
            .isEqualTo(getTestImage(FILE_NAME).readAllBytes());
    }

    @Test
    public void shouldNotGetNotExistingImageFile() throws Exception {
        // given // when
        MvcResult mvcResult = mockMvc.perform(get(IMAGE_PATH + "/{id}/{fileName}", paintingId, FILE_NAME))
            .andExpect(status().isBadRequest())
            .andReturn();

		// then
		ErrorResponse errorResponse = 
			objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
		
		then(errorResponse)
			.extracting(ErrorResponse::getErrorCode,
						ErrorResponse::getMessage)
			.containsOnly(Image.class.getSimpleName() + '.' + ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getDescription());
    }

    @Test
    public void shouldGetImageData() throws Exception {
        // given 
        createImage();

        // when
        MvcResult mvcResult = mockMvc.perform(get(IMAGE_PATH + "/{id}", paintingId))
            .andExpect(status().isOk())
            .andReturn();

		// then
		List<ImageDto> imageDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<ImageDto>>(){});
        
        Condition<String> imageUrlCondition = new Condition<>(v -> v.endsWith(FILE_NAME), "file name");
        then(imageDtos)
            .hasSize(1)
            .extracting(ImageDto::getOriginal)
            .areExactly(1, imageUrlCondition);
    }

    @Test
    public void shouldNotGetNotExistingImageData() throws Exception {
        // given // when
        MvcResult mvcResult = mockMvc.perform(get(IMAGE_PATH + "/{id}", paintingId))
            .andExpect(status().isOk())
            .andReturn();

		// then
		List<ImageDto> imageDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<ImageDto>>(){});
        
        then(imageDtos)
            .isEmpty();
    }

    @Test
    public void shouldDeleteImage() throws Exception {
        // given
        createImage();

        // when
        mockMvc.perform(delete(IMAGE_PATH + "/{id}/{fileName}", paintingId, FILE_NAME))
            .andExpect(status().isNoContent());
        
        // then
        Optional<Image> image = imageRepository.findByFileNameAndPaintingId(FILE_NAME, paintingId);
        Path filePath = Paths.get(path, "painting" + paintingId, FILE_NAME);

        then(image.isPresent())
            .isFalse();
        then(filePath.toFile())
            .doesNotExist();
        then(filePath.getParent().toFile())
            .exists();
    }

    @Test
    public void shouldDeleteAllPaintingImages() throws Exception {
        // given
        createImage();

        // when
        mockMvc.perform(delete(IMAGE_PATH + "/{id}/all", paintingId))
            .andExpect(status().isNoContent());
        
        // then
        Optional<Image> image = imageRepository.findByFileNameAndPaintingId(FILE_NAME, paintingId);
        Path filePath = Paths.get(path, "painting" + paintingId, FILE_NAME);

        then(image.isPresent())
            .isFalse();
        then(filePath.toFile())
            .doesNotExist();
        then(filePath.getParent().toFile())
            .doesNotExist();
    }

    private void createImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", FILE_NAME, APPLICATION_OCTET_STREAM, getTestImage(FILE_NAME));
        
        mockMvc.perform(multipart(IMAGE_PATH + "/{id}", paintingId)
        .file(file))
        .andExpect(status().isOk());
    }

    public InputStream getTestImage(String fileName){
            return this.getClass().getResourceAsStream("/" + fileName);
    }

}