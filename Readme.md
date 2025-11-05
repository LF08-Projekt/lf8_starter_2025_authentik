# LF08 Projekt - Projektverwaltung API

Spring Boot REST API für die Verwaltung von Projekten, Mitarbeitern und Qualifikationen mit OAuth2/JWT-Authentifizierung via Authentik.

## Technologie-Stack

- **Java**: 22
- **Framework**: Spring Boot 3.3.3
- **Build-Tool**: Gradle 8.8
- **Datenbank**: PostgreSQL 14
- **Authentifizierung**: OAuth2/JWT via Authentik
- **API-Dokumentation**: Swagger/OpenAPI

## Requirements

- Docker: https://docs.docker.com/get-docker/
- Docker Compose (bei Windows und Mac in Docker enthalten)
- Java 22

## Schnellstart

### 1. Datenbank starten

```bash
docker compose up
```

**Hinweis:** Der Docker-Container läuft dauerhaft! Stoppen Sie ihn, wenn er nicht mehr benötigt wird.

### 2. Anwendung starten

```bash
./gradlew bootRun
```

### 3. Zugriff

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger
- **PostgreSQL**: localhost:5432
  - Datenbank: `lf8Starter`
  - Username: `user`
  - Passwort: `secret`

## Gradle-Befehle

```bash
# Projekt bauen
./gradlew build

# Anwendung starten
./gradlew bootRun

# Tests ausführen
./gradlew test

# Einzelne Testklasse ausführen
./gradlew test --tests "de.szut.lf8_projekt.projekt.ProjektIT"

# Einzelne Testmethode ausführen
./gradlew test --tests "de.szut.lf8_projekt.projekt.ProjektIT.testCreateProjekt"
```

## Datenbank-Verwaltung

### Datenbank stoppen

```bash
docker compose down
```

### Datenbank zurücksetzen (alle Daten löschen)

```bash
docker compose down
docker volume rm lf8_starter_postgres_data
docker compose up
```

### PostgreSQL in IntelliJ einrichten

1. Docker-Container mit PostgreSQL starten
2. In `src/main/resources/application.properties` die Datenbank-URL kopieren
3. Im IntelliJ rechts den Reiter "Database" öffnen
4. Auf das Datenbanksymbol mit Schlüssel klicken (Data Source Properties)
5. Auf Pluszeichen klicken → "Data Source" → "PostgreSQL"
6. Alternativ: "Data Source from URL" auswählen
7. URL einfügen: `jdbc:postgresql://localhost:5432/lf8Starter`
8. Username: `user`, Passwort: `secret` (siehe application.properties)
9. Im Reiter "Schemas" nur `lf8starter` und `public` auswählen
10. Mit "Apply" und "OK" bestätigen

## Authentifizierung

Die API nutzt Authentik für OAuth2/JWT-Authentifizierung.

## Architektur

### Domain Model

Das Projekt verwaltet folgende Entitäten:

- **ProjektEntity**: Projekte mit Bezeichnung, Kunden-ID, Verantwortlichen-ID, Start-/Enddaten
- **GeplanteQualifikationEntity**: Benötigte Qualifikationen für Projekte
- **MitarbeiterZuordnungEntity**: Zuordnung von Mitarbeitern zu Projekten

### Projektstruktur

```
de.szut.lf8_projekt/
├── projekt/                      # Projektverwaltung (Hauptdomäne)
│   ├── geplante_qualifikation/   # Geplante Qualifikationen
│   └── mitarbeiter_zuordnung/    # Mitarbeiter-Zuordnungen
├── mapping/                       # DTO-Mapper
├── mitarbeiter/                   # Mitarbeiter-DTOs
├── config/                        # OpenAPI/Swagger-Konfiguration
├── security/                      # OAuth2/JWT Security
├── exceptionHandling/             # Globale Exception-Handler
└── testcontainers/                # Integration-Test Basis
```

### Schichtenarchitektur

- **Controller** (`*Controller.java`): REST-Endpoints
- **Service** (`*Service.java`): Business-Logik
- **Repository** (`*Repository.java`): Datenbankzugriff (JPA)
- **Entity** (`*Entity.java`): Datenbank-Entitäten
- **DTO** (`dto/*Dto.java`): Data Transfer Objects

## Testing

### Integration-Tests ausführen

```bash
./gradlew test
```

**Test-Setup:**
- Tests mit Suffix `IT.java` sind Integration-Tests
- Nutzen Testcontainers mit PostgreSQL 14
- Datenbank wird vor jedem Test geleert
- Profil: `it` (Integration-Test)

**Beispiel-Test:**
```java
@Test
@WithMockUser(roles = "user")
void testCreateProjekt() throws Exception {
    mockMvc.perform(post("/LF08Projekt/Projekt")
        .content(jsonContent)
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf()))
        .andExpect(status().isCreated());
}
```

## Externe API-Integration

Die Anwendung integriert eine externe Mitarbeiter-API:

- **Base URL**: https://employee-api.szut.dev
- **Endpoints**: `/employees`, `/qualifications`
- **Authentifizierung**: JWT Bearer Token (von Authentik)
- Beispielanfragen in [SampleRequests.http](SampleRequests.http)

## Troubleshooting

### Port 8080 bereits belegt

```bash
# Prozess auf Port 8080 finden
lsof -i :8080

# Prozess beenden (PID aus vorherigem Befehl) - vorher prüfen ob Prozess bedenkenlos gekillt werden kann
kill <PID>
```

### Datenbank-Probleme

```bash
# Datenbank komplett zurücksetzen
docker compose down
docker volume rm lf8_starter_postgres_data
docker compose up
```

### Tests schlagen fehl

```bash
# Cache leeren und neu bauen
./gradlew clean build
```
# Projekt anlegen
+ Es wird geprüft, ob das geplante Enddatum auch nach dem Startdatum ist (+ Unittest dazu); Hatten wir nicht in die Anforderungen geschrieben

# Projekt bearbeiten
+ Wenn das geplante Enddatum eines Projekts geändert wird und einer der im Projekt eingeplanten Mitarbeiter zu diesem Zeitpunkt bereits in einem
  anderen Projekt eingeplant ist, dann wird eine 409 (Conflict)-Response zurückgegeben, mit entsprechender Nachricht
+ Es wird geprüft, ob das geplante/wirkliche Enddatum auch nach dem Startdatum ist (+ Unittest dazu)

# Projekt nach Id abrufen
+ Die noch fehlenden Qualifikationen werden mit ausgegeben (weil noch kein Mitarbeiter mit den entsprechenden Qualifikationen hinzugefügt wurde)
