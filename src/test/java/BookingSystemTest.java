import org.controller.BookingController;
import org.junit.Test;
import org.resources.*;
import org.storage.QueryParameters;
import org.storage.SqlStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class BookingSystemTest {

    @Test
    public void createSystem() throws SQLException, ClassNotFoundException {
        SqlStorage.connect("library", "librarian", "tabula_rasa");
        SqlStorage s = SqlStorage.getInstance();
        BookingController controller = new BookingController(s);
        Book b = s.findBooks(new QueryParameters()
                .add("title", "The Lord of The Rings")).get(0);
        JournalArticle j = s.findArticles(new QueryParameters()
                .add("authors", Arrays.asList("J. A. Brown"))).get(0);
        User u  = s.findUsers(new QueryParameters()
                .add("login", "harrm")).get(0);

        try{
            controller.checkOut(u.getCardNumber(), "book", b.getId());
            controller.checkOut(u.getCardNumber(), "article", j.getId());

        } catch (BookingController.CheckoutException e) {
            System.out.println(s.getNumOfCheckouts(17));
            e.printStackTrace();
        }
        s.closeConnection();
    }

    @Test
    public void testCase1() throws SQLException, ClassNotFoundException {
        SqlStorage.connect("postgres", "postgres", "minerva");
        SqlStorage s = SqlStorage.getInstance();

        BookFactory factory = new BookFactory();
        factory.setTitle("Introduction to algorithms");
        factory.setCopiesNum(3);
        factory.setPublisher("MIT Press");
        factory.setAuthors(new ArrayList<String>() {{
            add("Charles E. Leiserson");
            add("Thomas H. Cormen");
            add("Ronald L. Rivest");
            add("Clifford Stein");
        }});
        factory.setPrice(100);
        factory.setPublicationDate(LocalDate.of(2009, 1, 1));
        factory.setKeywords(new ArrayList<String>(){{
            add("data structures");
            add("algorithms");
            add("computer science");
            add("complexity theory");
        }});
        factory.setAsNonReference();
        factory.setAsNonBestseller();


        s.addBook(factory);

        factory = new BookFactory();
        factory.setTitle("Design Patterns: Elements of Reusable Object-Oriented Software");
        factory.setCopiesNum(2);
        factory.setPublisher("Addison-Wesley Professional");
        factory.setAuthors(new ArrayList<String>() {{
            add("Erich Gamma");
            add("Ralph Johnson");
            add("John Vlissides");
            add("Richard Helm");
        }});
        factory.setPrice(100);
        factory.setPublicationDate(LocalDate.of(2003, 1, 1));
        factory.setKeywords(new ArrayList<String>(){{
            add("programming");
            add("systems");
            add("computer science");
            add("design patterns");
        }});
        factory.setAsNonReference();
        factory.setAsBestseller();
        s.addBook(factory);



        factory = new BookFactory();
        factory.setTitle("TheMythicalMan-month");
        factory.setCopiesNum(1);
        factory.setPublisher("Addison-Wesley Longman Publishing Co., Inc.");
        factory.setAuthors(new ArrayList<String>() {{
            add("Brooks,Jr.");
            add(" Frederick P.");
        }});
        factory.setPrice(100);
        factory.setPublicationDate(LocalDate.of(1995, 1, 1));
        factory.setKeywords(new ArrayList<String>(){{
            add("computer programming");
            add("systems analysis");

        }});
        factory.setAsReference();
        factory.setAsNonBestseller();
        s.addBook(factory);




        AvMaterialFactory avMaterialFactory = new AvMaterialFactory();
        avMaterialFactory.setTitle("Null References: The Billion Dollar Mistake");
        avMaterialFactory.setPrice(100);
        avMaterialFactory.setCopiesNum(1);
        avMaterialFactory.setKeywords(new ArrayList<String>() {{
            add("Tony Hoare");
        }});

        s.addAvMaterial(avMaterialFactory);

        avMaterialFactory = new AvMaterialFactory();
        avMaterialFactory.setTitle("Information Entropy");
        avMaterialFactory.setCopiesNum(1);

        avMaterialFactory.setKeywords(new ArrayList<String>() {{
            add("Claude Shannon");
        }});

        s.addAvMaterial(avMaterialFactory);



        User p1 = new User(1010, "Sergey Afonso", "Patron", "Faculty");
        p1.setAddress("ViaMargutta, 3");
        p1.setPhoneNumber("30001");
        s.addUser(p1);



        User p2 = new User(1011, "Nadia Teixeira", "Patron", "Student");
        p2.setAddress("Via Sacra, 13");
        p2.setPhoneNumber("30002");
        s.addUser(p2);

        User p3 = new User(1100, "Elvira Espindola", "Patron", "Student");
        p3.setPhoneNumber("30003");
        p3.setAddress("Via del Corso, 22");
        s.addUser(p3);

        s.closeConnection();



    }
}
