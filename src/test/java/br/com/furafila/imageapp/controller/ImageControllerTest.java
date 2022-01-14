package br.com.furafila.imageapp.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.furafila.imageapp.exception.FileExtensionNotValidException;
import br.com.furafila.imageapp.response.ErrorResponse;
import br.com.furafila.imageapp.response.NewImageResponse;
import br.com.furafila.imageapp.service.ImageService;

@ExtendWith(SpringExtension.class)
public class ImageControllerTest {

	private static final String BASE_PATH = "/image";
	private static final String PATH_VARIABLE = BASE_PATH.concat("/{id}");

	@InjectMocks
	private ImageController imageController;

	@Mock
	private ImageService imageService;

	private MockMvc mockMvc;
	private ObjectMapper mapper;

	@BeforeEach
	public void setup() {

		this.mockMvc = MockMvcBuilders.standaloneSetup(imageController).setControllerAdvice(new ImageControllerAdvice())
				.build();
		this.mapper = new ObjectMapper();

	}

	@Test
	public void shouldSaveImage() throws Exception {

		MockMultipartFile mockFile = new MockMultipartFile("file", "hello.png", MediaType.TEXT_PLAIN_VALUE,
				"Hello, World!".getBytes());

		long idImageReturned = 15l;
		when(imageService.save(any(MultipartFile.class))).thenReturn(idImageReturned);

		MvcResult result = mockMvc.perform(multipart(BASE_PATH).file(mockFile)).andExpect(status().isOk()).andReturn();

		NewImageResponse newImageResponse = mapper.readValue(result.getResponse().getContentAsString(),
				NewImageResponse.class);

		assertThat(newImageResponse.getId(), equalTo(idImageReturned));

	}

	@Test
	public void shouldFindImageById() throws Exception {

		Path tempFile = Files.createTempFile("tmp", "temp.file");
		Path tempFileWrote = Files.write(tempFile, new byte[] { 67, 85, 82, 73, 79, 83, 79, 79, 79, 79 });

		Resource resource = new FileSystemResource(tempFileWrote.toFile());

		when(imageService.findImageById(anyLong())).thenReturn(resource);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", 15l);
		String uriExpanded = UriComponentsBuilder.fromPath(PATH_VARIABLE).buildAndExpand(parameters).toUriString();

		MvcResult result = mockMvc.perform(get(uriExpanded).contentType(MediaType.MULTIPART_FORM_DATA).accept("*/*"))
				.andExpect(status().isOk()).andReturn();

		String responseAsString = result.getResponse().getContentAsString();

		assertTrue(StringUtils.isNotBlank(responseAsString));

	}

	@Test
	public void shouldFindImageByIdWithEmptyResource() throws Exception {

		Resource resource = null;

		when(imageService.findImageById(anyLong())).thenReturn(resource);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", 15l);
		String uriExpanded = UriComponentsBuilder.fromPath(PATH_VARIABLE).buildAndExpand(parameters).toUriString();

		MvcResult result = mockMvc.perform(get(uriExpanded).contentType(MediaType.MULTIPART_FORM_DATA).accept("*/*"))
				.andExpect(status().isOk()).andReturn();

		String responseAsString = result.getResponse().getContentAsString();

		assertTrue(StringUtils.isBlank(responseAsString));

	}

	@Test
	public void shouldEditImageById() throws Exception {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", 15l);
		String uriExpanded = UriComponentsBuilder.fromPath(PATH_VARIABLE).buildAndExpand(parameters).toUriString();

		MockMultipartFile file = new MockMultipartFile("file", "hello.png", MediaType.TEXT_PLAIN_VALUE,
				"Hello, World!".getBytes());

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(uriExpanded);
		builder.with(new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.setMethod(HttpMethod.PUT.name());
				return request;
			}
		});
		mockMvc.perform(builder.file(file)).andExpect(status().isNoContent()).andReturn();

		verify(this.imageService, times(1)).edit(any(), anyLong());

	}

	@Test
	public void shouldNotSaveImageBecauseExceptionIsThrown() throws Exception {

		MockMultipartFile mockFile = new MockMultipartFile("file", "hello.png", MediaType.TEXT_PLAIN_VALUE,
				"Hello, World!".getBytes());

		doThrow(new RuntimeException("TESTE EXCEPTION")).when(imageService).save(any(MultipartFile.class));

		mockMvc.perform(multipart(BASE_PATH).file(mockFile)).andExpect(status().isInternalServerError()).andReturn();

	}

	@Test
	public void shouldNotSaveImageBecauseFileExtensionNotValidExceptionIsThrown() throws Exception {

		MockMultipartFile mockFile = new MockMultipartFile("file", "hello.png", MediaType.TEXT_PLAIN_VALUE,
				"Hello, World!".getBytes());

		doThrow(new FileExtensionNotValidException()).when(imageService).save(any(MultipartFile.class));

		MvcResult result = mockMvc.perform(multipart(BASE_PATH).file(mockFile)).andExpect(status().isBadRequest())
				.andReturn();

		ErrorResponse errorResponse = mapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

		assertNotNull(errorResponse);
		assertTrue(StringUtils.isNotBlank(errorResponse.getMessage()));

	}

}
