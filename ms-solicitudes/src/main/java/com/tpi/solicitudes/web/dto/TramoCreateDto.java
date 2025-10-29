package com.tpi.solicitudes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record TramoCreateDto(
        @NotNull Long solicitudId,
        @NotBlank String origen,
        @NotBlank String destino,
        @Pattern(regexp = "^([A-Z]{3}[0-9]{3}|[A-Z]{2}[0-9]{3}[A-Z]{2})$", message = "Formato de dominio inválido") String dominioCamion,
        String estado,
        LocalDateTime fechaHoraInicioReal,
        LocalDateTime fechaHoraFinReal,
        Double costoReal
) {}
