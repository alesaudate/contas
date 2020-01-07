package br.com.alesaudate.contas.interfaces.intra.events;


import br.com.alesaudate.contas.domain.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class WantsToListEntriesEvent {

    String answer;
    Document document;

}
