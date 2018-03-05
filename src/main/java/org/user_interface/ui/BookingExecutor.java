package org.user_interface.ui;

import org.controller.BookingController;
import org.resources.AvMaterial;
import org.resources.Book;
import org.resources.JournalArticle;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import java.util.NoSuchElementException;

public class BookingExecutor implements Executor {

    BookingExecutor(Bot bot) {
        this.bot = bot;
        state = "select";
        isDone = false;
    }

    public void processUpdate(Update update) {
        Message message = update.getMessage();

        Long chatId = message.getChatId();
        String text = message.getText();

        switch (state) {
            case "select":
                if (bot.currentUser.containsKey(chatId)) {

                  bot.sendMessage(chatId,
                            "Select the kind of document you want to checkout:\n"+
                            "Book\n"+
                            "AV Material\n"+
                            "Journal Article\n");
                    state = "checkout";
                } else {
                    bot.sendMessage(chatId, "Please /login or /signup first before you can execute this command.");
                    isDone = true;
                }
                break;

            case "select_book":
                Book bookSelected = null;
                String msg = message.getText();
                int position;
                try {
                    position = Integer.parseInt(msg);
                    bookSelected = SqlStorage.getInstance().findBooks(new QueryParameters()).get(position - 1);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Input is not a number");
                    return;
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Input is an invalid index");
                    return;
                }

                if (bookSelected != null) {
                    state = "";
                    bot.showBookDetails(chatId, bookSelected);
                    bot.itemCursor.put(chatId, bookSelected);
                } else {
                    bot.sendMessage(chatId, "The book is not found somehow");
                    isDone = true;
                }
                break;

            case "select_av_material":
                AvMaterial materialSelected = null;
                int position1;
                try {
                    position1 = Integer.parseInt(text);
                    materialSelected  = SqlStorage.getInstance().findAvMaterials(new QueryParameters()).get(position1-1);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Input is not a number.");
                    return;
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Input is an invalid index");
                    return;
                }

                if(materialSelected  != null) {
                    bot.showAvMaterialDetails(chatId, materialSelected);
                    state = "";
                    bot.itemCursor.put(chatId, materialSelected );
                } else {
                    bot.sendMessage(chatId, "The material is not found somehow");
                    isDone = true;
                }

                break;

            case "select_article":
                JournalArticle articleSelected = null;
                int position2;
                try {
                    position2 = Integer.parseInt(text);
                    articleSelected = SqlStorage.getInstance().findArticles(new QueryParameters()).get(position2-1);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Input is not a number.");
                    return;
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Input is an invalid index");
                    return;
                }

                if(articleSelected != null) {
                    bot.showArticleDetails(chatId, articleSelected);
                    state = "";
                    bot.itemCursor.put(chatId, articleSelected);
                } else {
                    bot.sendMessage(chatId, "The article is not found somehow");
                    isDone = true;
                }
                break;

            default:
                switch (text) {
                    case "Book":
                        bot.sendMessage(chatId, "Here's a " +
                                "list of all Books in the library. Enter the number of the book you want:");
                        bot.listBooks(chatId);
                        state = "select_book";
                        break;

                    case "AV Material":
                        bot.sendMessage(chatId, "Here's a list of all AV Materials in the library. Enter" +
                                "the number of the AV material you want");
                        bot.listAvMaterials(chatId);
                        state = "select_av_material";
                        break;

                    case "Journal Article":
                        bot.sendMessage(chatId, "Here's a list of all Articles in the library. Enter" +
                                "the number of the article you want");
                        bot.listArticles(chatId);
                        state = "select_article";
                        break;

                    case "Checkout book":
                        try {
                            new BookingController(SqlStorage.getInstance())
                                    .checkOut(bot.currentUser.get(chatId).getCardNumber(),
                                            "book", bot.itemCursor.get(chatId).getId());
                            bot.showMainMenuKeyboard(chatId,
                                    bot.itemCursor.get(chatId).getTitle() + " is checked out successfully.");
                        } catch (BookingController.CheckoutException e) {
                            bot.showMainMenuKeyboard(chatId,
                                    "Sorry, but you cannot check out the item now.");
                        }
                        isDone = true;
                        break;

                    case "Checkout AV material":
                        try {
                            new BookingController(SqlStorage.getInstance())
                                    .checkOut(bot.currentUser.get(chatId).getCardNumber(),
                                            "av_material", bot.itemCursor.get(chatId).getId());
                            bot.showMainMenuKeyboard(chatId,
                                    bot.itemCursor.get(chatId).getTitle() + " is checked out successfully.");
                        } catch (BookingController.CheckoutException e) {
                            bot.showMainMenuKeyboard(chatId,
                                    "Sorry, but you cannot check out the item now.");
                        }
                        isDone = true;
                        break;

                    case "Checkout article":
                        try {
                            new BookingController(SqlStorage.getInstance())
                                    .checkOut(bot.currentUser.get(chatId).getCardNumber(),
                                            "article", bot.itemCursor.get(chatId).getId());
                            bot.showMainMenuKeyboard(chatId,
                                    bot.itemCursor.get(chatId).getTitle() + " is checked out successfully.");
                        } catch (BookingController.CheckoutException e) {
                            bot.showMainMenuKeyboard(chatId,
                                    "Sorry, but you cannot check out the item now.");
                        }
                        isDone = true;
                        break;

                    case "Cancel":
                        bot.showMainMenuKeyboard(chatId, "Cancelled");
                        isDone = true;
                        break;
                }
        }

    }

    public boolean isDone() {
        return isDone;
    }

    Bot bot;
    boolean isDone;
    String state;
}
