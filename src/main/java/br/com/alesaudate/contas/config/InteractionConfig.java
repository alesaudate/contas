package br.com.alesaudate.contas.config;


import br.com.alesaudate.contas.interfaces.InteractionScheme;
import java.util.Scanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InteractionConfig {


    @Bean
    public InteractionScheme interactionScheme() {
        return new InteractionScheme(new Scanner(System.in), System.out);
    }

}
