package com.tpi.solicitudes.web;

import com.tpi.solicitudes.domain.EstadoSolicitud;
import com.tpi.solicitudes.domain.Solicitud;
import com.tpi.solicitudes.service.SolicitudService;
import com.tpi.solicitudes.web.dto.CrearSolicitudRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Solicitud> listar(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Solicitud obtener(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENTE')")
    public Solicitud crear(@RequestBody @Valid CrearSolicitudRequest request) {
        Solicitud solicitud = Solicitud.builder()
                .idContenedor(request.idContenedor())
                .idCliente(request.idCliente())
                .estado(EstadoSolicitud.BORRADOR)
                .build();
        return service.create(solicitud);
    }

    @PutMapping("/{id}")
    public Solicitud actualizar(@PathVariable Long id, @RequestBody @Valid Solicitud s) {
        return service.update(id, s);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.delete(id);
    }
}
