import org.controller.BookingController;
import org.controller.ReturnController;
import org.junit.Test;
import org.resources.Book;
import org.resources.CheckoutRecord;
import org.resources.User;
import org.storage.QueryParameters;
import org.storage.SqlStorage;

import java.sql.SQLException;
import java.time.LocalDate;


public class ReturnSystemTest {

    @Test
    public void createSystem() throws SQLException, ClassNotFoundException {
        SqlStorage.connect("library", "librarian", "tabula_rasa");
        SqlStorage s = SqlStorage.getInstance();
        ReturnController controller = new ReturnController(s);
        Book b = s.findBooks(new QueryParameters().add("title", "The Lord of The Rings")).get(0);
        User u  = s.findUsers(new QueryParameters().add("login", "harrm")).get(0);
        CheckoutRecord r = (CheckoutRecord)s.getCheckoutRecordsFor(u.getCardNumber()).stream().filter(i -> i.item.getId() == b.getId()).toArray()[0];

        try{
            s.removeCheckoutRecord(r);

        } catch (RuntimeException e) {
            System.out.println(s.getNumOfCheckouts(17));
            e.printStackTrace();
        }
        s.closeConnection();
    }
}
