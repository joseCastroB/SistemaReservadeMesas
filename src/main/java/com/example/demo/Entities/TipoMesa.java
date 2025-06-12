package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "TipoMesa")
public class TipoMesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoMesa;
    private String nombre;
    private String descripcion;

    @OneToMany(mappedBy = "tipoMesa")
    private List<Mesa> mesas;
}
