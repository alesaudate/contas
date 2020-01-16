package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.domain.DocumentService;
import br.com.alesaudate.contas.events.definitions.DocumentFileEvent;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import br.com.alesaudate.contas.interfaces.outcoming.OutputMechanism;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static br.com.alesaudate.contas.config.EventBusConfig.DOCUMENT_READY;


@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SaveDocumentListener extends GenericMessageListener<DocumentFileEvent> {

    DocumentService documentService;

    InteractionScheme io;

    OutputMechanism outputMechanism;


    @Override
    protected void doAccept(DocumentFileEvent event) throws Exception {
        documentService.saveDocumentData(event.getDocument());
        outputMechanism.handleFile(event.getFile());
        io.tell("Dados salvos no banco de dados. Para revisar, utilize o comando 'listar entradas' ou 'exportar dados'");
        getEventsProducerService().publishReadyForEvents();
    }

    @Override
    public String listenToEvent() {
        return DOCUMENT_READY;
    }
}
