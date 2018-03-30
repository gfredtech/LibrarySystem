package org.user_interface.commands;

import org.items.*;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import java.util.ArrayList;
import java.util.List;

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

    Item parseAvMaterialParameters(Update update) {
        String message = update.getMessage().getText();
        String[] params = message.split("[;]+");

        for(int i = 0; i < params.length; i++) params[i] = params[i].trim();
        if (params.length != 6) return null;

        AvMaterialFactory factory = new AvMaterialFactory();
        factory.title(params[0]);
        factory.copiesNum(Integer.valueOf(params[1]));
        if(params[2].equals("true")) factory.isReference(); else factory.isNotReference();
        factory.keywords(parseGroups(params[3]));
        factory.price(Integer.valueOf(params[4]));
        factory.authors(parseGroups(params[5]));

        return factory.build();
    }

    Item parseJournalIssueParameters(Update update) {
        String message = update.getMessage().getText();
        String[] params = message.split("[;]+");

        for(int i = 0; i < params.length; i++) params[i] = params[i].trim();
        if (params.length != 8) return null;

        JournalIssueFactory factory = new JournalIssueFactory();
        factory.title(params[0]);
        factory.copiesNum(Integer.valueOf(params[1]));
        if(params[2].equals("true")) factory.isReference(); else factory.isNotReference();
        factory.keywords(parseGroups(params[3]));
        factory.price(Integer.valueOf(params[4]));
        factory.editors(parseGroups(params[5]));
        factory.publicationDate(parseDate(params[6]));
        factory.publisher(params[7]);

        return factory.build();
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

    @Override
    public String run(AbsSender sender, Update update, String info) {
        return null;
    }
}
