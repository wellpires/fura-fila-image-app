package br.com.furafila.imageapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.furafila.imageapp.response.ErrorResponse;

@RestControllerAdvice
public class ImageControllerAdvice {

	private static final Logger logger = LoggerFactory.getLogger(ImageControllerAdvice.class);

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {

		logger.error(ex.getMessage(), ex);

		return ResponseEntity.internalServerError().build();

	}

}
