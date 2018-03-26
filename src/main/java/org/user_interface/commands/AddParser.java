package org.user_interface.commands;


import org.items.Book;
import org.items.BookFactory;
import org.items.Item;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddParser extends Command{

    Book parseBookParameters(Update update) {
        String message = update.getMessage().getText();
        String [] params = message.split("[;]+");

        for(int i = 0; i < params.length; i++) params[i] = params[i].trim();
        if(params.length != 9) return null;

        BookFactory bookFactory = new BookFactory();
        bookFactory.title(params[0]);
        bookFactory.copiesNum(Integer.valueOf(params[1]));
        if(params[2].equals("true")) bookFactory.isReference(); else bookFactory.isNotReference();
        bookFactory.keywords(parseGroups(params[3]));
        bookFactory.price(Integer.valueOf(params[4]));
        bookFactory.authors(parseGroups(params[5]));
        bookFactory.publicationDate(parseDate(params[6]));
        bookFactory.publisher(params[7]);
        if(params[8].equals("true")) bookFactory.isBestseller(); else bookFactory.isNotBestseller();
        return bookFactory.build();
    }

    Item parseAvMaterialParameters(AbsSender sender, Update update, Long chatId) {

        return null;
    }

    Item parseJournalIssueParameters(AbsSender sender, Update update, Long chatId) {

        return null;
    }

    List<String> parseGroups(String a) {
        a = a.replaceAll("[\\[\\]]+", "");
        String tokens[] = a.split("[,]+");

        List<String> result = new ArrayList<>();
        for(String i: tokens) {
           if(!i.equals(" ")) result.add(i.trim());
        }

        return result;
    }

    LocalDate parseDate(String a) {
        DateFormat df = new SimpleDateFormat("MMddyyyy", Locale.ENGLISH);
        Date result = null;
        try {
            result = df.parse(a);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    @Override
    public String run(AbsSender sender, Update update, String info) {
        return null;
    }
}
