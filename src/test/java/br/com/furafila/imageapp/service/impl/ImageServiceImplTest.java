package br.com.furafila.imageapp.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import br.com.furafila.imageapp.exception.FileExtensionNotValidException;
import br.com.furafila.imageapp.exception.ImageNotFoundException;
import br.com.furafila.imageapp.model.Image;
import br.com.furafila.imageapp.repository.ImageRepository;
import br.com.furafila.imageapp.service.ImageService;

@ContextConfiguration
@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

	@InjectMocks
	public ImageService imageService = new ImageServiceImpl();

	@Mock
	private ImageRepository imageRepository;

	@Test
	public void shouldSaveImage() throws IOException {

		MultipartFile mpf = new MockMultipartFile("teste.png", "png", "application/png", new byte[] { 0, 0 });

		Image image = new Image();
		image.setId(5l);
		when(imageRepository.save(any(Image.class))).thenReturn(image);

		Long id = imageService.save(mpf);

		assertThat(id, is(5l));

	}

	@Test
	public void shouldSaveImageWhenFileIsEmpty() throws IOException {

		MultipartFile mpf = new MockMultipartFile("teste.png", "png", "application/png", new byte[] {});

		Image image = new Image();
		image.setId(5l);
		when(imageRepository.save(any(Image.class))).thenReturn(image);

		Long id = imageService.save(mpf);

		assertThat(id, is(5l));

	}

	@Test
	public void shouldFindImageById() throws IOException {

		Path pathTempFile = Files.createTempFile("tmp", "tmp.png");

		Image image = new Image();
		image.setImage(Files.readAllBytes(pathTempFile));
		image.setFileExtension("jpg");
		when(imageRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(image));

		Resource resource = imageService.findImageById(5l);

		assertThat(resource, is(not(nullValue())));

	}

	@Test
	public void shouldNotFindImageByIdWithImageFieldIsNull() throws IOException {

		Image image = new Image();
		image.setImage(null);
		when(imageRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(image));

		Resource resource = imageService.findImageById(5l);

		assertThat(resource, is(nullValue()));

	}

	@Test
	public void shouldNotFindImageByIdWithFileExtensionFieldIsNull() throws IOException {

		Path pathTempFile = Files.createTempFile("tmp", "tmp.png");

		Image image = new Image();
		image.setImage(Files.readAllBytes(pathTempFile));
		when(imageRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(image));

		Resource resource = imageService.findImageById(5l);

		assertThat(resource, is(nullValue()));

	}

	@Test
	public void shouldNotFindImageByIdThrowingImageNotFoundException() throws IOException {

		Image image = null;
		when(imageRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(image));

		ImageNotFoundException imageNotFoundException = Assertions.assertThrows(ImageNotFoundException.class, () -> {
			imageService.findImageById(5l);
		});

		assertThat(imageNotFoundException.getMessage(), equalTo("Image not found!"));

	}

	@Test
	public void shouldEditImagem() throws IOException {

		Path pathTempFile = Files.createTempFile("tmp", "tmp.png");
		byte[] tempFileInBytes = Files.readAllBytes(pathTempFile);

		Image image = new Image();
		image.setFileExtension("png");
		image.setImage(tempFileInBytes);
		image.setId(5l);
		when(imageRepository.findById(anyLong())).thenReturn(Optional.ofNullable(image));

		MultipartFile mpf = new MockMultipartFile("teste.png", "teste.png", "application/png", tempFileInBytes);
		imageService.edit(mpf, 4l);

		ArgumentCaptor<Image> captor = ArgumentCaptor.forClass(Image.class);
		verify(imageRepository).save(captor.capture());
		verify(imageRepository, times(1)).save(any());

		Image imageCapt = captor.getValue();
		assertThat(imageCapt.getId(), is(not(nullValue())));
		assertThat(imageCapt.getFileExtension(), is(not(nullValue())));
		assertThat(imageCapt.getImage(), is(not(nullValue())));

	}

	@Test
	public void shouldNotEditImagemBecauseImageIdWasNotFound() throws IOException {

		Path pathTempFile = Files.createTempFile("tmp", "tmp.png");
		byte[] tempFileInBytes = Files.readAllBytes(pathTempFile);

		Image image = null;
		when(imageRepository.findById(anyLong())).thenReturn(Optional.ofNullable(image));

		Assertions.assertThrows(ImageNotFoundException.class, () -> {
			MultipartFile mpf = new MockMultipartFile("teste.png", "teste.png", "application/png", tempFileInBytes);
			imageService.edit(mpf, 4l);
		});

		verify(imageRepository, never()).save(any());

	}

	@Test
	public void shouldNotEditImagemBeacuseImageFileExtensionIsNotValid() throws IOException {

		Path pathTempFile = Files.createTempFile("tmp", "tmp.txt");
		byte[] tempFileInBytes = Files.readAllBytes(pathTempFile);

		FileExtensionNotValidException exception = Assertions.assertThrows(FileExtensionNotValidException.class, () -> {
			MultipartFile mpf = new MockMultipartFile("teste.txt", "teste.txt", "application/txt", tempFileInBytes);
			imageService.edit(mpf, 4l);
		});

		verify(imageRepository, never()).save(any());
		assertThat(exception.getMessage(), equalTo("File extension not valid!"));

	}

}
