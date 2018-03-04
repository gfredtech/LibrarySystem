import org.controller.ReturnController;
import org.junit.Test;
import org.resources.Book;
import org.resources.User;
import org.storage.QueryParameters;
import org.storage.SqlStorage;

import java.sql.SQLException;



public class ReturnSystemTest {

    @Test
    public void createSystem() throws SQLException, ClassNotFoundException {
        SqlStorage.connect("library", "librarian", "tabula_rasa");
        SqlStorage s = SqlStorage.getInstance();
        ReturnController controller = new ReturnController(s);
        Book b = s.findBooks(new QueryParameters().add("title", "The Lord of The Rings")).get(0);
        User u  = s.findUsers(new QueryParameters().add("login", "harrm")).get(0);

        try{
            controller.returnItem(u.getCardNumber(), "book", b.getId());

        } catch (RuntimeException e) {
            System.out.println(s.getNumOfCheckouts(b.getId()));
            e.printStackTrace();
        }
        s.closeConnection();
    }
}
