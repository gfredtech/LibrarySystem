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
}
