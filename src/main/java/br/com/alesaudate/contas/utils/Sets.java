package br.com.alesaudate.contas.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Sets {


    public static <E> Set<E> newSet(E... elements) {
        return Arrays.stream(elements).collect(Collectors.toSet());
    }


}
