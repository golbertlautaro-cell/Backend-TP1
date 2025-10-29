package com.tpi.solicitudes.repository;

import com.tpi.solicitudes.domain.Tramo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {
    List<Tramo> findBySolicitud_Id(Long solicitudId); // legacy
    Page<Tramo> findBySolicitud_Id(Long solicitudId, Pageable pageable);

    Page<Tramo> findByEstado(String estado, Pageable pageable);
    Page<Tramo> findByDominioCamion(String dominioCamion, Pageable pageable);
    Page<Tramo> findByEstadoAndDominioCamion(String estado, String dominioCamion, Pageable pageable);

    // Rango por fechaHoraInicioReal
    Page<Tramo> findByFechaHoraInicioRealBetween(java.time.LocalDateTime desde, java.time.LocalDateTime hasta, Pageable pageable);
    Page<Tramo> findByEstadoAndFechaHoraInicioRealBetween(String estado, java.time.LocalDateTime desde, java.time.LocalDateTime hasta, Pageable pageable);
    Page<Tramo> findByDominioCamionAndFechaHoraInicioRealBetween(String dominioCamion, java.time.LocalDateTime desde, java.time.LocalDateTime hasta, Pageable pageable);
    Page<Tramo> findByEstadoAndDominioCamionAndFechaHoraInicioRealBetween(String estado, String dominioCamion, java.time.LocalDateTime desde, java.time.LocalDateTime hasta, Pageable pageable);
}
