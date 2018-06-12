import org.controller.CheckOutCommand;
import org.controller.Command;
import org.controller.LibraryManager;
import org.controller.OutstandingRequestCommand;
import org.items.Item;
import org.junit.jupiter.api.*;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;
import java.util.Arrays;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OutstandingRequestTest {

    @BeforeAll
    void initStorage() {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");
        storage = LibraryStorage.getInstance();
        manager = new LibraryManager(storage);
        data = new TestItems();
        cleanUp();
    }

    @BeforeEach
    void prepareData() {
        storage.add(Resource.Book, data.books.get("cormen").add("copy_num", 1));
        storage.add(Resource.User, data.users.get("nadia"));
        storage.add(Resource.User, data.users.get("sergey"));
        storage.add(Resource.User, data.users.get("elvira"));
        storage.add(Resource.User, data.users.get("alice"));
    }

    @Test
    void placeRequest() {
        UserEntry l = storage.find(Resource.User, data.users.get("alice")).get(0);
        UserEntry u1 = storage.find(Resource.User, data.users.get("nadia")).get(0);
        UserEntry u2 = storage.find(Resource.User, data.users.get("sergey")).get(0);
        UserEntry u3 = storage.find(Resource.User, data.users.get("elvira")).get(0);
        ItemEntry i = storage.find(Resource.Book, data.books.get("cormen")).get(0);

        Command c = new CheckOutCommand(u1, i);
        manager.execute(c).validate();
        c = new CheckOutCommand(u2, i);
        manager.execute(c).validate();
        c = new CheckOutCommand(u3, i);
        manager.execute(c).validate();
        assert storage.getNumOfEntries(Resource.PendingRequest,
                new QueryParameters().add("item_id", i.getId())) == 2;
        c = new OutstandingRequestCommand(l, i);
        manager.execute(c).validate();
        assert storage.getNumOfEntries(Resource.PendingRequest,
                new QueryParameters().add("item_id", i.getId())) == 1;
        c = new CheckOutCommand(u3, i);
        assert manager.execute(c) == Command.Result.Failure;

    }

    @AfterEach
    void cleanUp() {
        for(String user: Arrays.asList("elvira", "sergey", "nadia", "alice")) {
            storage.removeAll(Resource.Checkout, data.users.get(user).subset("user_id"));
            storage.removeAll(Resource.PendingRequest, data.users.get(user).subset("user_id"));
            storage.removeAll(Resource.User, data.users.get(user));
        }
        storage.removeAll(Resource.Book, data.books.get("cormen").subset("title"));
    }

    LibraryManager manager;
    LibraryStorage storage;
    TestItems data;
}
