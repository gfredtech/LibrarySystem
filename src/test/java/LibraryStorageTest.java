import org.controller.*;
import org.items.User;
import org.junit.jupiter.api.*;
import org.storage.ItemSerializer;
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
    }

    @BeforeEach
    void prepareData() {
        User admin = storage.find(Resource.User, new QueryParameters().add("type", "Admin")).get(0).getUser();
        manager.execute(new AddUserCommand(data.alice, admin)).validate();

        manager.execute(new AddItemCommand(data.b1, data.alice)).validate();
        storage.updateAll(Resource.Book, data.books.get("cormen"),
                new QueryParameters().add("copy_num", 1));
        data.b1 = storage.find(Resource.Book, data.books.get("cormen").subset("title")).get(0).getItem();
        manager.execute(new AddUserCommand(data.p1, data.alice)).validate();
        manager.execute(new AddUserCommand(data.p2, data.alice)).validate();
        manager.execute(new AddUserCommand(data.p3, data.alice)).validate();
    }

    @Test
    void checkFeeCalculation() {
        UserEntry user = storage.find(Resource.User, data.users.get("nadia")).get(0);
        ItemEntry item = storage.find(Resource.Book, data.books.get("cormen").subset("title")).get(0);
        Command c = new CheckOutCommand(user, item, LocalDate.now().minusWeeks(4));
        manager.execute(c).validate();
        CheckoutEntry co = storage.find(Resource.Checkout,
                new QueryParameters().add("user_id", user.getId())
                                     .add("item_id", item.getId())).get(0);
        assert storage.caluclateFee(co) == 0 : storage.caluclateFee(co);
    }

    @Test
    void testPendingQueue() {
        UserEntry student0 = storage.find(Resource.User, data.users.get("elvira")).get(0);
        UserEntry student1 = storage.find(Resource.User, data.users.get("nadia")).get(0);
        UserEntry professor = storage.find(Resource.User, data.users.get("sergey")).get(0);
        ItemEntry item = storage.find(Resource.Book, data.books.get("cormen").subset("title")).get(0);
        Command c = new CheckOutCommand(student0, item, LocalDate.now().minusWeeks(4));
        manager.execute(c).validate();
        c = new CheckOutCommand(professor, item, LocalDate.now().minusWeeks(4));
        manager.execute(c).validate();
        c = new CheckOutCommand(student1, item, LocalDate.now().minusWeeks(4));
        manager.execute(c).validate();
        for(UserEntry u: storage.getQueueFor(item)) {
            System.out.println(u.getUser().getName());
        }
        assert storage.getQueueFor(item).size() == 2;
    }

    @AfterEach
    void cleanUp() {
        UserEntry admin = storage.find(Resource.User, new QueryParameters().add("type", "Admin")).get(0);
        UserEntry librarian = storage.find(Resource.User, data.users.get("alice")).get(0);
        UserEntry student0 = storage.find(Resource.User, data.users.get("elvira")).get(0);
        UserEntry student1 = storage.find(Resource.User, data.users.get("nadia")).get(0);
        UserEntry professor = storage.find(Resource.User, data.users.get("sergey")).get(0);
        storage.removeAll(Resource.Book, ItemSerializer.serialize(data.b1));
        manager.execute(new RemoveUserCommand(librarian, student0)).validate();
        manager.execute(new RemoveUserCommand(librarian, student1)).validate();
        manager.execute(new RemoveUserCommand(librarian, professor)).validate();
        manager.execute(new RemoveUserCommand(admin, librarian)).validate();
    }

    private LibraryManager manager;
    private LibraryStorage storage;
    private TestItems data;
}
