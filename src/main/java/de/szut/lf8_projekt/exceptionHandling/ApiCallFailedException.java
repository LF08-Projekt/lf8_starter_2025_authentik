package de.szut.lf8_projekt.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wird geworfen, wenn ein Aufruf eines externen Dienstes fehlschlägt.
 */
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class ApiCallFailedException extends RuntimeException {
    /**
     * Erzeugt eine neue Ausnahme mit einer Fehlermeldung.
     *
     * @param message Fehlermeldung
     */
    public ApiCallFailedException(String message) {
        super(message);
    }
}
