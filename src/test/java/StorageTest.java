import org.junit.After;
import org.junit.Test;
import org.resources.Book;
import org.resources.BookFactory;
import org.storage.SqlStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StorageTest {
    public StorageTest() {
        try {
            storage = new SqlStorage("library", "librarian", "tabula_rasa");
        } catch (SQLException|ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findUsers() {
        Map<String, String> searchParameters = new HashMap<>();
        searchParameters.put("type", "Faculty");
        System.out.println(storage.findUsers(searchParameters));
    }

    @Test
    public void findBooks() {
        Map<String, String> searchParameters = new HashMap<>();
        searchParameters.put("title", "The Lord of The Rings");
        System.out.println(storage.findBooks(searchParameters));
    }

    @Test
    public void addBook() {
        BookFactory f = new BookFactory();
        f.setTitle("The Lord of The Rings");
        f.setCopiesNum(3);
        f.setPublicationDate(LocalDate.of(1955, 10, 20));
        f.setPublisher("George Allen & Unwin");
        f.setAsBestseller();
        f.setAuthors(Arrays.asList("J. R. R. Tolkien"));
        f.setPrice(9);
        Book b = f.build();
        storage.addBook(b);
    }

    @After
    public void finalize() {
        try {
            storage.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private final SqlStorage storage;
}
