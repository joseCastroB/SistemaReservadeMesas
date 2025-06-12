package com.example.demo.dto;

import lombok.Data;

@Data
public class ReservaFormDTO {
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private String comentarios;
    private String fecha; // Se recibe como String y se convierte
    private Integer numeroPersonas;
    private Integer idFranja;
    private Integer idTipoMesa;
}