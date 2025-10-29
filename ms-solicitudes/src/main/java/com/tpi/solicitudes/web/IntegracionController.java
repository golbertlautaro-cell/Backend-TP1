package com.tpi.solicitudes.web;

import com.tpi.solicitudes.client.LogisticaClient;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Object>> obtenerEstadoCamiones() {
        Map<String, Object> estado = logisticaClient.obtenerEstadoCamiones();
        return ResponseEntity.ok(estado);
    }

    /**
     * Endpoint para validar capacidad de un camión consultando ms-logistica.
     */
    @PostMapping("/camiones/validar-capacidad")
    public ResponseEntity<Map<String, Object>> validarCapacidad(
            @RequestParam String dominio,
            @RequestParam Double peso,
            @RequestParam Double volumen) {
        
        boolean valido = logisticaClient.validarCapacidadCamion(dominio, peso, volumen);
        return ResponseEntity.ok(Map.of("valido", valido));
    }

    /**
     * Endpoint para obtener datos de un camión desde ms-logistica.
     */
    @GetMapping("/camiones/{dominio}")
    public ResponseEntity<Map<String, Object>> obtenerCamion(@PathVariable String dominio) {
        Map<String, Object> camion = logisticaClient.obtenerCamion(dominio);
        return ResponseEntity.ok(camion);
    }
}
