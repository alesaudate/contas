package br.com.alesaudate.contas.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class Dates {


    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    public LocalDate localDate(Date date) {
        return date.toInstant().atZone(DEFAULT_ZONE).toLocalDate();
    }

    public Date date(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE).toInstant());
    }


    public LocalDate parseUserInputDate(String string) throws ParseException {

        //Formatos:
        //dd/MM/yyyy
        //dd/MM/yy
        //dd de MMM de yy
        //dd de MMM de yyyy
        //dd de MMM
        //dd/MM



        if (StringUtils.isBlank(string)) {
            throw new ParseException("User input is blank!",0);
        }

        Date date = DateUtils.parseDate(string, new Locale("pt", "BR"),
                "dd/MM/yyyy",
                "dd/MM/yy",
                "dd 'de' MMM 'de' yy",
                "dd 'de' MMM 'de' yyyy",
                "dd 'de' MMM",
                "dd/MM");

        LocalDate localDate = localDate(date);

        if (localDate.getYear() < 100) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            localDate = localDate.withYear(localDate.getYear() + 2000);
            localDate = localDate.withDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        }
        else if (localDate.getYear() < 1980) {
            localDate = localDate.withYear(LocalDate.now().getYear());
            if (localDate.isAfter(LocalDate.now())) {
                localDate = localDate.minusYears(1);
            }
        }
        return localDate;
    }

    public static void main(String[] args) throws ParseException {
        /*System.out.println(Dates.parseUserInputDate("18/1/2020"));
        System.out.println(Dates.parseUserInputDate("18/01/2020"));
        System.out.println(Dates.parseUserInputDate("18/1/20"));
        System.out.println(Dates.parseUserInputDate("18 de janeiro de 2020"));
        System.out.println(Dates.parseUserInputDate("18 de janeiro de 20"));
        System.out.println(Dates.parseUserInputDate("18 de janeiro"));
        System.out.println(Dates.parseUserInputDate("18/01"));
        System.out.println(Dates.parseUserInputDate("18/1"));


        System.out.println(Dates.parseUserInputDate("18/3"));*/
        System.out.println(Dates.parseUserInputDate("8 de dezembro"));

    }
}
