package org.storage;

import org.resources.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * Represents an abstract storage of library materials and records (both user and checkout)
 */
public interface Storage {

    List<User> findUsers(QueryParameters searchParameters);
    Optional<User> getUser(int id);

    List<Book> findBooks(QueryParameters searchParameters);
    Optional<Book> getBook(int id);

    List<JournalArticle> findArticles(QueryParameters searchParameters);
    Optional<JournalArticle> getArticle(int id);

    List<AvMaterial> findAvMaterials(QueryParameters searchParameters);
    Optional<AvMaterial> getAvMaterial(int id);

    List<JournalIssue> findJournals(QueryParameters searchParameters);
    Optional<JournalIssue> getJournal(int id);

    int getNumOfCheckouts(int item_id);
    List<CheckoutRecord> getCheckoutRecordsFor(int user_id);

    User addUser(User user);
    Book addBook(BookFactory book);
    JournalArticle addJournalArticle(JournalArticleFactory article);
    JournalIssue addJournal(JournalIssueFactory journal);
    AvMaterial addAvMaterial(AvMaterialFactory material);
    void addCheckoutRecord(CheckoutRecord record);

    void removeUser(int user_id);
    void removeBook(int book_id);
    void removeJournalArticle(int article_id);
    void removeJournal(int journal_id);
    void removeAvMaterial(int material_id);
    void removeCheckoutRecord(CheckoutRecord record);

    void updateUser(int user_id, User data);
    void updateBook(int book_id, Book data);
    void updateJournalArticle(int article_id, JournalArticle article);
    void updateJournal(int journal_id, JournalIssue journal);
    void updateAvMaterial(int material_id, AvMaterial material);

}
