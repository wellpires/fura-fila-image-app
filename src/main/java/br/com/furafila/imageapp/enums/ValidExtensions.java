package br.com.furafila.imageapp.enums;

import java.util.Arrays;

public enum ValidExtensions {

	PNG(".png"), JPG(".jpg"), JPEG(".jpeg");

	private String extension;

	ValidExtensions(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}

	public static boolean isExtensionValid(String extension) {
		return Arrays.asList(values()).stream().map(String::valueOf).filter(extension::equalsIgnoreCase).count() > 0;
	}

}
