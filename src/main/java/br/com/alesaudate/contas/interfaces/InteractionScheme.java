package br.com.alesaudate.contas.interfaces;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

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
        try {
            return sc.nextLine();
        }
        catch (NoSuchElementException e) {
            sc = new Scanner(System.in);
            return sc.nextLine();
        }
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


    public boolean askBoolean(String question, Set<String> acceptedYesAnwsers) {
        return askBoolean(question, acceptedYesAnwsers, new Object(){});
    }

    public boolean askBoolean(String question, Set<String> acceptedYesAnwsers, Object... args) {
        if (acceptedYesAnwsers == null) {
            acceptedYesAnwsers = new HashSet<>();
        }
        acceptedYesAnwsers.add("sim");
        acceptedYesAnwsers.add("s");
        String response = ask(question, args).trim();
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

    public Set<String> asSet(String... strings) {
        return Arrays.stream(strings).collect(Collectors.toSet());
    }

    private boolean checkMatch (String string, Set<String> strings) {
        string = string.trim();
        return strings.stream().filter(string::equalsIgnoreCase).findAny().isPresent();
    }

}
