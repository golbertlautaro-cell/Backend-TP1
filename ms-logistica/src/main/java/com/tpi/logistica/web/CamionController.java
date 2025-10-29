package com.tpi.logistica.web;

import com.tpi.logistica.domain.Camion;
import com.tpi.logistica.service.CamionService;
import com.tpi.logistica.web.dto.CapacidadRequest;
import com.tpi.logistica.web.dto.CapacidadResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/camiones")
public class CamionController {

    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    @GetMapping
    public Page<Camion> listar(Pageable pageable,
                               @RequestParam(required = false) Double minCapacidadPeso,
                               @RequestParam(required = false) Double maxCapacidadPeso,
                               @RequestParam(required = false) Double minCapacidadVolumen,
                               @RequestParam(required = false) Double maxCapacidadVolumen) {
        return camionService.listar(pageable, minCapacidadPeso, maxCapacidadPeso, minCapacidadVolumen, maxCapacidadVolumen);
    }

    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> obtenerEstado() {
        Map<String, Object> estado = camionService.obtenerEstadoCamiones();
        return ResponseEntity.ok(estado);
    }

    @GetMapping("/{dominio}")
    public Camion obtener(@PathVariable String dominio) {
        return camionService.obtener(dominio);
    }

    @PostMapping
    public ResponseEntity<Camion> crear(@RequestBody @Valid Camion camion) {
        Camion creado = camionService.crear(camion);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{dominio}")
    public Camion actualizar(@PathVariable String dominio, @RequestBody @Valid Camion camion) {
        return camionService.actualizar(dominio, camion);
    }

    @DeleteMapping("/{dominio}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String dominio) {
        camionService.eliminar(dominio);
    }

    @PostMapping("/validar-capacidad")
    public ResponseEntity<CapacidadResponse> validarCapacidad(@RequestBody @Valid CapacidadRequest request) {
        boolean valido = camionService.validarCapacidad(
                request.dominio(), request.pesoContenedor(), request.volumenContenedor()
        );
        return ResponseEntity.ok(new CapacidadResponse(valido));
    }
}
