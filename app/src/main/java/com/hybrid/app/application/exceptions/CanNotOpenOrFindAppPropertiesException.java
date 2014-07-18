package com.hybrid.app.application.exceptions;

/**
 * The exception must be fired if the "app.properties" can not be found or be opened.
 */
public final class CanNotOpenOrFindAppPropertiesException extends RuntimeException{
	@Override
	public String getMessage() {
		return "Can't find app.properties.";
	}
}
