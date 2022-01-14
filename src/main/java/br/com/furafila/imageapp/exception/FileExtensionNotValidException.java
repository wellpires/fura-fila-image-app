package br.com.furafila.imageapp.exception;

public class FileExtensionNotValidException extends RuntimeException {

	private static final long serialVersionUID = 1190608966314492127L;

	public FileExtensionNotValidException() {
		super("File extension not valid!");
	}

}
