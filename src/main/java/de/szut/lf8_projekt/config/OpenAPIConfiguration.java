package de.szut.lf8_projekt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguration für die OpenAPI/Swagger-Dokumentation.
 * Definiert Server-URL, Metadaten sowie das verwendete Security-Schema (Bearer/JWT).
 */
@Configuration
public class OpenAPIConfiguration {

    private final ServletContext context;

    /**
     * Erzeugt die OpenAPI-Konfiguration mit Zugriff auf den aktuellen Context-Path.
     *
     * @param context aktueller {@link ServletContext} zur Ermittlung des Context-Path
     */
    public OpenAPIConfiguration(ServletContext context) {
        this.context = context;
    }

    /**
     * Stellt die OpenAPI-Instanz bereit, inklusive Server, Info und Security-Definitionen.
     *
     * @return konfigurierte {@link OpenAPI}-Instanz
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addServersItem(new Server().url(this.context.getContextPath()))
                .info(new Info()
                        .title("LF08 Projekt-Management API")
                        .description("# Projekt-Management System\n\n" +
                                "REST API zur Verwaltung von Software-Projekten, Mitarbeiterzuordnungen und benötigten Qualifikationen.\n\n" +
                                "## Features\n" +
                                "- Projektverwaltung (anlegen, bearbeiten, löschen, abrufen)\n" +
                                "- Mitarbeiterzuordnung zu Projekten\n" +
                                "- Qualifikationsplanung und -tracking\n" +
                                "- Berechnung fehlender Qualifikationen\n\n" +
                                "## Authentication\n\n" +
                                "Diese API verwendet JWT-Tokens zur Authentifizierung über Authentik.\n\n" +
                                "### Token abrufen\n\n" +
                                "**Mit IntelliJ HTTP Client:**\n" +
                                "```http\n" +
                                "POST https://authentik.szut.dev/application/o/token/\n" +
                                "Content-Type: application/x-www-form-urlencoded\n\n" +
                                "grant_type=password&username=<username>&password=<app-password>&client_id=hitec_api_client\n" +
                                "```\n\n" +
                                "**Mit cURL:**\n" +
                                "```bash\n" +
                                "curl -X POST 'https://authentik.szut.dev/application/o/token/' \\\n" +
                                "  -H 'Content-Type: application/x-www-form-urlencoded' \\\n" +
                                "  -d 'grant_type=password' \\\n" +
                                "  -d 'client_id=hitec_api_client' \\\n" +
                                "  -d 'username=<username>' \\\n" +
                                "  -d 'password=<app-password>'\n" +
                                "```\n\n" +
                                "**Hinweis:** Das Passwort muss ein App-Passwort sein, das in Authentik unter *Directory → Token and App Passwords* generiert wurde.\n\n" +
                                "### Token verwenden\n\n" +
                                "Fügen Sie den erhaltenen Token im Authorization-Header hinzu:\n" +
                                "```\n" +
                                "Authorization: Bearer <your-token>\n" +
                                "```\n\n" +
                                "Nutzen Sie in Swagger UI den **Authorize**-Button oben rechts, um den Token einzutragen.")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }

}
