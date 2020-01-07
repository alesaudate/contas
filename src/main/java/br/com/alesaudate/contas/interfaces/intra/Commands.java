package br.com.alesaudate.contas.interfaces.intra;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class Commands {



    public static boolean match(String userSupplied, String match) {
        if (userSupplied == null) return false;

        userSupplied = userSupplied.trim().toLowerCase();
        match = match.trim().toLowerCase();

        int distance = LevenshteinDistance.getDefaultInstance().apply(userSupplied, match);
        return distance < match.length() / 10;


    }
}
