import org.controller.BookingController;
import org.controller.ReturnController;
import org.junit.Before;
import org.junit.Test;
import org.resources.*;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.Storage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;


public class BookReturnEditSystemTests {

    @Before
    public void init() throws ClassNotFoundException {
        SqlStorage.connect("library", "librarian", "tabula_rasa");
        storage = SqlStorage.getInstance();
        returner = new ReturnController(storage);
        booker = new BookingController(storage);
    }

    @Test
    public void test1() {
        BookFactory b1 = new BookFactory();
        b1.setTitle("Introduction to Algorithms, Third edition");
        b1.setAuthors(Arrays.asList("Thomas H. Cormen", "Charles E. Leiserson",
                                    "Ronald L. Rivest", "Clifford Stein"));
        b1.setPublisher("MIT Press");
        b1.setPublicationDate(LocalDate.of(2009, 1, 1));
        b1.setCopiesNum(3);
        b1.setKeywords(Collections.emptyList());

        storage.addBook(b1);

        BookFactory b2 = new BookFactory();
        b2.setTitle("Design Patterns: Elements of Reusable Object-Oriented Software, First edition");
        b2.setAuthors(Arrays.asList("Erich Gamma", "Ralph Johnson", "John Vlissides", "Richard Helm"));
        b2.setPublisher("Addison-Wesley Professional");
        b2.setPublicationDate(LocalDate.of(2003, 1, 1));
        b2.setAsBestseller();
        b2.setCopiesNum(2);
        b2.setKeywords(Collections.emptyList());

        storage.addBook(b2);

        BookFactory b3 = new BookFactory();
        b3.setTitle("The Mythical Man-month, Second edition");
        b3.setAuthors(Arrays.asList("Brooks", "Jr.", "Frederick P."));
        b3.setPublisher("Addison-Wesley Longman Publishing Co., Inc.");
        b3.setPublicationDate(LocalDate.of(1995, 1, 1));
        b3.setAsReference();
        b3.setCopiesNum(1);
        b3.setKeywords(Collections.emptyList());

        storage.addBook(b3);

        AvMaterialFactory av1 = new AvMaterialFactory();
        av1.setTitle("Null References: The Billion Dollar Mistake");
        av1.setAuthors(Arrays.asList("Tony Hoare"));
        av1.setCopiesNum(1);
        av1.setKeywords(Collections.emptyList());

        storage.addAvMaterial(av1);

        AvMaterialFactory av2 = new AvMaterialFactory();
        av2.setTitle("Information Entropy");
        av2.setAuthors(Arrays.asList("Claude Shannon"));
        av2.setCopiesNum(1);
        av2.setKeywords(Collections.emptyList());

        storage.addAvMaterial(av2);

        User p1 = new User(1010, "Sergey Afonso", "Patron", "Faculty");
        p1.setPhoneNumber("30001");
        p1.setAddress("Via Margutta, 3");
        p1.setLogin("s.afonso");
        storage.addUser(p1);

        User p2 = new User(1011, "Nadia Teixeira", "Patron", "Student");
        p2.setPhoneNumber("30002");
        p2.setAddress("Via Sacra, 13");
        p2.setLogin("n.teixeira");
        storage.addUser(p2);

        User p3 = new User(1100, "Elvira Espindola", "Patron", "Student");
        p3.setPhoneNumber("30003");
        p3.setAddress("Via del Corso, 22");
        p3.setLogin("e.espindola");
        storage.addUser(p3);

    }

    @Test
    public void test2() {
        storage.removeUser(1011);
        Book b = storage.findBooks(
                new QueryParameters()
                        .add("title", "Introduction to Algorithms, Third edition")
        ).get(0);
        storage.updateBook(b.getId(), new QueryParameters().add("copy_num", 1));

        b = storage.findBooks(
                new QueryParameters()
                        .add("title", "The Mythical Man-month, Second edition")
        ).get(0);

        storage.removeBook(b.getId());

        storage.removeUser(1011);

    }

    @Test
    public void test3() {
        System.out.println(storage.findUsers(new QueryParameters().add("user_id", 1010)).get(0));
        System.out.println(storage.findUsers(new QueryParameters().add("user_id", 1100)).get(0));
    }

    @Test
    public void test4() {
        try {
            storage.findUsers(new QueryParameters().add("user_id", 1011)).get(0);
            assert false;
        } catch (IndexOutOfBoundsException e) {
            // correct
        }
        System.out.println(storage.findUsers(new QueryParameters().add("user_id", 1100)).get(0));
    }

