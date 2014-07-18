package com.hybrid.app.application.exceptions;

/**
 * Exception must  e fired when the "app.properties" is invalid.
 * For example:
 *
 * app_config=....
 * app_config_fallback=....
 *
 * These two properties must be included in.
 */
public final class InvalidAppPropertiesException extends RuntimeException{
	@Override
	public String getMessage() {
		return "app.properties doesn't have standard properties like\napp_config\napp_config_fallback\netc.";
	}
}
