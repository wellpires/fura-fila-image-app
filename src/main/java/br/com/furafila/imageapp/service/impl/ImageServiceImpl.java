package br.com.furafila.imageapp.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.furafila.imageapp.enums.ValidExtensions;
import br.com.furafila.imageapp.exception.FileExtensionNotValidException;
import br.com.furafila.imageapp.exception.ImageNotFoundException;
import br.com.furafila.imageapp.model.Image;
import br.com.furafila.imageapp.repository.ImageRepository;
import br.com.furafila.imageapp.service.ImageService;

@Service
public class ImageServiceImpl implements ImageService {

	@Autowired
	private ImageRepository imageRepository;

	@Override
	public Long save(MultipartFile file) throws IOException {

		Image image = new Image();
		if (file.getBytes().length > 0) {
			image.setImage(file.getBytes());
			image.setFileExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
		}

		Image newImage = imageRepository.save(image);

		return newImage.getId();
	}

	@Override
	public Resource findImageById(Long id) throws IOException {

		Image image = imageRepository.findById(id).orElseThrow(ImageNotFoundException::new);

		UrlResource urlResource = null;
		if (Objects.nonNull(image.getImage()) && StringUtils.isNotBlank(image.getFileExtension())) {
			Path tempFile = Files.createTempFile("tmp", ".".concat(image.getFileExtension()));
			FileUtils.writeByteArrayToFile(tempFile.toFile(), image.getImage());

			urlResource = new UrlResource(tempFile.toUri());

		}

		return urlResource;
	}

	@Override
	public void edit(MultipartFile file, Long id) throws IOException {

		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (!ValidExtensions.isExtensionValid(extension)) {
			throw new FileExtensionNotValidException();
		}

		Image image = imageRepository.findById(id).orElseThrow(ImageNotFoundException::new);

		image.setImage(file.getBytes());
		image.setFileExtension(extension);

		imageRepository.save(image);

	}

}
