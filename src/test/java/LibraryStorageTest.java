import org.controller.CheckOutCommand;
import org.controller.Command;
import org.controller.LibraryManager;
import org.junit.jupiter.api.*;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LibraryStorageTest {

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
    }

    @Test
    void checkFeeCalculation() {
        UserEntry user = storage.find(Resource.User, data.users.get("nadia")).get(0);
        ItemEntry item = storage.find(Resource.Book, data.books.get("cormen")).get(0);
        Command c = new CheckOutCommand(user, item, LocalDate.now().minusWeeks(4));
        manager.execute(c).validate();
        CheckoutEntry co = storage.find(Resource.Checkout,
                new QueryParameters().add("user_id", user.getId())
                                     .add("item_id", item.getId())).get(0);
        assert storage.caluclateFee(co) == Math.min(700, co.getItem().getItem().getPrice()) : storage.caluclateFee(co);

    }

    @Test
    void testPendingQueue() {
        UserEntry student0 = storage.find(Resource.User, data.users.get("elvira")).get(0);
        UserEntry student1 = storage.find(Resource.User, data.users.get("nadia")).get(0);
        UserEntry professor = storage.find(Resource.User, data.users.get("sergey")).get(0);
        ItemEntry item = storage.find(Resource.Book, data.books.get("cormen")).get(0);
        Command c = new CheckOutCommand(student0, item, LocalDate.now().minusWeeks(4));
        manager.execute(c).validate();
        c = new CheckOutCommand(professor, item, LocalDate.now().minusWeeks(4));
        manager.execute(c).validate();
        c = new CheckOutCommand(student1, item, LocalDate.now().minusWeeks(4));
        manager.execute(c).validate();
        for(UserEntry u: storage.getQueueFor(item)) {
            System.out.println(u.getUser().getName());
        }
        assert storage.getQueueFor(item).get(0).getId() == student1.getId();
        assert storage.getQueueFor(item).get(1).getId() == professor.getId();
    }

    @AfterEach
    void cleanUp() {
        storage.removeAll(Resource.Checkout, data.users.get("nadia").subset("user_id"));
        storage.removeAll(Resource.Checkout, data.users.get("elvira").subset("user_id"));
        storage.removeAll(Resource.Checkout, data.users.get("sergey").subset("user_id"));
        storage.removeAll(Resource.PendingRequest, data.users.get("nadia").subset("user_id"));
        storage.removeAll(Resource.PendingRequest, data.users.get("elvira").subset("user_id"));
        storage.removeAll(Resource.PendingRequest, data.users.get("sergey").subset("user_id"));
        storage.removeAll(Resource.User, data.users.get("nadia"));
        storage.removeAll(Resource.User, data.users.get("elvira"));
        storage.removeAll(Resource.User, data.users.get("sergey"));
        storage.removeAll(Resource.Book, data.books.get("cormen").subset("title"));
    }

    private LibraryManager manager;
    private LibraryStorage storage;
    private TestItems data;
}
