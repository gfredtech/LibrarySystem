import org.controller.*;
import org.items.Book;
import org.items.BookFactory;
import org.junit.jupiter.api.*;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CheckOutTests {

    @BeforeAll
    void connect() {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");
        manager = new LibraryManager(LibraryStorage.getInstance());
        storage = LibraryStorage.getInstance();
        cleanUp();
    }

    @BeforeEach
    void initStorage() {
        UserEntry admin = storage.find(Resource.User, new QueryParameters().add("type", "Admin")).get(0);

        manager.execute(new AddUserCommand(items.alice, admin.getUser())).validate();
        manager.execute(new AddItemCommand(items.b1, items.alice)).validate();
        Book b = new BookFactory()
                .title("Hackers and Painters")
                .copiesNum(5)
                .authors(Arrays.asList("Paul","Graham"))
                .isNotBestseller()
                .publicationDate(LocalDate.of(2005, 9, 9))
                .publisher("Harper Collins")
                .keywords(Arrays.asList("hacker","lisp","painter","startup"))
                .price(2500).build();
        manager.execute(new AddItemCommand(b, items.alice));

        manager.execute(new AddUserCommand(items.s, items.alice)).validate();
    }

    @Test
    void studentChecksOutBook() {
        UserEntry user = storage.find(Resource.User, items.users.get("andrey")).get(0);
        ItemEntry item = storage.find(Resource.Book, items.books.get("cormen")).get(0);
        Command c = new CheckOutCommand(user, item);
        manager.execute(c).validate();
        ItemEntry item2 = storage.find(Resource.Book, new QueryParameters().add("title", "Hackers and Painters")).get(0);
        c = new CheckOutCommand(user, item2);
        manager.execute(c).validate();
        int n = storage.getNumOfEntries(Resource.Checkout,
                new QueryParameters().add("user_id", items.s.getCardNumber()));
        assertTrue(n == 2);
    }

    @AfterEach
    void cleanUp() {
        UserEntry admin = storage.find(Resource.User, new QueryParameters().add("type", "Admin")).get(0);
        try {
            UserEntry u1 = storage.find(Resource.User, items.users.get("andrey")).get(0);
            manager.execute(new RemoveUserCommand(admin, u1));
            u1 = storage.find(Resource.User, items.users.get("alice")).get(0);
            manager.execute(new RemoveUserCommand(admin, u1));
            storage.removeAll(Resource.Book, items.books.get("cormen"));
        } catch (IndexOutOfBoundsException e) { ; }
        storage.removeAll(Resource.Book, new QueryParameters().add("title", "Hackers and Painters"));
    }

    private LibraryManager manager;
    private LibraryStorage storage;
    private TestItems items = new TestItems();
}
