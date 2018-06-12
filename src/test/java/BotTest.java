import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.user_interface.commands.LoginCommand;
import org.user_interface.commands.SignUpCommand;
import org.user_interface.ui.Bot;

class BotTest {

    @Test
    void test() {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");

        Bot bot = Mockito.mock(Bot.class);
        try {
            Mockito.when(bot.execute(Mockito.any(SendMessage.class))).thenReturn(new Message());

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        LoginCommand login = new LoginCommand();
        Message m = Mockito.mock(Message.class);
        Mockito.when(m.getText()).thenReturn("qwert qwert");
        Chat chat = Mockito.mock(Chat.class);
        Mockito.when(m.getChat()).thenReturn(chat);
        Update u = Mockito.mock(Update.class);
        Mockito.when(u.getMessage()).thenReturn(m);
        Mockito.when(u.hasMessage()).thenReturn(true);
        login.run("password");

        SignUpCommand command = new SignUpCommand();
        m = Mockito.mock(Message.class);
        Mockito.when(m.getText()).thenReturn("qwerty, somewhere, +42, qwerty, ytrewq, Student, null");
        Mockito.when(m.getChat()).thenReturn(chat);
        u = Mockito.mock(Update.class);
        Mockito.when(u.getMessage()).thenReturn(m);
        Mockito.when(u.hasMessage()).thenReturn(true);
        command.run(bot, u, "validator");

        CallbackQuery c = Mockito.mock(CallbackQuery.class);
        Mockito.when(c.getData()).thenReturn("Confirm");
        Mockito.when(u.getCallbackQuery()).thenReturn(c);
        command.run(bot, u, "confirm");

        assert !LibraryStorage.getInstance().find(Resource.User, new QueryParameters().add("login","qwerty")).isEmpty();
        LibraryStorage.getInstance().removeAll(Resource.User, new QueryParameters().add("login","qwerty"));

    }
}
