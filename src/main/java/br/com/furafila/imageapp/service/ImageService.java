package br.com.furafila.imageapp.service;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

	Long save(MultipartFile file) throws IOException;

	Resource findImageById(Long id) throws IOException;

	void edit(MultipartFile file, Long id) throws IOException;

}
