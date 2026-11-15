package com.pr06.asistencia.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javax.validation.constraints.*;

public class LecturaIoTDto {
    @NotNull @Min(1) public Integer idDispositivo;
    @NotNull @Min(1) public Integer idSesion;
    @NotNull public OffsetDateTime fechaHora;
    @NotBlank public String variable;
    @NotBlank public String unidad;
    @NotNull @DecimalMin("0") @DecimalMax("100") public BigDecimal valor;
    @NotBlank public String origen;
}
