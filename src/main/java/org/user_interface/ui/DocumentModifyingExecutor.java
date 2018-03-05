package org.user_interface.ui;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;



public class DocumentModifyingExecutor implements Executor{

    DocumentModifyingExecutor(Bot bot) {
        this.bot = bot;
        state = "modify";
        isDone = false;
    }

    public void processUpdate(Update update) {
        Message message = update.getMessage();

        Long chatId = message.getChatId();
        String text = message.getText();

        switch (state) {
            case "modify":
                bot.sendMessage(chatId, "Select the type of document you want to edit\n"+
                        "Types:\n"+
                        "Edit Book\n"+
                        "Edit Av Material\n"+
                        "Edit Journal Issue\n"+
                        "Edit Journal Article\n");
                state = "edit";
                break;
            default:
                switch (text) {
                    case "Edit Book":
                        if(state.equals("edit")) {
                            bot.sendMessage(chatId, "Select the book that you want to edit:");
                            bot.listBooks(chatId);
                            state = "edit_book";
                        }
                        break;

                    case "Edit AV Material":
                        if(state.equals("edit")) {
                            bot.sendMessage(chatId, "Select the AV Material you want to edit");
                            bot.listAvMaterials(chatId);
                            state = "edit_av_material";
                        }
                        break;

                    case "Edit Journal Article":
                        if(state.equals("edit")) {
                            bot.sendMessage(chatId, "Select the article you want to edit");
                            bot.listArticles(chatId);
                            state = "edit_article";
                        }
                        break;

                    case "Edit Journal Issue":
                        if(state.equals("edit")) {
                            bot.sendMessage(chatId, "Select the issue you want to edit");
                            bot.listJournalIssues(chatId);
                            state = "edit_journal_issue";
                        }
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
