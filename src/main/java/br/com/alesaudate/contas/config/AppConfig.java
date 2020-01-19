package br.com.alesaudate.contas.config;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.alesaudate.contas.interfaces.outcoming.ExcelLayout;
import br.com.alesaudate.contas.interfaces.outcoming.Layout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {




    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(2);
    }

    @Bean
    public SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }

    @Bean
    public Layout exportLayout() {
        return new ExcelLayout();
    }
}
