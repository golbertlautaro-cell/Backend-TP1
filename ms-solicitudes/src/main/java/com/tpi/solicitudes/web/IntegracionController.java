package com.tpi.solicitudes.web;

import com.tpi.solicitudes.client.LogisticaClient;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/integracion")
public class IntegracionController {

    private final LogisticaClient logisticaClient;

    public IntegracionController(LogisticaClient logisticaClient) {
        this.logisticaClient = logisticaClient;
    }

    /**
     * Endpoint que consulta el estado de camiones desde ms-logistica.
     */
    @GetMapping("/camiones/estado")
    public Mono<Map<String, Object>> obtenerEstadoCamiones() {
        return logisticaClient.obtenerEstadoCamiones();
    }

    /**
     * Endpoint para validar capacidad de un camión consultando ms-logistica.
     */
    @PostMapping("/camiones/validar-capacidad")
    public Mono<Map<String, Object>> validarCapacidad(
            @RequestParam String dominio,
            @RequestParam Double peso,
            @RequestParam Double volumen) {
        return logisticaClient.validarCapacidadCamion(dominio, peso, volumen)
                .map(valido -> Map.of("valido", valido));
    }

    /**
     * Endpoint para obtener datos de un camión desde ms-logistica.
     */
    @GetMapping("/camiones/{dominio}")
    public Mono<Map<String, Object>> obtenerCamion(@PathVariable String dominio) {
        return logisticaClient.obtenerCamion(dominio);
    }
}
