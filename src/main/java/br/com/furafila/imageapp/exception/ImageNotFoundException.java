package br.com.furafila.imageapp.exception;

public class ImageNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6223617338695620801L;

	public ImageNotFoundException() {
		super("Image not found!");
	}

}
