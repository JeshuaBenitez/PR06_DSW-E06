package com.pr06.asistencia.model;

import java.io.Serializable;

public class Participante implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int idParticipante;
    private final String codigoFicticio;
    private final String nombre;
    private final String apellido;
    private final String estado;

    public Participante(
            int idParticipante,
            String codigoFicticio,
            String nombre,
            String apellido,
            String estado) {
        this.idParticipante = idParticipante;
        this.codigoFicticio = codigoFicticio;
        this.nombre = nombre;
        this.apellido = apellido;
        this.estado = estado;
    }

    public int getIdParticipante() {
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
