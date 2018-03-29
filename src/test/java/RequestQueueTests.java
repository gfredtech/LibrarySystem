import org.controller.CheckOutCommand;
import org.controller.Command;
import org.controller.LibraryManager;
import org.junit.jupiter.api.*;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.Storage;
import org.storage.resources.BookEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RequestQueueTests {
    @BeforeAll
    void initStorage() throws ClassNotFoundException {
        SqlStorage.connect("library", "librarian", "tabula_rasa");
        storage = SqlStorage.getInstance();
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

    @AfterEach
    void cleanUp() {
        System.out.println("Cleanup");
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

    private Storage storage;
    private LibraryManager manager;
    private TestItems data = new TestItems();
}
