import org.controller.BookingController;
import org.junit.Test;
import org.storage.SqlStorage;

import java.sql.SQLException;

public class BookingSystemTest {

    @Test
    public void createSystem() throws SQLException, ClassNotFoundException {
        SqlStorage s = new SqlStorage("library", "librarian", "tabula_rasa");
        BookingController controller = new BookingController(s);
        try{
            controller.checkOut(5, "book", 17);
        } catch (BookingController.CheckoutException e) {
            System.out.println(s.getNumOfCheckouts(17));
            e.printStackTrace();
        }
        s.closeConnection();
    }
}
