package ru.msk.ehome.mining.traiderbot.thingspeak;

/**
 * Thrown when the ThingSpeak API rejects requests due to invalid API keys,
 * arguments, or data formats.
 */
public class ThingSpeakException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ThingSpeakException(String message) {
		super(message);
	}
}