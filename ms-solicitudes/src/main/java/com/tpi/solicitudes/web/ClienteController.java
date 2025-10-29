package com.tpi.solicitudes.web;

import com.tpi.solicitudes.domain.Cliente;
import com.tpi.solicitudes.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Cliente> listar(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente crear(@RequestBody @Valid Cliente c) {
        return service.create(c);
    }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Long id, @RequestBody @Valid Cliente c) {
        return service.update(id, c);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{idCliente}/contenedores")
    public List<Map<String, Object>> listarContenedores(@PathVariable Long idCliente) {
        // Simulación: verificar que el cliente existe
        service.findById(idCliente);
        
        // Retornar lista simulada de contenedores
        return List.of(
            Map.of("idContenedor", 1L, "tipo", "Refrigerado", "capacidadPeso", 15000.0, "capacidadVolumen", 30.0),
            Map.of("idContenedor", 2L, "tipo", "Seco", "capacidadPeso", 20000.0, "capacidadVolumen", 35.0)
        );
    }
}
