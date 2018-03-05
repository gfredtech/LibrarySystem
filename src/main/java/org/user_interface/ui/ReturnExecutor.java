package org.user_interface.ui;

import org.resources.AvMaterial;
import org.resources.Book;
import org.resources.CheckoutRecord;
import org.resources.JournalIssue;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class ReturnExecutor implements Executor {

    ReturnExecutor(Bot bot) {
        this.bot = bot;
        state = "return";
        isDone = false;
    }

    public void processUpdate(Update update) {
        Message message = update.getMessage();

        Long chatId = message.getChatId();
        String text = message.getText();

        switch (state) {
            case "return":
                if (bot.currentUser.containsKey(chatId)) {
                    bot.sendMessage(chatId, "Select the type of document you want to return");
                    bot.sendMessage(chatId, "Types:\n" +
                            "Return Book\n" +
                            "Return Av Material\n" +
                            "Return Journal Issue\n");
                    state = "return_document_list";
                } else {
                    bot.sendMessage(chatId, "Please /login or /signup first before you can execute this command.");
                    isDone = true;
                }
                break;
            case "return_book_index_number":
                try {
                    int position = Integer.parseInt(text);
                    final Book returnSelected = SqlStorage.getInstance().getBook(position).get();
                    CheckoutRecord r =
                            SqlStorage.getInstance().getCheckoutRecordsFor(bot.currentUser.get(chatId).getCardNumber())
                                    .stream().filter(c -> c.item.getId() == returnSelected.getId()).findFirst().get();
                    SqlStorage.getInstance().removeCheckoutRecord(r);
                    bot.showMainMenuKeyboard(chatId, returnSelected.getTitle() + " returned successfully");
                    isDone = true;

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Not a number");
                    return;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    bot.showMainMenuKeyboard(chatId, "Internal bot error");
                    isDone = true;
                }
                break;

            case "return_av_material_index_number":
                try {
                    int position = Integer.parseInt(text);
                    final AvMaterial returnSelected = SqlStorage.getInstance().getAvMaterial(position).get();
                    CheckoutRecord r =
                            SqlStorage.getInstance().getCheckoutRecordsFor(bot.currentUser.get(chatId).getCardNumber())
                                    .stream().filter(c -> c.item.getId() == returnSelected.getId()).findFirst().get();
                    SqlStorage.getInstance().removeCheckoutRecord(r);
                    bot.showMainMenuKeyboard(chatId, returnSelected.getTitle() + " returned successfully");
                    isDone = true;

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Not a number");
                    return;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Invalid index. Please try again.");
                    isDone = true;
                }
                break;

            case "return_journal_index_number":
                try {
                    int position = Integer.parseInt(text);
                    final JournalIssue returnSelected = SqlStorage.getInstance().getJournal(position).get();
                    CheckoutRecord r =
                            SqlStorage.getInstance().getCheckoutRecordsFor(bot.currentUser.get(chatId).getCardNumber())
                                    .stream().filter(c -> c.item.getId() == returnSelected.getId()).findFirst().get();
                    SqlStorage.getInstance().removeCheckoutRecord(r);
                    bot.showMainMenuKeyboard(chatId, returnSelected.getTitle() + " returned successfully");
                    isDone = true;

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Not a number");
                    return;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    bot.sendMessage(chatId, "Invalid index. Please try again.");
                }
                break;

            default:
                switch (text) {
                    case "Return Book":
                        List<CheckoutRecord> checkedOut =
                                SqlStorage.getInstance().getCheckoutRecordsFor(bot.currentUser.get(chatId).getCardNumber());
                        System.out.println(bot.currentUser.get(chatId).getCardNumber());
                        StringBuilder books = new StringBuilder();
                        int i = 1;
                        for(CheckoutRecord c: checkedOut) {
                            if(c.item.getType().equals("book")) {
                                Book b = SqlStorage.getInstance().getBook(c.item.getId()).get();
                                books.append(b.getId());
                                books.append(". ");
                                books.append(b.getTitle());
                                books.append(" by ");
                                books.append(b.getAuthors());
                                books.append("\n");
                            }
                        }
                        if(books.length() > 0) {
                            bot.sendMessage(chatId,
                                    "This is the list of current books checked out by you:\n"+books.toString());
                        } else {
                            isDone = true;
                            bot.showMainMenuKeyboard(chatId, "You have no books checked out");
                        }

                        state = "return_book_index_number";

                        break;

                    case "Return Av Material":
                        List<CheckoutRecord> checkedOutMaterials =
                                SqlStorage.getInstance().getCheckoutRecordsFor(bot.currentUser.get(chatId).getCardNumber());
                        System.out.println(bot.currentUser.get(chatId).getCardNumber());
                        StringBuilder items = new StringBuilder();
                        int j = 1;
                        for(CheckoutRecord c: checkedOutMaterials) {
                            if(c.item.getType().equals("av_material")) {
                                AvMaterial avMaterial = SqlStorage.getInstance().getAvMaterial(c.item.getId()).get();
                                items.append(avMaterial.getId());
                                items.append(". ");
                                items.append(avMaterial.getTitle());
                                items.append("\n");
                            }
                        }
                        if(items.length() > 0) {
                            bot.sendMessage(chatId,
                                    "This is the list of current AV materials checked out by you:\n"+items.toString());
                        } else {
                            isDone = true;
                            bot.showMainMenuKeyboard(chatId, "You have no materials checked out");
                        }

                        state = "return_av_material_index_number";

                        break;

                    case "Return Journal Issue":
                        List<CheckoutRecord> checkedOutIssues =
                                SqlStorage.getInstance().getCheckoutRecordsFor(bot.currentUser.get(chatId).getCardNumber());
                        System.out.println(bot.currentUser.get(chatId).getCardNumber());
                        StringBuilder issues = new StringBuilder();
                        int k = 1;
                        for(CheckoutRecord c: checkedOutIssues) {
                            if(c.item.getType().equals("journal_issue")) {
                                JournalIssue journal = SqlStorage.getInstance().getJournal(c.item.getId()).get();
                                issues.append(journal.getId());
                                issues.append(". ");
                                issues.append(journal.getTitle());
                                issues.append("\n");
                            }
                        }
                        if(issues.length() > 0) {
                            bot.sendMessage(chatId,
                                    "This is the list of current journals checked out by you:\n"+issues.toString());
                        } else {
                            isDone = true;
                            bot.showMainMenuKeyboard(chatId, "You have no journals checked out");
                        }

                        state = "return_journal_index_number";

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
