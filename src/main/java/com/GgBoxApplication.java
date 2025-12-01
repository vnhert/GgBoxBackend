package com;
import com.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class GgBoxApplication {

    /**
     * 
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(GgBoxApplication.class, args);
    }
    @Bean
    public CommandLineRunner initData(UsuarioService userService) {
        return args -> {
            userService.createAdminUserIfNotExists();
        };
    }

}  