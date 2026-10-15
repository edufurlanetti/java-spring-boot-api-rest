package br.edu.furlanetti.manutencao.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

/**
 * Configurações gerais da API.
 * VIA_DTO serializa as páginas em um formato JSON estável (conteúdo + metadados).
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@OpenAPIDefinition(info = @Info(
        title = "API de Ordens de Serviço de Manutenção",
        version = "1.0.0",
        description = "Gestão de equipamentos e ordens de manutenção corretiva, preventiva e preditiva.",
        contact = @Contact(name = "Prof. Eduardo Furlanetti", url = "https://github.com/edufurlanetti"),
        license = @License(name = "MIT")))
public class WebConfig {
}
