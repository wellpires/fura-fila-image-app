package br.com.furafila.imageapp.controller.resource;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import br.com.furafila.imageapp.response.NewImageResponse;

public interface ImageResource {

	ResponseEntity<NewImageResponse> save(MultipartFile file) throws IOException;

	ResponseEntity<Void> edit(MultipartFile file, Long id) throws IOException;

	ResponseEntity<Resource> findImageById(Long id) throws IOException;

}
