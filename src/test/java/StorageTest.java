import org.junit.After;
import org.junit.Test;
import org.resources.*;
import org.storage.QueryParameters;
import org.storage.SqlStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

public class StorageTest {
    public StorageTest() {
        try {
            SqlStorage.connect("library", "librarian", "tabula_rasa");
            storage = SqlStorage.getInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addUser() {
        User libra = new User(-1, "Librarian", "Librarian", null);
        libra.setLogin("libra");
        libra.setPassword("arbil");
        libra.setPhoneNumber("+42");
        libra.setAddress("...");
        storage.addUser(libra);
    }

    @Test
    public void findUsers() {
        System.out.println(storage.findUsers(new QueryParameters().add("type", "Faculty")));
    }

    @Test
    public void findBooks() {
        System.out.println(storage.findBooks(new QueryParameters().add("title", "The Lord of The Rings")));
    }

    @Test
    public void addBook() {
        BookFactory f = new BookFactory();
        f.setTitle("The Lord of The Rings");
        f.setCopiesNum(3);
        f.setPublicationDate(LocalDate.of(1955, 10, 20));
        f.setPublisher("George Allen & Unwin");
        f.setAsBestseller();
        f.setKeywords(Collections.emptyList());
        f.setAuthors(Arrays.asList("J. R. R. Tolkien"));
        f.setPrice(9);
        storage.addBook(f);
    }

    @Test
    public void addJournal() {
        JournalIssueFactory j = new JournalIssueFactory();
        j.setEditors(Arrays.asList("Someone"));
        j.setPublicationDate(LocalDate.of(2000, 12, 20));
        j.setPublisher("Some");
        j.setCopiesNum(5);
        j.setPrice(100);
        j.setTitle("Gamedev Prime");
        j.setKeywords(Arrays.asList("GameDev"));

        storage.addJournal(j);
    }

    @Test
    public void addJournalArticle() {
        JournalArticleFactory j = new JournalArticleFactory();
        j.setAuthors(Arrays.asList("J. A. Brown"));
        j.setJournalIssue(storage.findJournals(
                new QueryParameters().add("title", "Gamedev Prime")).get(0));
        j.setKeywords(Collections.emptyList());
        j.setTitle("Some bright article about gamedev");
        storage.addJournalArticle(j);
    }

    @Test
    public void updateArticle() {
        JournalArticle a = storage.findArticles(
                new QueryParameters().add("title", "Some bright article about gamedev")).get(0);
        storage.updateJournalArticle(a.getId(), new QueryParameters().add("price", 100));
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
