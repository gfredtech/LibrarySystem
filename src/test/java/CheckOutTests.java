import org.controller.*;
import org.items.Book;
import org.items.BookFactory;
import org.items.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.Storage;
import org.storage.resources.BookEntry;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CheckOutTests {

    @BeforeAll
    public void connect() throws ClassNotFoundException {
        SqlStorage.connect("library", "librarian", "tabula_rasa");
        manager = new LibraryManager(SqlStorage.getInstance());
        storage = SqlStorage.getInstance();
    }

    @BeforeEach
    public void initStorage() {
        Book b = new BookFactory()
                .title("Cormen")
                .copiesNum(1)
                .build();
        manager.execute(new AddItemCommand(b));
        User u = new User(1001, "u1",
                "Faculty", "Student");
        manager.execute(new AddUserCommand(u));
    }

    @Test
    public void studentChecksOutBook() {
        UserEntry user = storage.find(Resource.User,
                new QueryParameters().add("name", "u1")).get(0);
        ItemEntry item = storage.find(Resource.Book,
                new QueryParameters().add("title", "Cormen")).get(0);
        Command c = new CheckOutCommand(user, item);
        manager.execute(c);
        int n = storage.getNumOfEntries(Resource.Checkout,
                new QueryParameters().add("user_id", 1001));
        assertTrue(n == 1);
    }

    @AfterEach
    public void cleanUp() {
        storage.removeAll(Resource.Book, new QueryParameters().add("title", "Cormen"));
        storage.removeAll(Resource.User, new QueryParameters().add("user_id", 1001));
    }

    LibraryManager manager;
    Storage storage;
}
