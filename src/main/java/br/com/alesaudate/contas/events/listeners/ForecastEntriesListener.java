package br.com.alesaudate.contas.events.listeners;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ForecastEntriesListener extends InputCommandListener{


    @Override
    protected boolean matchExpected(String message) {
        return isMessageClose(message, "Localizar gastos futuros")
                || isMessageClose(message, "Localizar lançamentos futuros");
    }

    @Override
    protected void process(String message) {

    }

    //TODO criar sistema para predizer gastos
}
