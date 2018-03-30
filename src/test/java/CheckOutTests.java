import org.controller.*;
import org.items.Book;
import org.items.BookFactory;
import org.items.User;
import org.junit.jupiter.api.*;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.Storage;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckOutTests {

    @BeforeAll
    public void connect() throws ClassNotFoundException {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");
        manager = new LibraryManager(SqlStorage.getInstance());
        storage = LibraryStorage.getInstance();
    }

    @BeforeEach
    public void initStorage() {
        Book b = new BookFactory()
                .authors(Collections.emptyList())
                .publisher("MIT Press")
                .publicationDate(LocalDate.of(1990, 01, 01))
                .title("Cormen")
                .copiesNum(1)
                .keywords(Collections.emptyList())
                .build();
        manager.execute(new AddItemCommand(b)).validate();
        User u = new User(1001, "u1",
                "Student", null);
        u.setAddress("Somewhere");
        u.setPhoneNumber("12342");
        u.setLogin("uone");
        u.setPasswordHash(32);
        manager.execute(new AddUserCommand(u)).validate();
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
        storage.removeAll(Resource.Checkout, new QueryParameters().add("user_id", 1001));
        storage.removeAll(Resource.Book, new QueryParameters().add("title", "Cormen"));
        storage.removeAll(Resource.User, new QueryParameters().add("user_id", 1001));
    }

    LibraryManager manager;
    LibraryStorage storage;
}
