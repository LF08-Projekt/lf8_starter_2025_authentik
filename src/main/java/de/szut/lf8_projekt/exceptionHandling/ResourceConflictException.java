package de.szut.lf8_projekt.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wird geworfen, wenn ein Ressourcenkonflikt vorliegt (HTTP 409).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceConflictException extends RuntimeException {
    /**
     * Erzeugt eine neue Ausnahme mit einer Fehlermeldung.
     *
     * @param message Fehlermeldung
     */
    public ResourceConflictException(String message) {
        super(message);
    }
}
