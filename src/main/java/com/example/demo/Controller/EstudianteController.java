package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Services.ConsultaDataSource;
import com.example.demo.Services.EstudianteService;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {
    
    private final EstudianteService estudianteService;

    @Autowired
    private ConsultaDataSource consultaDataSource;

    public EstudianteController(EstudianteService estudianteService){
        this.estudianteService = estudianteService;
    }
    
    @GetMapping("/get/{id}")
    public String obtenerEstudiante(@PathVariable int id) {
        return estudianteService.obtenerEstudiante(id);
    }   

    @GetMapping("/list")
    public String consultaManual() {
        consultaDataSource.consultaManual();
        return "Consulta manual ejecutada";
    }
    
    

}
