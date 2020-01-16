package br.com.alesaudate.contas.config;


import br.com.alesaudate.contas.events.EventsProducerService;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import br.com.alesaudate.contas.interfaces.incoming.GenericReader;
import br.com.alesaudate.contas.interfaces.outcoming.OutputMechanism;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;

@Configuration
@ConditionalOnProperty(name = "contas.input.type", havingValue = "FILE")
@ConfigurationProperties(prefix = "contas.input.file")
@Data
public class FileInputConfiguration {


    private String folderIn;
    private String folderOut;
    private String folderErr;


    @Bean
    public StartSystem startSystemBean() {
        return new StartSystem(folderIn, folderOut, folderErr);
    }


    @Bean
    public OutputMechanism fileOutputMechanism() {
        return new FileOutputMechanism(folderIn, folderOut, folderErr);
    }


    @AllArgsConstructor
    public static class FileOutputMechanism implements OutputMechanism {

        String folderIn;
        String folderOut;
        String folderErr;

        @Override
        public void handleFile(String file) throws IOException {
            Path folderOut = Paths.get(this.folderOut);
            Path fileToMove = Paths.get(file);

            Path destination = folderOut.resolve(fileToMove.getFileName());
            Files.move(fileToMove, destination);
        }
    }

    public static class StartSystem implements ApplicationListener<ContextRefreshedEvent> {

        private String folderIn;
        private String folderOut;
        private String folderErr;

        StartSystem(String folderIn, String folderOut, String folderErr) {
            this.folderErr = folderErr;
            this.folderIn = folderIn;
            this.folderOut = folderOut;
        }

        @Autowired
        private GenericReader reader;

        @Autowired
        private ExecutorService executorService;

        @Autowired
        private EventsProducerService eventsProducerService;

        @Autowired
        private InteractionScheme io;

        @Override
        @SneakyThrows
        public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
            Path initialFolder = Paths.get(folderIn);

            WatchService service = FileSystems.getDefault().newWatchService();
            initialFolder.register(service, StandardWatchEventKinds.ENTRY_CREATE);

            executorService.submit(() -> {

                while (true) {
                    WatchKey key = service.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        try {
                            Path file = (Path) event.context();
                            Path fullPath = initialFolder.resolve(file);

                            loadFile(fullPath);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    key.reset();
                }
            });

            initialLoadOfFiles(initialFolder);

        }

        private void initialLoadOfFiles(Path initialFolder) throws IOException {

                Files
                    .list(initialFolder)
                    .filter(Files::isRegularFile)
                    .forEach(this::loadFile);
                    ;


        }


        private void loadFile(Path fullPath) {
                eventsProducerService.publishFileFound(fullPath.toString());
        }

    }

}
