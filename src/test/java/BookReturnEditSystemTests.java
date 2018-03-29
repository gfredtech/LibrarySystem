import org.controller.CheckOutCommand;
import org.controller.Command;
import org.controller.LibraryManager;
import org.items.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.storage.EntrySerializer;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.Storage;
import org.storage.resources.*;

import java.time.LocalDate;
import java.util.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookReturnEditSystemTests {

    @BeforeAll
    void init() throws ClassNotFoundException {
        SqlStorage.connect("library", "librarian", "tabula_rasa");
        storage = SqlStorage.getInstance();
        manager = new LibraryManager(storage);
        initItemData();
        cleanUp();
    }

    private void initItemData() {
        QueryParameters b1 = new QueryParameters()
                .add("title", "Introduction to Algorithms, Third edition")
                .add("authors", Arrays.asList("Thomas H. Cormen", "Charles E. Leiserson",
                        "Ronald L. Rivest", "Clifford Stein"))
                .add("publisher", "MIT Press")
                .add("publication_date", LocalDate.of(2009, 1, 1))
                .add("copy_num", 3)
                .add("price", 0)
                .add("keywords", Collections.emptyList());
        itemData.put("cormen", b1);

        QueryParameters b2 = new QueryParameters()
                .add("title", "Design Patterns: Elements of Reusable Object-Oriented Software, First edition")
                .add("authors", Arrays.asList("Erich Gamma", "Ralph Johnson", "John Vlissides", "Richard Helm"))
                .add("publisher", "Addison-Wesley Professional")
                .add("publication_date", LocalDate.of(2003, 1, 1))
                .add("is_bestseller", true)
                .add("price", 0)
                .add("copy_num", 2)
                .add("keywords", Collections.emptyList());
        itemData.put("patterns", b2);


        QueryParameters b3 = new QueryParameters()
                .add("title", "The Mythical Man-month, Second edition")
                .add("authors", Arrays.asList("Brooks", "Jr.", "Frederick P."))
                .add("publisher", "Addison-Wesley Longman Publishing Co., Inc.")
                .add("publication_date", LocalDate.of(1995, 1, 1))
                .add("is_reference", true)
                .add("price", 0)
                .add("copy_num", 1)
                .add("keywords", Collections.emptyList());
        itemData.put("brooks", b3);
        QueryParameters av1 = new QueryParameters()
                .add("title", "Null References: The Billion Dollar Mistake")
                .add("authors", Collections.singletonList("Tony Hoare"))
                .add("price", 0)
                .add("copy_num", 1)
                .add("keywords", Collections.emptyList());
        itemData.put("null", av1);
        QueryParameters av2 = new QueryParameters()
                .add("title", "Information Entropy")
                .add("authors", Arrays.asList("Claude Shannon"))
                .add("price", 0)
                .add("copy_num", 1)
                .add("keywords", Collections.emptyList());
        itemData.put("entropy", av2);

        User p1 = new User(
                1010, "Sergey Afonso", "Faculty", "Professor");
        p1.setPhoneNumber("30001");
        p1.setAddress("Via Margutta, 3");
        p1.setLogin("s.afonso");
        itemData.put("sergey", EntrySerializer.serialize(p1));

        User p2 = new User(
                1011, "Nadia Teixeira", "Student", null);
        p2.setPhoneNumber("30002");
        p2.setAddress("Via Sacra, 13");
        p2.setLogin("n.teixeira");
        itemData.put("nadia", EntrySerializer.serialize(p2));

        User p3 = new User(
                1100, "Elvira Espindola", "Student", null);
        p3.setPhoneNumber("30003");
        p3.setAddress("Via del Corso, 22");
        p3.setLogin("e.espindola");
        itemData.put("elvira", EntrySerializer.serialize(p3));
    }

    private void initStorage() {
        initItemData();
        storage.add(Resource.Book, itemData.get("cormen"));
        storage.add(Resource.Book, itemData.get("patterns"));
        storage.add(Resource.Book, itemData.get("brooks"));
        storage.add(Resource.AvMaterial, itemData.get("null"));
        storage.add(Resource.AvMaterial, itemData.get("entropy"));
        itemData.get("cormen").remove("copy_num");
        itemData.get("patterns").remove("copy_num");
        itemData.get("brooks").remove("copy_num");
        itemData.get("null").remove("copy_num");
        itemData.get("entropy").remove("copy_num");
        storage.add(Resource.User, itemData.get("sergey"));
        storage.add(Resource.User, itemData.get("elvira"));
        storage.add(Resource.User, itemData.get("nadia"));
    }

    private void modifyStorage() {
        storage.removeAll(Resource.User, new QueryParameters().add("user_id", 1011));

        storage.updateAll(Resource.Book, itemData.get("cormen"),
                new QueryParameters().add("copy_num", 1));

        storage.removeAll(Resource.Book, itemData.get("brooks"));
        storage.removeAll(Resource.User, new QueryParameters().add("user_id", 1011));
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
        storage.get(Resource.User, 1010).get();
        storage.get(Resource.User, 1100).get();
        cleanUp();
    }

    @Test
    void test4() {
        initStorage();
        modifyStorage();
        try {
            storage.find(Resource.User, new QueryParameters().add("user_id", 1011)).get(0);
            assert false;
        } catch (IndexOutOfBoundsException e) {
            // correct
        }
        storage.find(Resource.User, new QueryParameters().add("user_id", 1100)).get(0);
        cleanUp();
    }

    @Test
    void test5() {
        initStorage();
        modifyStorage();

        try {
            BookEntry b = storage.find(Resource.Book, itemData.get("cormen")).get(0);
            UserEntry u = storage.get(Resource.User, 1011).get();
            manager.execute(new CheckOutCommand(u, b));
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
        Command checkout = new CheckOutCommand(
                storage.get(Resource.User,1010).get(),
                storage.find(Resource.Book, itemData.get("cormen")).get(0));
        manager.execute(checkout);
        Command failingCheckout = new CheckOutCommand(
                storage.get(Resource.User, 1100).get(),
                storage.find(Resource.Book, itemData.get("cormen")).get(0));

        assert manager.execute(failingCheckout) == Command.Result.Failure;
        checkout = new CheckOutCommand(
                storage.get(Resource.User, 1010).get(),
                storage.find(Resource.Book, itemData.get("patterns")).get(0));
        manager.execute(checkout);
        cleanUp();
    }


    @Test
    void test7() {
        initStorage();
        Command checkout = new CheckOutCommand(
                storage.get(Resource.User, 1010).get(),
                storage.find(Resource.Book, itemData.get("cormen")).get(0));
        manager.execute(checkout);
        checkout = new CheckOutCommand(
                storage.get(Resource.User, 1010).get(),
                storage.find(Resource.Book, itemData.get("patterns")).get(0));
        manager.execute(checkout);
        checkout = new CheckOutCommand(
                storage.get(Resource.User, 1010).get(),
                storage.find(Resource.Book, itemData.get("brooks")).get(0));
        manager.execute(checkout);
        checkout = new CheckOutCommand(
                storage.get(Resource.User, 1010).get(),
                storage.find(Resource.AvMaterial, itemData.get("null")).get(0));
        manager.execute(checkout);
        checkout = new CheckOutCommand(
                storage.get(Resource.User, 1011).get(),
                storage.find(Resource.Book, itemData.get("cormen")).get(0));
        manager.execute(checkout);
        checkout = new CheckOutCommand(
                storage.get(Resource.User, 1011).get(),
                storage.find(Resource.Book, itemData.get("patterns")).get(0));
        manager.execute(checkout);
        checkout = new CheckOutCommand(
                storage.get(Resource.User, 1011).get(),
                storage.find(Resource.AvMaterial, itemData.get("entropy")).get(0));
        manager.execute(checkout);
        cleanUp();
    }

    @Test
    void test8() {
        initStorage();
        for(String u: Arrays.asList("elvira", "nadia", "sergey")) {
            UserEntry entry = storage.find(Resource.User, itemData.get(u)).get(0);
            storage.find(Resource.Checkout, new QueryParameters().add("user_id", entry.getId()));
        }
        cleanUp();
    }

    void cleanUp() {
        for(String u: Arrays.asList("elvira", "nadia", "sergey")) {
            List<UserEntry> found = storage.find(Resource.User, itemData.get(u));
            if(!found.isEmpty()) {
                int id = found.get(0).getId();
                storage.removeAll(Resource.Checkout,
                        new QueryParameters().add("user_id", id));
                storage.removeAll(Resource.User,
                        new QueryParameters().add("user_id", id));
            }
        }
        for(String b: Arrays.asList("cormen", "patterns", "brooks")) {
            storage.removeAll(Resource.Book, itemData.get(b));
        }
        for(String a: Arrays.asList("null", "entropy")) {
            storage.removeAll(Resource.AvMaterial, itemData.get(a));
        }
    }

    private HashMap<String, QueryParameters> itemData = new HashMap<>();
    private LibraryManager manager;
    private Storage storage;
}
