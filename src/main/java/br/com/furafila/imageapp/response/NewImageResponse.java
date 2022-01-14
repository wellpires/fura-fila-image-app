package br.com.furafila.imageapp.response;

public class NewImageResponse {

	private Long id;

	public NewImageResponse(Long id) {
		this.id = id;
	}

	public NewImageResponse() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
