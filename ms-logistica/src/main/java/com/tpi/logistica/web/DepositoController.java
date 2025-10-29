package com.tpi.logistica.web;

import com.tpi.logistica.domain.Deposito;
import com.tpi.logistica.service.DepositoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/depositos")
public class DepositoController {

    private final DepositoService depositoService;

    public DepositoController(DepositoService depositoService) {
        this.depositoService = depositoService;
    }

    @GetMapping
    public Page<Deposito> listar(Pageable pageable) {
        return depositoService.listar(pageable);
    }

    @GetMapping("/{id}")
    public Deposito obtener(@PathVariable Long id) {
        return depositoService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Deposito> crear(@RequestBody @Valid Deposito deposito) {
        Deposito creado = depositoService.crear(deposito);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public Deposito actualizar(@PathVariable Long id, @RequestBody @Valid Deposito deposito) {
        return depositoService.actualizar(id, deposito);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        depositoService.eliminar(id);
    }
}
