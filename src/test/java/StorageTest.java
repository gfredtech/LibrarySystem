import org.junit.Test;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

public class StorageTest {
    public StorageTest() {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");
        storage = LibraryStorage.getInstance();
    }

    @Test
    public void addUser() {
        QueryParameters params = new QueryParameters()
                .add("user_id")
                .add("name", "Librarian")
                .add("type", "Librarian")
                .add("login", "libra")
                .add("password", "arbil")
                .add("phone_number", "+42")
                .add("address", "...");
        storage.add(Resource.User, params);
    }

    @Test
    public void findUsers() {
        System.out.println(storage.find(Resource.User, new QueryParameters().add("type", "Faculty")));
    }

    @Test
    public void findBooks() {
        System.out.println(storage.find(Resource.User, new QueryParameters().add("title", "The Lord of The Rings")));
    }

    @Test
    public void addBook() {
        QueryParameters params = new QueryParameters()
                .add("title", "The Lord of The Rings")
                .add("copy_num", 3)
                .add("publication_date", LocalDate.of(1955, 10, 20))
                .add("publisher", "George Allen & Unwin")
                .add("is_bestseller", true)
                .add("keywords", Collections.emptyList())
                .add("authors", Arrays.asList("J. R. R. Tolkien"))
                .add("price", 9);
        storage.add(Resource.Book, params);
    }

    @Test
    public void addJournal() {
        QueryParameters params = new QueryParameters()
                .add("authors", Arrays.asList("Someone"))
                .add("publication_date", LocalDate.of(2000, 12, 20))
                .add("publisher", "Some")
                .add("copy_num", 5)
                .add("price", 100)
                .add("title", "Gamedev Prime")
                .add("keywords", Arrays.asList("GameDev"));

        storage.add(Resource.JournalIssue, params);
    }

    @Test
    public void addJournalArticle() {/*
        JournalArticleFactory j = new JournalArticleFactory();
        j.authors(Arrays.asList("J. A. Brown"));
        j.setJournalIssue(storage.find(Resource.JournalIssue,
                new QueryParameters().add("title", "Gamedev Prime")).get(0).getItem());
        j.keywords(Collections.emptyList());
        j.title("Some bright article about gamedev");
        storage.add(Resource.JournalArticle, new );*/
    }

    @Test
    public void updateArticle() {
        storage.updateAll(Resource.JournalArticle,
                new QueryParameters().add("title", "Some bright article about gamedev"),
                new QueryParameters().add("price", 100));
    }


    public void finalize() {
        try {
            storage.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private final LibraryStorage storage;
}
