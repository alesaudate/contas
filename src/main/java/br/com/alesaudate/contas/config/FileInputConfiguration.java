package br.com.alesaudate.contas.config;


import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.domain.DocumentService;
import br.com.alesaudate.contas.interfaces.incoming.GenericReader;
import br.com.alesaudate.contas.interfaces.intra.EventsProducerService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

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
        private DocumentService documentService;


        @Autowired
        private EventsProducerService eventsProducerService;

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
            try {
                byte[] data = FileUtils.readFileToByteArray(fullPath.toFile());
                if (reader.fileIsCorrect(data)) {
                    Document document = reader.loadDocument(data);
                    document.setName(fullPath.getFileName().toString());
                    documentService.saveDocument(document);
                }
                Files.move(fullPath, Paths.get(folderOut).resolve(fullPath.getFileName()));

            }
            catch (Exception e) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(baos);
                e.printStackTrace(printStream);

                try {

                    Path destinationPath = Paths.get(folderErr).resolve(fullPath.getFileName());
                    String fullFileName = destinationPath.toString();

                    Path errPath = Paths.get(fullFileName.substring(0, fullFileName.lastIndexOf(".")) + ".err");

                    Files.move(fullPath, destinationPath);
                    Files.write(errPath, baos.toByteArray());


                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }

    }

}
