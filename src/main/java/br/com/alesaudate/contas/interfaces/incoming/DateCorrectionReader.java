package br.com.alesaudate.contas.interfaces.incoming;


import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.utils.Dates;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.text.ParseException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DateCorrectionReader implements DataReader {


    DataReader underlying;

    @Override
    public boolean fileIsCorrect(byte[] data) {
        return underlying.fileIsCorrect(data);
    }

    @Override
    public Document loadDocument(byte[] data) throws IOException, ParseException {
        Document loaded = underlying.loadDocument(data);
        List<Entry> entries = loaded.getEntries().stream().sorted(Comparator.comparing(Entry::getDate)).collect(Collectors.toList());

        LocalDate compareDate = LocalDate.now();
        if (someItemsAheadOfGivenDate(entries, compareDate)) {

            int correctYear = getCorrectYear(entries);
            entries.stream().forEach(entry -> correctDate(entry, correctYear, compareDate));
        }
        return loaded;
    }

    private int getCorrectYear(List<Entry> entries) {

        List<Integer> years = entries.stream().map(entry -> Dates.localDate(entry.getDate()).getYear()).distinct().sorted(Integer::compareTo).collect(Collectors.toList());
        return years.size() == 1 ? years.get(0) - 1 : years.get(0);

    }

    private boolean someItemsAheadOfGivenDate(List<Entry> entries, LocalDate compareDate) {
        int currentMonth = compareDate.getMonthValue();
        return entries.stream().filter(entry -> Dates.localDate(entry.getDate()).getMonthValue() > currentMonth).findAny().isPresent();
    }




    private void correctDate(Entry entry, int correctYear, LocalDate compareDate) {

        int nowMonth = compareDate.getMonthValue();
        LocalDate entryDate = Dates.localDate(entry.getDate());

        if (entryDate.getMonthValue() > nowMonth) {
            entryDate = entryDate.withYear(correctYear);
            entry.setDate(Dates.date(entryDate));
        }

    }
}