    @Test
    public void test5() {
        try{
            booker.checkOut(1011, "book",
                    storage.findBooks(new QueryParameters()
                            .add("title", "Introduction to Algorithms, Third edition")).get(0).getId());
            assert false;
        } catch (IllegalArgumentException e) {
            // correct
        }
    }

    @Test
    public void test6() {
        booker.checkOut(1010, "book",
                storage.findBooks(new QueryParameters()
                        .add("title", "Introduction to Algorithms, Third edition")).get(0).getId());
        try {
            booker.checkOut(1100, "book",
                    storage.findBooks(new QueryParameters()
                            .add("title", "Introduction to Algorithms, Third edition")).get(0).getId());
            assert false;
        } catch (BookingController.CheckoutException e) {
            // correct
        }
        booker.checkOut(1010, "book",
                storage.findBooks(new QueryParameters()
                        .add("title", "Design Patterns: Elements of Reusable Object-Oriented Software, First edition")).get(0).getId());

    }


    @Test
    public void test7() {
        try {
            booker.checkOut(1010, "book",
                    storage.findBooks(new QueryParameters()
                            .add("title", "Introduction to Algorithms, Third edition")).get(0).getId());
        } catch (BookingController.CheckoutException e) {
            ;
        }
        try {
            booker.checkOut(1010, "book",
                    storage.findBooks(new QueryParameters()
                            .add("title", "Design Patterns: Elements of Reusable Object-Oriented Software, First edition")).get(0).getId());
        } catch (BookingController.CheckoutException e) {
            ;
        }
        try {
            booker.checkOut(1010, "book",
                    storage.findBooks(new QueryParameters()
                            .add("title", "The Mythical Man-month, Second edition")).get(0).getId());
        } catch (BookingController.CheckoutException e) {
            ;
        }
        try {
            booker.checkOut(1010, "av_material",
                    storage.findAvMaterials(new QueryParameters()
                            .add("title", "Null References: The Billion Dollar Mistake")).get(0).getId());
        } catch (BookingController.CheckoutException e) {
            ;
        }
        try {
            booker.checkOut(1011, "book",
                    storage.findBooks(new QueryParameters()
                            .add("title", "Introduction to Algorithms, Third edition")).get(0).getId());
        } catch (BookingController.CheckoutException e) {
            ;
        }
        try {
            booker.checkOut(1011, "book",
                    storage.findBooks(new QueryParameters()
                            .add("title", "Design Patterns: Elements of Reusable Object-Oriented Software, First edition")).get(0).getId());
        } catch (BookingController.CheckoutException e) {
            ;
        }
        try {
            booker.checkOut(1011, "av_material",
                    storage.findAvMaterials(new QueryParameters()
                            .add("title", "Information Entropy")).get(0).getId());
        } catch (BookingController.CheckoutException e) {
            ;
        }
    }

    @Test
    public void test8() {
        System.out.println(storage.getCheckoutRecordsFor(1010));
        System.out.println(storage.getCheckoutRecordsFor(1011));
        System.out.println(storage.getCheckoutRecordsFor(1100));
    }

    @Test
    public void cleanUp() {
        for(CheckoutRecord c: storage.getCheckoutRecordsFor(1010)) {
            storage.removeCheckoutRecord(c);
        }
        for(CheckoutRecord c: storage.getCheckoutRecordsFor(1011)) {
            storage.removeCheckoutRecord(c);
        }
        for(CheckoutRecord c: storage.getCheckoutRecordsFor(1100)) {
            storage.removeCheckoutRecord(c);
        }
        storage.removeUser(1010);
        storage.removeUser(1011);
        storage.removeUser(1100);
        storage.removeBook(storage.findBooks(new QueryParameters()
                .add("title", "The Mythical Man-month, Second edition")).get(0).getId());
        storage.removeBook(storage.findBooks(new QueryParameters()
                .add("title", "Introduction to Algorithms, Third edition")).get(0).getId());
        storage.removeBook(storage.findBooks(new QueryParameters()
                .add("title", "Design Patterns: Elements of Reusable Object-Oriented Software, First edition")).get(0).getId());
        storage.removeAvMaterial(storage.findAvMaterials(new QueryParameters()
                .add("title", "Information Entropy")).get(0).getId());
        storage.removeAvMaterial(storage.findAvMaterials(new QueryParameters()
                .add("title", "Null References: The Billion Dollar Mistake")).get(0).getId());
    }

    BookingController booker;
    ReturnController returner;
    Storage storage;
}
