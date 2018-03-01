import org.controller.BookingController;
import org.controller.ReturnController;
import org.junit.Test;
import org.storage.SqlStorage;

import java.sql.SQLException;
import java.time.LocalDate;


public class ReturnSystemTest {

    @Test
    public void createSystem() throws SQLException, ClassNotFoundException {
        SqlStorage s = new SqlStorage("library", "librarian", "tabula_rasa");
        ReturnController controller = new ReturnController(s);
        try{
            s.removeCheckoutRecord(17, 5, "book", LocalDate.of(2018, 03, 22));

        } catch (RuntimeException e) {
            System.out.println(s.getNumOfCheckouts(17));
            e.printStackTrace();
        }
        s.closeConnection();
    }
}
