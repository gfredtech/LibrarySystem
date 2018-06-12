import org.controller.*;
import org.junit.jupiter.api.*;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.BookEntry;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestQueueTests {
    @BeforeAll
    void initStorage() {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");
        storage = LibraryStorage.getInstance();
        manager = new LibraryManager(storage);
    }

    @BeforeEach
    void init() {
        storage.add(Resource.User, data.users.get("nadia"));
        storage.add(Resource.User, data.users.get("sergey"));
        data.books.get("cormen").add("copy_num", 1);
        storage.add(Resource.Book, data.books.get("cormen"));
    }

    @Test
    void test1() {
        BookEntry book =
                storage.find(Resource.Book, data.books.get("cormen")).get(0);
        UserEntry user1 =
                storage.find(Resource.User, data.users.get("nadia")).get(0);
        UserEntry user2 =
                storage.find(Resource.User, data.users.get("sergey")).get(0);
        CheckOutCommand command = new CheckOutCommand(user1, book);
        assert manager.execute(command) == Command.Result.Success;
        command = new CheckOutCommand(user2, book);
        assert manager.execute(command) == Command.Result.Warning;
        QueryParameters p = new QueryParameters()
                .add("user_id", user2.getId());
        assert !storage.find(Resource.PendingRequest, p).isEmpty();
    }

    @Test
    void test2() {
        BookEntry book =
                storage.find(Resource.Book, data.books.get("cormen")).get(0);
        UserEntry user =
                storage.find(Resource.User, data.users.get("nadia")).get(0);
        Command c = new CheckOutCommand(user, book);
        manager.execute(c).validate();

        CheckoutEntry checkout = storage.find(Resource.Checkout,
                new QueryParameters().add("user_id", user.getId())
                                     .add("item_id", book.getId())).get(0);
        c = new RenewCommand(checkout);
        manager.execute(c).validate();

        checkout = storage.find(Resource.Checkout,
                new QueryParameters().add("user_id", user.getId())
                        .add("item_id", book.getId())).get(0);
        c = new RenewCommand(checkout);
        assert manager.execute(c) == Command.Result.Failure;

        c = new ReturnCommand(user, book);
        manager.execute(c).validate();
        assert storage.find(Resource.Checkout,
                new QueryParameters().add("user_id", user.getId())
                        .add("item_id", book.getId())).isEmpty();
    }

    @AfterEach
    void cleanUp() {
        UserEntry user1 =
                storage.find(Resource.User, data.users.get("nadia")).get(0);
        BookEntry book =
                storage.find(Resource.Book, data.books.get("cormen")).get(0);
        storage.removeAll(Resource.Checkout,
                new QueryParameters().add("user_id", user1.getId()));
        storage.removeAll(Resource.PendingRequest,
                new QueryParameters().add("item_id", book.getId()));
        storage.removeAll(Resource.User, data.users.get("nadia"));
        storage.removeAll(Resource.User, data.users.get("sergey"));
        storage.removeAll(Resource.Book, data.books.get("cormen"));
    }

    private LibraryStorage storage;
    private LibraryManager manager;
    private TestItems data = new TestItems();
}
