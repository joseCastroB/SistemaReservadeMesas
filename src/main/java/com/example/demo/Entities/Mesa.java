package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Mesa")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMesa;
    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "idTipoMesa", nullable = false)
    private TipoMesa tipoMesa;
}