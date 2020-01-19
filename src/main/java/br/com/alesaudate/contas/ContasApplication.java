package br.com.alesaudate.contas;

import br.com.alesaudate.contas.config.FileInputConfiguration;
import br.com.alesaudate.contas.domain.SystemManagement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class ContasApplication {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext ctx =  SpringApplication.run(ContasApplication.class, args);
        FileInputConfiguration.StartSystem startSystem = ctx.getBean(FileInputConfiguration.StartSystem.class);
        SystemManagement systemManagement = ctx.getBean(SystemManagement.class);
        startSystem.start();
        systemManagement.start();
    }
}
