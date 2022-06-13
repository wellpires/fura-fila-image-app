package br.com.furafila.imageapp.controller;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.furafila.imageapp.controller.resource.ImageResource;
import br.com.furafila.imageapp.response.NewImageResponse;
import br.com.furafila.imageapp.service.ImageService;

@RestController
@RequestMapping("image")
public class ImageController implements ImageResource {

	@Autowired
	private ImageService imageService;

	@Override
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NewImageResponse> save(@RequestParam("file") MultipartFile file) throws IOException {

		Long idImage = this.imageService.save(file);

		return ResponseEntity.ok(new NewImageResponse(idImage));
	}

	@Override
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Resource> findImageById(@PathVariable("id") Long id) throws IOException {

		Resource imageBytes = this.imageService.findImageById(id);

		ResponseEntity<Resource> response = ResponseEntity.ok().build();
		if (Objects.nonNull(imageBytes)) {
			response = ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION,
							String.format("%s", imageBytes.getFilename()))
					.body(imageBytes);
		}

		return response;
	}

	@Override
	@PutMapping(path = "/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Void> edit(@RequestParam("file") MultipartFile file, @PathVariable("id") Long id)
			throws IOException {

		this.imageService.edit(file, id);

		return ResponseEntity.noContent().build();
	}

}
