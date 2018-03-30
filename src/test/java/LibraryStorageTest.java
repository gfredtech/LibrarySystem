import org.controller.CheckOutCommand;
import org.controller.Command;
import org.controller.LibraryManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LibraryStorageTest {

    @BeforeAll
    void initStorage() {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");
        storage = LibraryStorage.getInstance();
        manager = new LibraryManager(storage);
        data = new TestItems();
        cleanUp();
    }



    @Test
    void checkFeeCalculation() {
        storage.add(Resource.Book, data.books.get("cormen"));
        storage.add(Resource.User, data.users.get("nadia"));

        UserEntry user = storage.find(Resource.User, data.users.get("nadia")).get(0);
        ItemEntry item = storage.find(Resource.Book, data.books.get("cormen")).get(0);
        Command c = new CheckOutCommand(user, item, LocalDate.now().minusWeeks(4));
        manager.execute(c).validate();
        CheckoutEntry co = storage.find(Resource.Checkout,
                new QueryParameters().add("user_id", user.getId())
                                     .add("item_id", item.getId())).get(0);
        assert storage.caluclateFee(co) == 700 : storage.caluclateFee(co);

    }

    @AfterAll
    void cleanUp() {
        storage.removeAll(Resource.Checkout, data.users.get("nadia").subset("user_id"));
        storage.removeAll(Resource.User, data.users.get("nadia"));
        storage.removeAll(Resource.Book, data.books.get("cormen"));
    }

    LibraryManager manager;
    LibraryStorage storage;
    TestItems data;
}
