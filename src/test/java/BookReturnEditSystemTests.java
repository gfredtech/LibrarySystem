import org.controller.CheckOutCommand;
import org.controller.Command;
import org.controller.LibraryManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.BookEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookReturnEditSystemTests {

    @BeforeAll
    void init() {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");
        storage = LibraryStorage.getInstance();
        manager = new LibraryManager(storage);
        cleanUp();
    }

    private void initStorage() {
        storage.add(Resource.Book, data.books.get("cormen"));
        storage.add(Resource.Book, data.books.get("patterns"));
        storage.add(Resource.Book, data.books.get("brooks"));
        storage.add(Resource.AvMaterial, data.av.get("null"));
        storage.add(Resource.AvMaterial, data.av.get("entropy"));
        storage.add(Resource.User, data.users.get("sergey"));
        storage.add(Resource.User, data.users.get("elvira"));
        storage.add(Resource.User, data.users.get("nadia"));
    }

    private void modifyStorage() {
        storage.removeAll(Resource.User, data.users.get("nadia"));

        storage.updateAll(Resource.Book, data.books.get("cormen"),
                new QueryParameters().add("copy_num", 1));

        storage.removeAll(Resource.Book, data.books.get("brooks").subset("title"));
        storage.removeAll(Resource.User, data.users.get("nadia"));
    }

    @Test
    void test1() {
        initStorage();
        cleanUp();
    }

    @Test
    void test2() {
        initStorage();
        modifyStorage();
        cleanUp();
    }

    @Test
    void test3() {
        initStorage();
        modifyStorage();
        assert storage.get(Resource.User, 1010).isPresent();
        assert storage.get(Resource.User, 1100).isPresent();
        cleanUp();
    }

    @Test
    void test4() {
        initStorage();
        modifyStorage();
        assert !storage.get(Resource.User, 1011).isPresent();
        assert storage.get(Resource.User, 1100).isPresent();
        cleanUp();
    }

    @Test
    void test5() {
        initStorage();
        modifyStorage();

        try {
            BookEntry b =
                    storage.find(Resource.Book, data.books.get("cormen").subset("title")).get(0);
            UserEntry u = storage.get(Resource.User, 1011).get();
            assert manager.execute(new CheckOutCommand(u, b)).successful();
            assert false;

        } catch (NoSuchElementException e) {
            // correct
        }

        cleanUp();
    }

    @Test
    void test6() {
        initStorage();
        modifyStorage();
        UserEntry user = storage.get(Resource.User,1010).get();
        Command checkout = new CheckOutCommand(
                user,
                storage.find(Resource.Book, data.books.get("cormen").subset("title")).get(0));
        manager.execute(checkout).validate();
        user = storage.get(Resource.User, 1100).get();
        Command failingCheckout = new CheckOutCommand(
                user,
                storage.find(Resource.Book, data.books.get("cormen").subset("title")).get(0));

        assert manager.execute(failingCheckout) == Command.Result.Warning;
        user = storage.get(Resource.User, 1010).get();
        checkout = new CheckOutCommand(
                user,
                storage.find(Resource.Book, data.books.get("patterns").subset("title")).get(0));
        manager.execute(checkout).validate();
        cleanUp();
    }


    @Test
    void test7() {
        initStorage();
        UserEntry user = storage.get(Resource.User, 1010).get();
        Command checkout = new CheckOutCommand(
                user,
                storage.find(Resource.Book, data.books.get("cormen").subset("title")).get(0));
        manager.execute(checkout).validate();
        checkout = new CheckOutCommand(
                user,
                storage.find(Resource.Book, data.books.get("patterns").subset("title")).get(0));
        manager.execute(checkout).validate();
        checkout = new CheckOutCommand(
                user,
                storage.find(Resource.Book, data.books.get("brooks").subset("title")).get(0));
        assert manager.execute(checkout) == Command.Result.Failure;
        checkout = new CheckOutCommand(
                user,
                storage.find(Resource.AvMaterial, data.av.get("null").subset("title")).get(0));
        manager.execute(checkout).validate();

        user = storage.get(Resource.User, 1011).get();
        checkout = new CheckOutCommand(
                user,
                storage.find(Resource.Book, data.books.get("cormen").subset("title")).get(0));
        manager.execute(checkout).validate();
        checkout = new CheckOutCommand(
                user,
                storage.find(Resource.Book, data.books.get("patterns").subset("title")).get(0));
        manager.execute(checkout).validate();
        checkout = new CheckOutCommand(
                user,
                storage.find(Resource.AvMaterial, data.av.get("entropy").subset("title")).get(0));
        manager.execute(checkout).validate();
        cleanUp();
    }

    @Test
    void test8() {
        initStorage();
        for(String u: Arrays.asList("elvira", "nadia", "sergey")) {
            UserEntry entry = storage.find(Resource.User, data.users.get(u)).get(0);
            storage.find(Resource.Checkout, new QueryParameters().add("user_id", entry.getId()));
        }
        cleanUp();
    }

    void cleanUp() {
        for(String u: Arrays.asList("elvira", "nadia", "sergey")) {
            List<UserEntry> found = storage.find(Resource.User, data.users.get(u));
            if(!found.isEmpty()) {
                int id = found.get(0).getId();
                storage.removeAll(Resource.Checkout,
                        new QueryParameters().add("user_id", id));
                storage.removeAll(Resource.PendingRequest,
                        new QueryParameters().add("user_id", id));
                storage.removeAll(Resource.User,
                        new QueryParameters().add("user_id", id));
            }
        }
        for(String b: Arrays.asList("cormen", "patterns", "brooks")) {
            storage.removeAll(Resource.Book, data.books.get(b).subset("title"));
        }
        for(String a: Arrays.asList("null", "entropy")) {
            storage.removeAll(Resource.AvMaterial, data.av.get(a).subset("title"));
        }
    }

    private TestItems data = new TestItems();
    private LibraryManager manager;
    private LibraryStorage storage;
}
