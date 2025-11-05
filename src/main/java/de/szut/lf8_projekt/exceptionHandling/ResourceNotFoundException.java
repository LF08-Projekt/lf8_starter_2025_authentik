package de.szut.lf8_projekt.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wird geworfen, wenn eine angeforderte Ressource nicht gefunden wurde.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Erzeugt eine neue Ausnahme mit einer Fehlermeldung.
     *
     * @param message Fehlermeldung
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
