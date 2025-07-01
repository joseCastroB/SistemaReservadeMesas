package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del Sistema de Reserva de Mesas "7 Sopas"
 * 
 * Esta clase contiene el método main que inicia la aplicación Spring Boot.
 * 
 * @SpringBootApplication: Esta anotación combina tres anotaciones importantes:
 * - @Configuration: Permite definir beans de configuración
 * - @EnableAutoConfiguration: Habilita la configuración automática de Spring Boot
 * - @ComponentScan: Escanea automáticamente los componentes en este paquete y subpaquetes
 * 
 * Spring Boot configurará automáticamente:
 * - Servidor Tomcat embebido en puerto 8080
 * - Base de datos H2 en memoria
 * - Spring Security
 * - Spring Data JPA
 * - Thymeleaf como motor de templates
 */
@SpringBootApplication
public class DemoApplication {

	/**
	 * Método principal que inicia la aplicación Spring Boot
	 * 
	 * @param args Argumentos de línea de comandos (opcional)
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
