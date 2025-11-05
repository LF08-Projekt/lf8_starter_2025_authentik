package de.szut.lf8_projekt.exceptionHandling;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Standardisierte Fehlerantwort mit Zeitstempel, Nachricht und Details (z. B. Request-URI).
 */
@Data
@AllArgsConstructor
public class ErrorDetails {
    /** Zeitpunkt des Fehlers */
    private LocalDateTime timestamp;
    /** Fehlernachricht */
    private String message;
    /** Zusatzinformationen, z. B. die Request-URI */
    private String details;
}
