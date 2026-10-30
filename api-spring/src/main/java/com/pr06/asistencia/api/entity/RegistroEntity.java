package com.pr06.asistencia.api.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
    name = "registro",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"id_sesion", "id_participante", "tipo_registro"}
    )
)
public class RegistroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Integer idRegistro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sesion", nullable = false)
    private SesionEntity sesion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_participante", nullable = false)
    private ParticipanteEntity participante;

    @Column(name = "tipo_registro", nullable = false, length = 20)
    private String tipoRegistro;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "observacion", length = 200)
    private String observacion;

    protected RegistroEntity() {
    }

    public RegistroEntity(
            SesionEntity sesion,
            ParticipanteEntity participante,
            String tipoRegistro,
            String observacion) {
        this.sesion = sesion;
        this.participante = participante;
        this.tipoRegistro = tipoRegistro;
        this.observacion = observacion;
    }

    @PrePersist
    void asignarFechaHora() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public SesionEntity getSesion() {
        return sesion;
    }

    public ParticipanteEntity getParticipante() {
        return participante;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getObservacion() {
        return observacion;
    }
}
