package com.tpi.solicitudes.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
    public Map<String, Object> obtenerEstadoCamiones() {
        return webClient.get()
                .uri("/api/camiones/estado")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    /**
     * Valida si un camión tiene capacidad suficiente para un contenedor.
     * Implementa RF11 consultando ms-logistica.
     */
    public boolean validarCapacidadCamion(String dominio, Double peso, Double volumen) {
        Map<String, Object> request = Map.of(
                "dominio", dominio,
                "pesoContenedor", peso,
                "volumenContenedor", volumen
        );

        Map<String, Object> response = webClient.post()
                .uri("/api/camiones/validar-capacidad")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response != null && Boolean.TRUE.equals(response.get("valido"));
    }

    /**
     * Obtiene los datos de un camión específico por su dominio.
     */
    public Map<String, Object> obtenerCamion(String dominio) {
        return webClient.get()
                .uri("/api/camiones/{dominio}", dominio)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
