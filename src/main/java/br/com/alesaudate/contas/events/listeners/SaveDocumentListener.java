package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.domain.DocumentService;
import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.events.definitions.DocumentFileEvent;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import br.com.alesaudate.contas.interfaces.outcoming.Layout;
import br.com.alesaudate.contas.interfaces.outcoming.OutputMechanism;
import br.com.alesaudate.contas.utils.Dates;
import br.com.alesaudate.contas.utils.Sets;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SaveDocumentListener extends GenericMessageListener<DocumentFileEvent> {

    DocumentService documentService;

    InteractionScheme io;

    OutputMechanism outputMechanism;

    Layout layout;



    @Override
    public void accept(DocumentFileEvent event) throws IOException {
        documentService.saveDocumentData(event.getDocument());
        outputMechanism.handleFile(event.getFile());
        io.tell("Dados salvos no banco de dados.");

    }


}
