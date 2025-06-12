package com.example.demo.security;

import com.example.demo.Entities.ConfiguracionFranja;
import com.example.demo.Entities.TipoMesa;
import com.example.demo.Entities.Usuario;
import com.example.demo.Repository.ConfiguracionFranjaRepository;
import com.example.demo.Repository.TipoMesaRepository;
import com.example.demo.Repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TipoMesaRepository tipoMesaRepository;
    private final ConfiguracionFranjaRepository franjaRepository;

    /**
     * El constructor ahora incluye todos los repositorios necesarios.
     * Spring se encargará de inyectarlos automáticamente.
     */
    public DataInitializer(UsuarioRepository usuarioRepository, 
                           PasswordEncoder passwordEncoder, 
                           TipoMesaRepository tipoMesaRepository, 
                           ConfiguracionFranjaRepository franjaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tipoMesaRepository = tipoMesaRepository;
        this.franjaRepository = franjaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // --- 1. Crear usuario admin si no existe ---
        // Revisa si el usuario 'admin' ya existe en la base de datos
        if (usuarioRepository.findByUsuario("admin").isPresent()) {
            log.info("El usuario administrador ya existe. No se realizarán acciones.");
        } else {
            // Si no existe, crea y guarda el nuevo usuario administrador
            log.info("Creando usuario administrador por primera vez...");
            Usuario adminUser = new Usuario();
            adminUser.setNombreCompleto("Administrador del Sistema");
            adminUser.setCorreo("admin@sietesopas.com");
            adminUser.setTelefono("987654321");
            adminUser.setUsuario("admin");
            // Codifica la contraseña usando el PasswordEncoder de la aplicación
            adminUser.setContrasena(passwordEncoder.encode("adminpass"));
            adminUser.setRol("ADMIN");
            
            usuarioRepository.save(adminUser);
            log.info("Usuario administrador creado exitosamente.");
        }

        // --- 2. Crear Tipos de Mesa si no existen ---
        if (tipoMesaRepository.count() == 0) {
            log.info("Creando tipos de mesa...");
            tipoMesaRepository.saveAll(List.of(
                createTipoMesa("Común", "Mesas estándar en el salón principal."),
                createTipoMesa("Terraza", "Mesas al aire libre."),
                createTipoMesa("Privada", "Mesas en un área reservada para mayor privacidad."),
                createTipoMesa("Ventana", "Mesas con vista a la calle.")
            ));
            log.info("Tipos de mesa creados.");
        }

        // --- 3. Crear Franjas Horarias si no existen ---
        if (franjaRepository.count() == 0) {
            log.info("Creando franjas horarias...");
            franjaRepository.saveAll(List.of(
                createFranja("10:00 - 12:00", 100, 20),
                createFranja("12:00 - 14:00", 100, 20),
                createFranja("14:00 - 16:00", 100, 20),
                createFranja("16:00 - 18:00", 100, 20),
                createFranja("18:00 - 20:00", 100, 20),
                createFranja("20:00 - 22:00", 100, 20),
                createFranja("22:00 - 00:00", 100, 20)
            ));
            log.info("Franjas horarias creadas.");
        }

    }

    private TipoMesa createTipoMesa(String nombre, String desc) {
        TipoMesa tipo = new TipoMesa();
        tipo.setNombre(nombre);
        tipo.setDescripcion(desc);
        return tipo;
    }
    
    private ConfiguracionFranja createFranja(String horario, int personas, int mesas) {
        ConfiguracionFranja franja = new ConfiguracionFranja();
        franja.setFranjaHoraria(horario);
        franja.setCapacidadMaxima(personas);
        franja.setCantidadMesas(mesas);
        return franja;
    }

}