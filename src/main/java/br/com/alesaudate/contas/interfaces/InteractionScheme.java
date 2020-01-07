package br.com.alesaudate.contas.interfaces;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class InteractionScheme {


    Scanner sc;
    PrintStream out;

    public String ask(String question, Object... args) {
        tell(question, args);
        return sc.nextLine();
    }

    public String ask(String question, Set<String> noResponses) {
        String response = ask(question);
        if (noResponses == null) {
            noResponses = new HashSet<>();
        }
        if (checkMatch(response, noResponses)) {
            return null;
        }
        return response;
    }


    public boolean askYes(String question, Set<String> acceptedYesAnwsers) {
        if (acceptedYesAnwsers == null) {
            acceptedYesAnwsers = new HashSet<>();
        }
        acceptedYesAnwsers.add("sim");
        String response = ask(question).trim();
        return checkMatch(response, acceptedYesAnwsers);
    }

    public void tell(String message) {
        out.println(message);
    }

    public void tell(String template, Object... args) {
        String s = String.format(template, args);
        tell(s);
    }

    public String getNextInput() {
        return sc.nextLine();
    }



    private boolean checkMatch (String string, Set<String> strings) {
        string = string.trim();
        return strings.stream().filter(string::equalsIgnoreCase).findAny().isPresent();
    }

}
