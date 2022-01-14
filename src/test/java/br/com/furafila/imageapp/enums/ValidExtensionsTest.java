package br.com.furafila.imageapp.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidExtensionsTest {

	@Test
	public void shouldReturnTrueBecauseExtensionIsValid() {
		Assertions.assertTrue(ValidExtensions.isExtensionValid("png"));
	}

	@Test
	public void shouldReturnFalseBecauseExtensionIsNotValid() {
		Assertions.assertFalse(ValidExtensions.isExtensionValid("txt"));
	}

}
