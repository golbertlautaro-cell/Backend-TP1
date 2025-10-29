package com.tpi.solicitudes.service;

import com.tpi.solicitudes.domain.Solicitud;
import com.tpi.solicitudes.repository.SolicitudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SolicitudService {

    private final SolicitudRepository repository;

    public SolicitudService(SolicitudRepository repository) {
        this.repository = repository;
    }

    public List<Solicitud> findAll() { // legacy
        return repository.findAll();
    }

    public Page<Solicitud> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Solicitud findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada: " + id));
    }

    public Solicitud create(Solicitud s) {
        s.setNroSolicitud(null); // Generado por BD
        return repository.save(s);
    }

    public Solicitud update(Long id, Solicitud s) {
        Solicitud actual = findById(id);
        actual.setIdContenedor(s.getIdContenedor());
        actual.setIdCliente(s.getIdCliente());
        actual.setEstado(s.getEstado());
        actual.setCostoEstimado(s.getCostoEstimado());
        actual.setCostoFinal(s.getCostoFinal());
        actual.setTiempoReal(s.getTiempoReal());
        return repository.save(actual);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Solicitud no encontrada: " + id);
        }
        repository.deleteById(id);
    }
}
