package de.szut.lf8_projekt.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class ApiCallFailedException extends RuntimeException {
    public ApiCallFailedException(String message) {
        super(message);
    }
}
