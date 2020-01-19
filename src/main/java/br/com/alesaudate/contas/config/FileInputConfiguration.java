package br.com.alesaudate.contas.config;


import br.com.alesaudate.contas.events.listeners.InvalidDocumentException;
import br.com.alesaudate.contas.events.listeners.NewFileDetectedMessageListener;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import br.com.alesaudate.contas.interfaces.outcoming.OutputMechanism;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

@Configuration
@ConditionalOnProperty(name = "contas.input.type", havingValue = "FILE")
@ConfigurationProperties(prefix = "contas.input.file")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileInputConfiguration {


    String folderIn;
    String folderOut;
    String folderErr;
    String folderExport;



    @Bean
    public StartSystem startSystemBean() {
        return new StartSystem(folderIn, folderErr);
    }


    @Bean
    public OutputMechanism fileOutputMechanism() {
        return new FileOutputMechanism(folderIn, folderOut, folderErr, folderExport);
    }


    @AllArgsConstructor
    public static class FileOutputMechanism implements OutputMechanism {

        String folderIn;
        String folderOut;
        String folderErr;
        String folderExport;

        @Override
        public void handleFile(String file) throws IOException {
            Path folderOut = Paths.get(this.folderOut);
            Path fileToMove = Paths.get(file);

            Path destination = folderOut.resolve(fileToMove.getFileName());
            Files.move(fileToMove, destination);
        }

        @Override
        public void writeData(byte[] data, String file) throws IOException {
            FileUtils.writeByteArrayToFile(Paths.get(folderExport).resolve(file).toFile(), data);
        }
    }

    public static class StartSystem{

        private String folderIn;
        private String folderErr;

        StartSystem(String folderIn, String folderErr) {
            this.folderIn = folderIn;
            this.folderErr = folderErr;
        }

        @Autowired
        private InteractionScheme io;

        @Autowired
        private NewFileDetectedMessageListener newFileDetectedMessageListener;


        @SneakyThrows
        public void start() {
            Path initialFolder = Paths.get(folderIn);
            initialLoadOfFiles(initialFolder);
        }

        private void initialLoadOfFiles(Path initialFolder) throws IOException {

                Files
                    .list(initialFolder)
                    .filter(Files::isRegularFile)
                    .forEach((file) -> {
                        try {
                            loadFile(file);
                        }
                        catch (InvalidDocumentException e) {
                            Path fileToMove = Paths.get(folderErr).resolve(file.getFileName());
                            try {
                                Files.move(file, fileToMove);
                            } catch (IOException ex) {
                                io.tell("Não conseguí mover o arquivo %s para diretório de erros.", fileToMove);
                            }
                        }
                        catch (Exception e) {
                            io.tell("Houve uma falha no processamento: ");
                            io.tell(String.format("%s : %s", e.getClass().getSimpleName(), e.getMessage()));
                        }
                    });
                    ;


        }


        private void loadFile(Path fullPath) throws IOException, ParseException, InvalidDocumentException {
            newFileDetectedMessageListener.accept(fullPath.toString());
        }

    }

}
