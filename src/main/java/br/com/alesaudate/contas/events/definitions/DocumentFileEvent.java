package br.com.alesaudate.contas.events.definitions;


import br.com.alesaudate.contas.domain.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentFileEvent {

    Document document;
    String file;

}
