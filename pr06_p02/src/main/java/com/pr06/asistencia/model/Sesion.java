package com.pr06.asistencia.model;

import java.io.Serializable;

import java.time.LocalDate;
import java.time.LocalTime;

public class Sesion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int idSesion;
    private final String nombre;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final String estado;
    private final String descripcion;

    public Sesion(
            int idSesion,
            String nombre,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            String estado,
            String descripcion) {
        this.idSesion = idSesion;
        this.nombre = nombre;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    public int getIdSesion() {
        return idSesion;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
