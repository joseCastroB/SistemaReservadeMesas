package com.example.demo.Repository;

import com.example.demo.Entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TipoMesaRepository extends JpaRepository<TipoMesa, Integer> {}

