package com.tpi.solicitudes.service;

import com.tpi.solicitudes.domain.Solicitud;
import com.tpi.solicitudes.domain.Tramo;
import com.tpi.solicitudes.repository.SolicitudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.tpi.solicitudes.repository.TramoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TramoService {

    private final TramoRepository tramoRepository;
    private final SolicitudRepository solicitudRepository;

    public TramoService(TramoRepository tramoRepository, SolicitudRepository solicitudRepository) {
        this.tramoRepository = tramoRepository;
        this.solicitudRepository = solicitudRepository;
    }

    public List<Tramo> listarPorSolicitud(Long solicitudId) { // legacy
        return tramoRepository.findBySolicitud_Id(solicitudId);
    }

    public Page<Tramo> listarPorSolicitud(Long solicitudId, Pageable pageable) {
        return tramoRepository.findBySolicitud_Id(solicitudId, pageable);
    }

    public Page<Tramo> listar(Pageable pageable, String estado, String dominioCamion,
                               LocalDateTime desde, LocalDateTime hasta) {
        boolean hasEstado = estado != null && !estado.isBlank();
        boolean hasDominio = dominioCamion != null && !dominioCamion.isBlank();

        // Normalizar rango si no viene alguno de los extremos
        LocalDateTime from = (desde != null) ? desde : LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime to = (hasta != null) ? hasta : LocalDateTime.of(9999, 12, 31, 23, 59, 59);

        if (hasEstado && hasDominio) {
            return tramoRepository.findByEstadoAndDominioCamionAndFechaHoraInicioRealBetween(estado, dominioCamion, from, to, pageable);
        } else if (hasEstado) {
            return tramoRepository.findByEstadoAndFechaHoraInicioRealBetween(estado, from, to, pageable);
        } else if (hasDominio) {
            return tramoRepository.findByDominioCamionAndFechaHoraInicioRealBetween(dominioCamion, from, to, pageable);
        } else {
            return tramoRepository.findByFechaHoraInicioRealBetween(from, to, pageable);
        }
    }

    public Tramo obtener(Long id) {
        return tramoRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Tramo no encontrado: " + id));
    }

    public Tramo crear(Long solicitudId, Tramo tramo) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada: " + solicitudId));
        tramo.setIdTramo(null);
        tramo.setSolicitud(solicitud);
        return tramoRepository.save(tramo);
    }

    public Tramo actualizar(Long id, Tramo tramo) {
        Tramo actual = obtener(id);
        actual.setOrigen(tramo.getOrigen());
        actual.setDestino(tramo.getDestino());
        actual.setDominioCamion(tramo.getDominioCamion());
        actual.setEstado(tramo.getEstado());
        actual.setFechaHoraInicioReal(tramo.getFechaHoraInicioReal());
        actual.setFechaHoraFinReal(tramo.getFechaHoraFinReal());
        actual.setCostoReal(tramo.getCostoReal());
        return tramoRepository.save(actual);
    }

    public void eliminar(Long id) {
        if (!tramoRepository.existsById(id)) {
            throw new NoSuchElementException("Tramo no encontrado: " + id);
        }
        tramoRepository.deleteById(id);
    }

    public Tramo asignarACamion(Long idTramo, String dominioCamion) {
        Tramo tramo = obtener(idTramo);
        tramo.setDominioCamion(dominioCamion);
        return tramoRepository.save(tramo);
    }

    // Alias con el nombre solicitado
    public Tramo asignarCamion(Long idTramo, String dominioCamion) {
        return asignarACamion(idTramo, dominioCamion);
    }

    public Tramo iniciarTramo(Long idTramo) {
        Tramo tramo = obtener(idTramo);
        tramo.setEstado("iniciado");
        tramo.setFechaHoraInicioReal(LocalDateTime.now());
        return tramoRepository.save(tramo);
    }

    public Tramo finalizarTramo(Long idTramo, LocalDateTime fechaHoraFin, Double odometroFinal, 
                                 Double costoReal, Double tiempoReal) {
        Tramo tramo = obtener(idTramo);
        tramo.setEstado("finalizado");
        tramo.setFechaHoraFinReal(fechaHoraFin);
        tramo.setOdometroFinal(odometroFinal);
        tramo.setCostoReal(costoReal);
        tramo.setTiempoReal(tiempoReal);
        return tramoRepository.save(tramo);
    }
}
