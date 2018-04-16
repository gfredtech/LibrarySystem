import org.controller.*;
import org.junit.jupiter.api.*;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CheckOutTests {

    @BeforeAll
    void connect() {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");
        manager = new LibraryManager(LibraryStorage.getInstance());
        storage = LibraryStorage.getInstance();
    }

    @BeforeEach
    void initStorage() {
        UserEntry admin = storage.find(Resource.User, new QueryParameters().add("type", "Admin")).get(0);

        manager.execute(new AddUserCommand(items.alice, admin.getUser())).validate();
        manager.execute(new AddItemCommand(items.b1, items.alice)).validate();
        manager.execute(new AddUserCommand(items.s, items.alice)).validate();
    }

    @Test
    void studentChecksOutBook() {
        UserEntry user = storage.find(Resource.User, items.users.get("andrey")).get(0);
        ItemEntry item = storage.find(Resource.Book, items.books.get("cormen")).get(0);
        Command c = new CheckOutCommand(user, item);
        manager.execute(c);
        int n = storage.getNumOfEntries(Resource.Checkout,
                new QueryParameters().add("user_id", items.s.getCardNumber()));
        assertTrue(n == 1);
    }

    @AfterEach
    void cleanUp() {
        storage.removeAll(Resource.Checkout, new QueryParameters().add("user_id", items.s.getCardNumber()));
        storage.removeAll(Resource.Book, items.books.get("cormen"));
        storage.removeAll(Resource.User, new QueryParameters().add("user_id", items.s.getCardNumber()));
    }

    private LibraryManager manager;
    private LibraryStorage storage;
    private TestItems items = new TestItems();
}
