package com.pr06.asistencia.bean;

import com.pr06.asistencia.dao.ParticipanteDAO;
import com.pr06.asistencia.dao.RegistroDAO;
import com.pr06.asistencia.dao.SesionDAO;
import com.pr06.asistencia.model.Participante;
import com.pr06.asistencia.model.Registro;
import com.pr06.asistencia.model.Sesion;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;

@ManagedBean(name = "registroBean")
@ViewScoped
public class RegistroBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ENTRADA = "ENTRADA";
    private static final String DUPLICADO =
        "El participante ya tiene una entrada registrada en esta sesión.";

    private List<Sesion> sesiones = new ArrayList<>();
    private List<Participante> participantes = new ArrayList<>();
    private List<Registro> registros = new ArrayList<>();
    private Integer idSesion;
    private Integer idParticipante;
    private String tipoRegistro = ENTRADA;
    private String observacion;

    // f:viewAction invoca la carga inicial; no se consulta desde los getters.
    public void cargarSesiones() {
        try {
            sesiones = new SesionDAO().obtenerTodas();
        } catch (SQLException e) {
            informarError("No se pudieron cargar las sesiones.", e);
        }
    }

    public void cargarRegistros() {
        try {
            registros = new RegistroDAO().listarRegistros();
        } catch (SQLException e) {
            informarError("No se pudieron cargar los registros.", e);
        }
    }

    public void cargarFormulario() {
        cargarSesiones();
        try {
            participantes = new ParticipanteDAO().obtenerActivos();
        } catch (SQLException e) {
            informarError("No se pudieron cargar los participantes activos.", e);
        }
        cargarRegistros();
    }

    public void validarSesion(FacesContext contexto, UIComponent componente, Object valor) {
        if (valor != null && sesiones.stream().noneMatch(s -> valor.equals(s.getIdSesion()))) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Debe seleccionar una sesión válida.", null));
        }
    }

    public void validarParticipante(FacesContext contexto, UIComponent componente, Object valor) {
        if (valor != null && participantes.stream().noneMatch(p -> valor.equals(p.getIdParticipante()))) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Debe seleccionar un participante activo válido.", null));
        }
    }

    public void validarTipo(FacesContext contexto, UIComponent componente, Object valor) {
        if (valor != null && !ENTRADA.equals(valor)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Debe seleccionar ENTRADA como tipo de registro.", null));
        }
    }

    public void validarObservacion(FacesContext contexto, UIComponent componente, Object valor) {
        // PrimeFaces recorta a maxlength al decodificar: validar también el envío original.
        String enviada = contexto.getExternalContext().getRequestParameterMap()
            .get(componente.getClientId(contexto));
        if (enviada != null && enviada.length() > 200) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "La observación no puede exceder 200 caracteres.", null));
        }
    }

    public void registrar() {
        if (idSesion == null) {
            rechazar("Debe seleccionar una sesión.");
            return;
        }
        if (idParticipante == null) {
            rechazar("Debe seleccionar un participante.");
            return;
        }
        if (!ENTRADA.equals(tipoRegistro)) {
            rechazar("Debe seleccionar ENTRADA como tipo de registro.");
            return;
        }
        String observacionLimpia = observacion == null ? null : observacion.trim();
        if (observacionLimpia != null && observacionLimpia.length() > 200) {
            rechazar("La observación no puede exceder 200 caracteres.");
            return;
        }
        if (observacionLimpia != null && observacionLimpia.isEmpty()) {
            observacionLimpia = null;
        }

        RegistroDAO dao = new RegistroDAO();
        try {
            // Revalidar contra PostgreSQL: las opciones pudieron cambiar desde el GET.
            sesiones = new SesionDAO().obtenerTodas();
            participantes = new ParticipanteDAO().obtenerActivos();
            if (sesiones.stream().noneMatch(s -> s.getIdSesion() == idSesion)) {
                rechazar("Debe seleccionar una sesión válida.");
                return;
            }
            if (participantes.stream().noneMatch(p -> p.getIdParticipante() == idParticipante)) {
                rechazar("Debe seleccionar un participante activo válido.");
                return;
            }
            if (dao.existeRegistro(idSesion, idParticipante, ENTRADA)) {
                rechazar(DUPLICADO);
                return;
            }
            dao.insertarRegistro(new Registro(idSesion, idParticipante, ENTRADA, observacionLimpia));
        } catch (SQLException e) {
            if (esViolacionUnica(e)) {
                rechazar(DUPLICADO);
            } else {
                informarError("No se pudo registrar la entrada. Intente nuevamente.", e);
            }
            return;
        }

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Entrada registrada correctamente.", null));
        // Una falla al consultar no debe presentarse como falla de la inserción ya confirmada.
        try {
            registros = dao.listarRegistros();
        } catch (SQLException e) {
            informarError("La entrada se guardó, pero no se pudo actualizar la tabla. Vuelva a consultar los registros.", e);
        }
    }

    private boolean esViolacionUnica(SQLException e) {
        for (SQLException actual = e; actual != null; actual = actual.getNextException()) {
            if ("23505".equals(actual.getSQLState())) {
                return true;
            }
        }
        return false;
    }

    private void rechazar(String mensaje) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        contexto.validationFailed();
        contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));
    }

    private void informarError(String mensaje, SQLException e) {
        FacesContext.getCurrentInstance().getExternalContext().log(mensaje, e);
        rechazar(mensaje);
    }

    public List<Sesion> getSesiones() { return sesiones; }
    public List<Participante> getParticipantes() { return participantes; }
    public List<Registro> getRegistros() { return registros; }
    public Integer getIdSesion() { return idSesion; }
    public void setIdSesion(Integer idSesion) { this.idSesion = idSesion; }
    public Integer getIdParticipante() { return idParticipante; }
    public void setIdParticipante(Integer idParticipante) { this.idParticipante = idParticipante; }
    public String getTipoRegistro() { return tipoRegistro; }
    public void setTipoRegistro(String tipoRegistro) { this.tipoRegistro = tipoRegistro; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
