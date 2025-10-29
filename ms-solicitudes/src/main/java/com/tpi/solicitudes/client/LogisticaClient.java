package com.tpi.solicitudes.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class LogisticaClient {

    private final WebClient webClient;

    public LogisticaClient(WebClient webClientLogistica) {
        this.webClient = webClientLogistica;
    }

    /**
     * Consulta el estado de los camiones (libres/ocupados) en ms-logistica.
     */
    public Mono<Map<String, Object>> obtenerEstadoCamiones() {
        return webClient.get()
                .uri("/api/camiones/estado")
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
    }

    /**
     * Valida si un camión tiene capacidad suficiente para un contenedor.
     * Implementa RF11 consultando ms-logistica.
     */
    public Mono<Boolean> validarCapacidadCamion(String dominio, Double peso, Double volumen) {
        Map<String, Object> request = Map.of(
                "dominio", dominio,
                "pesoContenedor", peso,
                "volumenContenedor", volumen
        );

    return webClient.post()
        .uri("/api/camiones/validar-capacidad")
        .bodyValue(request)
        .retrieve()
        .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
        .map(resp -> resp != null && Boolean.TRUE.equals(resp.get("valido")));
    }

    /**
     * Obtiene los datos de un camión específico por su dominio.
     */
    public Mono<Map<String, Object>> obtenerCamion(String dominio) {
        return webClient.get()
                .uri("/api/camiones/{dominio}", dominio)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
    }
}
