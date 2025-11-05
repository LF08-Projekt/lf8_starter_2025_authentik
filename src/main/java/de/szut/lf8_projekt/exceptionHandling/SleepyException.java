package de.szut.lf8_projekt.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_EARLY)
public class SleepyException extends RuntimeException {
    public SleepyException(String message) {
        super(message);
    }
}
