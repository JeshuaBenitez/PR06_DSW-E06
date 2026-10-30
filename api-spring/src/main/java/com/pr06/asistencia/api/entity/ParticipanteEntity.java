package com.pr06.asistencia.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "participante")
public class ParticipanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_participante")
    private Integer idParticipante;

    @Column(name = "codigo_ficticio", nullable = false, unique = true, length = 30)
    private String codigoFicticio;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 80)
    private String apellido;

    @Column(name = "estado", length = 20)
    private String estado;

    protected ParticipanteEntity() {
    }

    public ParticipanteEntity(
            String codigoFicticio,
            String nombre,
            String apellido,
            String estado) {
        this.codigoFicticio = codigoFicticio;
        this.nombre = nombre;
        this.apellido = apellido;
        this.estado = estado;
    }

    public Integer getIdParticipante() {
        return idParticipante;
    }

    public String getCodigoFicticio() {
        return codigoFicticio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEstado() {
        return estado;
    }
}
