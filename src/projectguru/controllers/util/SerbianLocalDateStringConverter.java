/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.util.StringConverter;

/**
 *
 * @author marko
 */
public class SerbianLocalDateStringConverter extends StringConverter<LocalDate> {
    private final DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    @Override
    public String toString(LocalDate localDate)
    {
        if(localDate==null)
            return "";
        return dateTimeFormatter.format(localDate);
    }

    @Override
    public LocalDate fromString(String dateString)
    {
        if(dateString==null || dateString.trim().isEmpty())
        {
            return null;
        }
        return LocalDate.parse(dateString,dateTimeFormatter);
    }
}
