package org.storage;

import org.resources.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Represents an abstract storage of library materials and records (both user and checkout)
 */
public interface Storage {

    List<User> findUsers(Map<String, String> searchParameters);
    Optional<User> getUser(int id);

    List<Book> findBooks(Map<String, String> searchParameters);
    Optional<Book> getBook(int id);

    List<JournalArticle> findArticles(Map<String, String> searchParameters);
    Optional<JournalArticle> getArticle(int id);

    List<AvMaterial> findAvMaterial(Map<String, String> searchParameters);
    Optional<AvMaterial> getAvMaterial(int id);

    int getNumOfCheckouts(int item_id);
    List<CheckoutRecord> getCheckoutRecordsFor(int user_id);

    int addUser(User user);
    int addBook(Book book);
    void addJournalArticle(JournalArticle article);
    int addJournal(JournalIssue journal);
    int addAvMaterial(AvMaterial material);
    void addCheckoutRecord(CheckoutRecord record);

    void removeUser(int user_id);
    void removeBook(int book_id);
    void removeJournalArticle(int article_id);
    void removeJournal(int journal_id);
    void removeAvMaterial(int material_id);
    void removeCheckoutRecord(int item_id, int user_id, String item_type, LocalDate dueDate);

}
