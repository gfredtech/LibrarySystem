import org.controller.CheckOutCommand;
import org.controller.ReturnCommand;
import org.items.*;
import org.junit.Before;
import org.junit.Test;
import org.storage.EntrySerializer;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.Storage;
import org.storage.resources.AvMaterialEntry;
import org.storage.resources.BookEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;
import java.util.*;


public class BookReturnEditSystemTests {

    @Before
    public void init() throws ClassNotFoundException {
        SqlStorage.connect("library", "librarian", "tabula_rasa");
        storage = SqlStorage.getInstance();
        returner = new ReturnCommand(storage);
        booker = new CheckOutCommand(storage);
        initItemData();
        cleanUp();
    }

    void initItemData() {
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
                .add("authors", Arrays.asList("Tony Hoare"))
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

        User p1 = new User(1010, "Sergey Afonso", "Patron", "Faculty");
        p1.setPhoneNumber("30001");
        p1.setAddress("Via Margutta, 3");
        p1.setLogin("s.afonso");
        itemData.put("sergey", EntrySerializer.serialize(p1));

        User p2 = new User(1011, "Nadia Teixeira", "Patron", "Student");
        p2.setPhoneNumber("30002");
        p2.setAddress("Via Sacra, 13");
        p2.setLogin("n.teixeira");
        itemData.put("nadia", EntrySerializer.serialize(p2));

        User p3 = new User(1100, "Elvira Espindola", "Patron", "Student");
        p3.setPhoneNumber("30003");
        p3.setAddress("Via del Corso, 22");
        p3.setLogin("e.espindola");
        itemData.put("elvira", EntrySerializer.serialize(p3));
    }

    void initStorage() {
        storage.add(Resource.Book, itemData.get("cormen"));
        storage.add(Resource.Book, itemData.get("patterns"));
        storage.add(Resource.Book, itemData.get("brooks"));
        storage.add(Resource.AvMaterial, itemData.get("null"));
        storage.add(Resource.AvMaterial, itemData.get("entropy"));
        storage.add(Resource.User, itemData.get("sergey"));
        storage.add(Resource.User, itemData.get("elvira"));
        storage.add(Resource.User, itemData.get("nadia"));
    }

    void modifyStorage() {
        storage.remove(Resource.User, 1011);

        BookEntry b = storage.find(Resource.Book, itemData.get("cormen")).get(0);
        storage.update(Resource.Book, b.getId(), new QueryParameters().add("copy_num", 1));

        b = storage.find(Resource.Book, itemData.get("brooks")).get(0);
        storage.remove(Resource.Book, b.getId());
        storage.remove(Resource.User, 1011);
    }

    @Test
    public void test1() {
        initStorage();
        cleanUp();
    }

    @Test
    public void test2() {
        initStorage();
        modifyStorage();
        cleanUp();
    }

    @Test
    public void test3() {
        initStorage();
        modifyStorage();
        storage.get(Resource.User, 1010).get();
        storage.get(Resource.User, 1100).get();
        cleanUp();
    }

    @Test
    public void test4() {
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
    public void test5() {
        initStorage();
        modifyStorage();

        try {
            BookEntry b = storage.find(Resource.Book, itemData.get("cormen")).get(0);
            UserEntry u = storage.get(Resource.User, 1011).get();
            booker.checkOut(u, b);
            assert false;
        } catch (NoSuchElementException e) {
            // correct
        }

        cleanUp();
    }

    @Test
    public void test6() {
        initStorage();
        booker.checkOut(storage.get(Resource.User,1010).get(),
                storage.find(Resource.Book, itemData.get("cormen")).get(0));
        try {
            booker.checkOut(storage.get(Resource.User, 1100).get(),
                    storage.find(Resource.Book, itemData.get("cormen")).get(0));
            assert false;
        } catch (CheckOutCommand.CheckoutException e) {
            // correct
        }
        booker.checkOut(storage.get(Resource.User, 1010).get(),
                storage.find(Resource.Book, itemData.get("patterns")).get(0));
        cleanUp();
    }


    @Test
    public void test7() {
        initStorage();
        try {
            booker.checkOut(storage.get(Resource.User, 1010).get(),
                    storage.find(Resource.Book, itemData.get("cormen")).get(0));
        } catch (CheckOutCommand.CheckoutException e) {
        }
        try {
            booker.checkOut(storage.get(Resource.User, 1010).get(),
                    storage.find(Resource.Book, itemData.get("patterns")).get(0));
            } catch (CheckOutCommand.CheckoutException e) {
        }
        try {
            booker.checkOut(storage.get(Resource.User, 1010).get(),
                    storage.find(Resource.Book, itemData.get("brooks")).get(0));
        } catch (CheckOutCommand.CheckoutException e) {
        }
        try {
            booker.checkOut(storage.get(Resource.User, 1010).get(),
                    storage.find(Resource.AvMaterial, itemData.get("null")).get(0));
        } catch (CheckOutCommand.CheckoutException e) {
        }
        try {
            booker.checkOut(storage.get(Resource.User, 1011).get(),
                    storage.find(Resource.Book, itemData.get("cormen")).get(0));
        } catch (CheckOutCommand.CheckoutException e) {
        }
        try {
            booker.checkOut(storage.get(Resource.User, 1011).get(),
                    storage.find(Resource.Book, itemData.get("patterns")).get(0));
        } catch (CheckOutCommand.CheckoutException e) {
        }
        try {
            booker.checkOut(storage.get(Resource.User, 1011).get(),
                    storage.find(Resource.AvMaterial, itemData.get("entropy")).get(0));
        } catch (CheckOutCommand.CheckoutException e) {
        }
        cleanUp();
    }

    @Test
    public void test8() {
        initStorage();
        for(String u: Arrays.asList("elvira", "nadia", "sergey")) {
            UserEntry entry = storage.find(Resource.User, itemData.get(u)).get(0);
            storage.getCheckoutRecordsFor(entry.getId());
        }
        cleanUp();
    }

    @Test
    public void cleanUp() {
        for(String u: Arrays.asList("elvira", "nadia", "sergey")) {
            List<UserEntry> found = storage.find(Resource.User, itemData.get(u));
            if(!found.isEmpty()) {
                for (CheckoutRecord c : storage.getCheckoutRecordsFor(found.get(0).getId())) {
                    storage.removeCheckoutRecord(c);
                }
                storage.remove(Resource.User, found.get(0).getId());
            }
        }
        for(String b: Arrays.asList("cormen", "patterns", "brooks")) {
            List<BookEntry> found = storage.find(Resource.Book, itemData.get(b));
            if(!found.isEmpty()) {
                storage.remove(Resource.Book, found.get(0).getId());
            }
        }
        for(String a: Arrays.asList("null", "entropy")) {
            List<AvMaterialEntry> found = storage.find(Resource.AvMaterial, itemData.get(a));
            if(!found.isEmpty()) {
                storage.remove(Resource.AvMaterial, found.get(0).getId());
            }
        }
    }

    HashMap<String, QueryParameters> itemData = new HashMap<>();

    CheckOutCommand booker;
    ReturnCommand returner;
    Storage storage;
}
