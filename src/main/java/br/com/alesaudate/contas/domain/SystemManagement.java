package br.com.alesaudate.contas.domain;

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
import java.util.List;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SystemManagement {



    Layout layout;
    DocumentService documentService;
    OutputMechanism outputMechanism;
    InteractionScheme io;

    public void start() throws IOException {
        boolean emitReport = io.askBoolean("Quer emitir relatório do período?", Sets.newSet("Sim", "Quero"));
        if (emitReport) {
            LocalDate initialDate = ask("Certo, qual a data inicial?");
            LocalDate finalDate = ask("E a data final?");

            List<Entry> entries = documentService.listEntriesByPeriod(initialDate, finalDate);
            byte[] data = layout.format(entries);
            outputMechanism.writeData(data, String.format("Contas_%d_%d_%d_a_%d_%d_%d.%s", initialDate.getDayOfMonth(), initialDate.getMonthValue(), initialDate.getYear(), finalDate.getDayOfMonth(), finalDate.getMonthValue(), finalDate.getYear(), layout.getExtension()));
            io.tell("Documento salvo com sucesso.");
        }
    }



    private LocalDate ask(String message) {
        while(true) {
            String dateAsString = io.ask(message);
            try {
                if (StringUtils.isNotBlank(dateAsString)) {
                    return parseUserProvidedDate(dateAsString.trim());
                }
            } catch (ParseException e) {
                io.tell("Desculpe, não entendí. ");
            }
        }
    }


    private LocalDate parseUserProvidedDate(String initialDate) throws ParseException {

        return Dates.parseUserInputDate(initialDate);

    }
}
