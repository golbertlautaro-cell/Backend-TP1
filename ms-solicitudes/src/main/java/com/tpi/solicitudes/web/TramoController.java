package com.tpi.solicitudes.web;

import com.tpi.solicitudes.domain.Tramo;
import com.tpi.solicitudes.service.TramoService;
import com.tpi.solicitudes.web.dto.AsignarCamionRequest;
import com.tpi.solicitudes.web.dto.FinalizarTramoRequest;
import com.tpi.solicitudes.web.dto.TramoAsignacionDTO;
import com.tpi.solicitudes.web.dto.TramoCreateDto;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
public class TramoController {

    private final TramoService service;

    public TramoController(TramoService service) {
        this.service = service;
    }

    @GetMapping("/solicitudes/{solicitudId}/tramos")
    public Page<Tramo> listarPorSolicitud(@PathVariable Long solicitudId, Pageable pageable) {
        return service.listarPorSolicitud(solicitudId, pageable);
    }

    // Endpoint adicional en /api/solicitudes (RESTful)
    @GetMapping("/api/solicitudes/{idSolicitud}/tramos")
    public Page<Tramo> listarPorSolicitudApi(@PathVariable Long idSolicitud, Pageable pageable) {
        return service.listarPorSolicitud(idSolicitud, pageable);
    }

    @PostMapping("/solicitudes/{solicitudId}/tramos")
    @ResponseStatus(HttpStatus.CREATED)
    public Tramo crear(@PathVariable Long solicitudId, @RequestBody @Valid Tramo t) {
        return service.crear(solicitudId, t);
    }

    @GetMapping("/tramos/{id}")
    public Tramo obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PutMapping("/tramos/{id}")
    public Tramo actualizar(@PathVariable Long id, @RequestBody @Valid Tramo t) {
        return service.actualizar(id, t);
    }

    @DeleteMapping("/tramos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    // New API base: /api/tramos
    @PostMapping("/api/tramos")
    public ResponseEntity<Tramo> crearDesdeDto(@RequestBody @Valid TramoCreateDto dto) {
        Tramo t = Tramo.builder()
                .origen(dto.origen())
                .destino(dto.destino())
                .dominioCamion(dto.dominioCamion())
                .estado(dto.estado())
                .fechaHoraInicioReal(dto.fechaHoraInicioReal())
                .fechaHoraFinReal(dto.fechaHoraFinReal())
                .costoReal(dto.costoReal())
                .build();
        Tramo creado = service.crear(dto.solicitudId(), t);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/api/tramos/{idTramo}/asignarACamion")
    public Mono<Tramo> asignarACamion(@PathVariable Long idTramo, @RequestBody @Valid TramoAsignacionDTO body) {
        return service.asignarACamion(idTramo, body.dominioCamion());
    }

    // Endpoint RESTful con guiones
    @PutMapping("/api/tramos/{idTramo}/asignar-camion")
    @PreAuthorize("hasRole('OPERADOR')")
    public Mono<Tramo> asignarCamion(@PathVariable Long idTramo, @RequestBody @Valid AsignarCamionRequest request) {
        return service.asignarACamion(idTramo, request.dominioCamion());
    }

    @PutMapping("/api/tramos/{idTramo}/iniciar")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public Tramo iniciar(@PathVariable Long idTramo) {
        return service.iniciarTramo(idTramo);
    }

    @PutMapping("/api/tramos/{idTramo}/finalizar")
    public Tramo finalizar(@PathVariable Long idTramo, @RequestBody @Valid FinalizarTramoRequest request) {
        return service.finalizarTramo(idTramo, request.fechaHoraFin(), request.odometroFinal(), 
                                       request.costoReal(), request.tiempoReal());
    }

    // Listado general paginado con filtros opcionales
    @GetMapping("/api/tramos")
    public Page<Tramo> listar(Pageable pageable,
                              @RequestParam(required = false) String estado,
                              @RequestParam(required = false) String dominioCamion,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime desde,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime hasta) {
        return service.listar(pageable, estado, dominioCamion, desde, hasta);
    }
}
