package com.tpi.solicitudes.service;

import com.tpi.solicitudes.domain.EstadoTramo;
import com.tpi.solicitudes.domain.Solicitud;
import com.tpi.solicitudes.domain.Tramo;
import com.tpi.solicitudes.repository.SolicitudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.tpi.solicitudes.repository.TramoRepository;
import com.tpi.solicitudes.client.LogisticaClient;
import com.tpi.solicitudes.client.GoogleMapsClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class TramoService {

    private final TramoRepository tramoRepository;
    private final SolicitudRepository solicitudRepository;
    private final LogisticaClient logisticaClient;
    private final GoogleMapsClient googleMapsClient;

    public TramoService(TramoRepository tramoRepository,
                        SolicitudRepository solicitudRepository,
                        LogisticaClient logisticaClient,
                        GoogleMapsClient googleMapsClient) {
        this.tramoRepository = tramoRepository;
        this.solicitudRepository = solicitudRepository;
        this.logisticaClient = logisticaClient;
        this.googleMapsClient = googleMapsClient;
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

    public Mono<Tramo> asignarACamion(Long idTramo, String dominioCamion) {
        // NOTA: No tenemos peso/volumen del contenedor en el modelo actual.
        // Por ahora, se parametriza con 0.0. Ajustar cuando haya origen real de datos.
        Double pesoContenedor = 0.0;
        Double volumenContenedor = 0.0;

        return Mono.fromCallable(() -> obtener(idTramo))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(tramo -> logisticaClient.validarCapacidadCamion(dominioCamion, pesoContenedor, volumenContenedor)
                        .defaultIfEmpty(false)
                        .flatMap(valido -> {
                            if (!valido) {
                                return Mono.error(new IllegalStateException("Capacidad insuficiente del camión para el contenedor"));
                            }
                            tramo.setDominioCamion(dominioCamion);
                            tramo.setEstado(EstadoTramo.ASIGNADO);
                            return Mono.fromCallable(() -> tramoRepository.save(tramo))
                                    .subscribeOn(Schedulers.boundedElastic());
                        })
                );
    }

    // Alias con el nombre solicitado
    public Mono<Tramo> asignarCamion(Long idTramo, String dominioCamion) {
        return asignarACamion(idTramo, dominioCamion);
    }

    public Tramo iniciarTramo(Long idTramo) {
        Tramo tramo = obtener(idTramo);
        tramo.setEstado(EstadoTramo.INICIADO);
        tramo.setFechaHoraInicioReal(LocalDateTime.now());
        return tramoRepository.save(tramo);
    }

    public Tramo finalizarTramo(Long idTramo, LocalDateTime fechaHoraFin, Double odometroFinal, 
                                 Double costoReal, Double tiempoReal) {
        Tramo tramo = obtener(idTramo);
        tramo.setEstado(EstadoTramo.FINALIZADO);
        tramo.setFechaHoraFinReal(fechaHoraFin);
        tramo.setOdometroFinal(odometroFinal);
        tramo.setCostoReal(costoReal);
        tramo.setTiempoReal(tiempoReal);
        return tramoRepository.save(tramo);
    }

    /**
     * Calcula costo y tiempo estimado para un tramo usando Google Directions y datos del camión.
     * - Distancia (km) y duración (min) desde GoogleMapsClient.
     * - costoBaseKm del camión desde LogisticaClient.
     * Guarda costoAproximado, fechaHoraInicioEstimada y fechaHoraFinEstimada.
     */
    public Mono<Tramo> calcularCostoYTiempoEstimado(Long idTramo,
                                                    double origenLat, double origenLng,
                                                    double destinoLat, double destinoLng) {
        return Mono.fromCallable(() -> obtener(idTramo))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(tramo -> {
                    if (tramo.getDominioCamion() == null || tramo.getDominioCamion().isBlank()) {
                        return Mono.error(new IllegalStateException("El tramo no tiene camión asignado"));
                    }
                    String dominio = tramo.getDominioCamion();
                    return googleMapsClient.obtenerDistanciaYDuracion(origenLat, origenLng, destinoLat, destinoLng)
                            .zipWith(logisticaClient.obtenerCamion(dominio))
                            .flatMap(tuple -> {
                                Map<String, Object> direccionData = tuple.getT1();
                                Map<String, Object> camion = tuple.getT2();

                                Double distanciaKm = (Double) direccionData.get("distanciaKm");
                                Long duracionMinutos = (Long) direccionData.get("duracionMinutos");

                                Object costoBaseKmObj = camion != null ? camion.get("costoBaseKm") : null;
                                if (!(costoBaseKmObj instanceof Number)) {
                                    return Mono.error(new IllegalStateException("costoBaseKm no disponible para camión: " + dominio));
                                }
                                double costoBaseKm = ((Number) costoBaseKmObj).doubleValue();
                                double costoEstimado = distanciaKm * costoBaseKm;
                                tramo.setCostoAproximado(costoEstimado);

                                // Usa la duración real de Google Maps
                                if (tramo.getFechaHoraInicioEstimada() == null) {
                                    tramo.setFechaHoraInicioEstimada(LocalDateTime.now());
                                }
                                tramo.setFechaHoraFinEstimada(tramo.getFechaHoraInicioEstimada().plusMinutes(duracionMinutos));

                                return Mono.fromCallable(() -> tramoRepository.save(tramo))
                                        .subscribeOn(Schedulers.boundedElastic());
                            });
                });
    }
}
