package com.tpi.solicitudes.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tramos")
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramo")
    private Long idTramo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nro_solicitud", nullable = false)
    @JsonIgnore
    private Solicitud solicitud;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String origen;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String destino;

    @Pattern(regexp = "^([A-Z]{3}[0-9]{3}|[A-Z]{2}[0-9]{3}[A-Z]{2})$", message = "Formato de dominio inválido")
    @Column(name = "dominio_camion", length = 20)
    private String dominioCamion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 30)
    private EstadoTramo estado;

    @Column(name = "fecha_hora_inicio_real")
    private LocalDateTime fechaHoraInicioReal;

    @Column(name = "fecha_hora_fin_real")
    private LocalDateTime fechaHoraFinReal;

    @Column(name = "odometro_final")
    private Double odometroFinal;

    @Column(name = "costo_real")
    private Double costoReal;

    @Column(name = "tiempo_real")
    private Double tiempoReal;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "costo_aproximado")
    private Double costoAproximado;

    @Column(name = "fecha_hora_inicio_estimada")
    private LocalDateTime fechaHoraInicioEstimada;

    @Column(name = "fecha_hora_fin_estimada")
    private LocalDateTime fechaHoraFinEstimada;
}
