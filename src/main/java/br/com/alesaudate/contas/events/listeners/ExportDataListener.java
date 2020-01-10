package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.interfaces.InteractionScheme;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ExportDataListener extends InputCommandListener {


    InteractionScheme io;

    @Override
    protected boolean matchExpected(String message) {
        return isMessageClose(message, "Exportar dados");
    }

    @Override
    protected void process(String message) {

        //TODO completar a parte de exportação
        String initialDate = io.ask("Qual a data inicial (formato dd/MM/yyyy)?");


        getEventsProducerService().publishReadyForEvents();
    }

}
