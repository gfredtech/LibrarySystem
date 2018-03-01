package org.storage;


import javafx.util.Pair;
import org.resources.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;


/**
 * This class encapsulates the communication between the database and the system
 */
public class SqlStorage extends SqlQueryExecutor implements Storage {

    /**
     * Calls the constructor of the superclass
     * @see SqlQueryExecutor#SqlQueryExecutor(String, String, String)
     */
    public SqlStorage(String databaseName, String userName, String userPassword)
            throws SQLException, ClassNotFoundException {
        super(databaseName, userName, userPassword);
    }



    @Override
    public List<User> findUsers(Map<String, String> searchParameters) {
        List<User> result = new LinkedList<>();
        try {
            ResultSet rs = select("user_card", searchParameters);

            while(rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String subType = rs.getString("subtype");
                String login = rs.getString("login");
                User u = new User(id, name, type, subType);
                u.setLogin(login);
                result.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }



    @Override
    public Optional<User> getUser(int id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(id));
        List<User> users = findUsers(params);
        if(!users.isEmpty()) {
            return Optional.of(users.get(0));
        } else {
            return Optional.empty();
        }
    }



    @Override
    public List<Book> findBooks(Map<String, String> searchParameters) {
        List<Book> result = new LinkedList<>();
        try {
            ResultSet rs = select("book", searchParameters);

            while(rs.next()) {
                BookFactory f = new BookFactory();
                f.setTitle(rs.getString("title"));
                f.setPublisher(rs.getString("publisher"));
                f.setCopiesNum(rs.getInt("copy_num"));

                String[] authorsArray = (String[])rs.getArray("authors").getArray();
                List<String> authors = Arrays.asList(authorsArray);
                f.setAuthors(authors);

                String[] keywordsArray = (String[])rs.getArray("keywords").getArray();
                List<String> keywords = Arrays.asList(keywordsArray);
                f.setKeywords(keywords);

                LocalDate publicationDate = rs.getDate("publication_date").toLocalDate();
                f.setPublicationDate(publicationDate);

                result.add(f.build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }



    @Override
    public Optional<Book> getBook(int id) {
        Map<String, String> params = new HashMap<>();
        params.put("book_id", String.valueOf(id));
        List<Book> books = findBooks(params);
        if(!books.isEmpty()) {
            return Optional.of(books.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<JournalArticle> findArticles(Map<String, String> searchParameters) {
        return new LinkedList<>();
    }

    @Override
    public Optional<JournalArticle> getArticle(int id) {
        return Optional.empty();
    }

    @Override
    public List<AvMaterial> findAvMaterial(Map<String, String> searchParameters) {
        return new LinkedList<>();
    }

    @Override
    public Optional<AvMaterial> getAvMaterial(int id) {
        return Optional.empty();
    }

    @Override
    public int getNumOfCheckouts(int item_id) {
        try {
            HashMap<String, String> m = new HashMap<>();
            m.put("item_id", String.valueOf(item_id));
            ResultSet rs = select("checkout", m, Arrays.asList("count(item_id)"));
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<CheckoutRecord> getCheckoutRecordsFor(int user_id) {
        return new LinkedList<>();
    }

    @Override
    public int addUser(User user) {
        return 0;
    }

    @Override
    public int addBook(Book book) {
        List<Pair<Integer, ?>> data = new LinkedList<>();
        data.add(new Pair<>(Types.INTEGER, book.getPrice()));
        data.add(new Pair<>(Types.VARCHAR, book.getTitle()));
        data.add(new Pair<>(Types.ARRAY, book.getKeywords()));
        data.add(new Pair<>(Types.INTEGER, book.getCopiesNum()));
        data.add(new Pair<>(Types.INTEGER, "DEFAULT"));
        data.add(new Pair<>(Types.ARRAY, book.getAuthors()));
        data.add(new Pair<>(Types.DATE, book.getPublicationDate()));
        data.add(new Pair<>(Types.VARCHAR, book.getPublisher()));
        data.add(new Pair<>(Types.BOOLEAN, book.isBestseller()));
        data.add(new Pair<>(Types.BOOLEAN, book.isReference()));

        try {
            insert("book", data);
            HashMap<String, String> m = new HashMap<>();
            m.put("title", book.getTitle());
            ResultSet rs = select("book", m);
            rs.next();
            return rs.getInt("book_id");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addJournalArticle(JournalArticle article) {
        List<Pair<Integer, ?>> data = new LinkedList<>();
        data.add(new Pair<>(Types.VARCHAR, article.getTitle()));
        data.add(new Pair<>(Types.ARRAY, article.getAuthors()));
        data.add(new Pair<>(Types.ARRAY, article.getKeywords()));

        try {
            HashMap<String, String> m = new HashMap<>();
            m.put("title", article.getJournal().getTitle());
            ResultSet rs = select("journal_issue", m);
            rs.next();
            int journalId = rs.getInt("issue_id");
            data.add(new Pair<>(Types.INTEGER, journalId));

            insert("article", data);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int addJournal(JournalIssue journal) {
        List<Pair<Integer, ?>> data = new LinkedList<>();
        data.add(new Pair<>(Types.INTEGER, journal.getPrice()));
        data.add(new Pair<>(Types.VARCHAR, journal.getTitle()));
        data.add(new Pair<>(Types.ARRAY, journal.getKeywords()));
        data.add(new Pair<>(Types.INTEGER, journal.getCopiesNum()));
        data.add(new Pair<>(Types.INTEGER, "DEFAULT"));
        data.add(new Pair<>(Types.ARRAY, journal.getEditors()));
        data.add(new Pair<>(Types.VARCHAR, journal.getPublisher()));
        data.add(new Pair<>(Types.DATE, journal.getPublicationDate()));
        data.add(new Pair<>(Types.BOOLEAN, journal.isReference()));

        try {
            insert("journal_issue", data);
            HashMap<String, String> m = new HashMap<>();
            m.put("title", journal.getTitle());
            ResultSet rs = select("journal_issue", m);
            rs.next();
            return rs.getInt("issue_id");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int addAvMaterial(AvMaterial material) {
        List<Pair<Integer, ?>> data = new LinkedList<>();
        data.add(new Pair<>(Types.INTEGER, material.getPrice()));
        data.add(new Pair<>(Types.VARCHAR, material.getTitle()));
        data.add(new Pair<>(Types.ARRAY, material.getKeywords()));
        data.add(new Pair<>(Types.INTEGER, material.getCopiesNum()));
        data.add(new Pair<>(Types.INTEGER, "DEFAULT"));
        data.add(new Pair<>(Types.ARRAY, material.getAuthors()));
        data.add(new Pair<>(Types.DATE, material.isReference()));

        try {
            insert("av_material", data);
            HashMap<String, String> m = new HashMap<>();
            m.put("title", material.getTitle());
            ResultSet rs = select("av_material", m);
            rs.next();
            return rs.getInt("material_id");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addCheckoutRecord(CheckoutRecord record) {
        List<Pair<Integer, ?>> data = new LinkedList<>();

        String itemType = record.item.getType();

        try {
            HashMap<String, String> m = new HashMap<>();
            m.put("title", record.item.getTitle());
            ResultSet rs = select(itemType, m);
            rs.next();
            data.add(new Pair<>(Types.INTEGER, rs.getInt(itemType+"_id")));

            data.add(new Pair<>(Types.VARCHAR, itemType));

            m.clear();
            m.put("login", record.patron.getLogin());
            ResultSet userRs = select("user_card", m);
            userRs.next();
            data.add(new Pair<>(Types.INTEGER, userRs.getInt("user_id")));

            data.add(new Pair<>(Types.DATE, record.overdue));

            insert("checkout", data);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeUser(int user_id) {
    }

    @Override
    public void removeBook(int book_id) {
    }

    @Override
    public void removeJournalArticle(int article_id) {
    }

    @Override
    public void removeJournal(int journal_id) {
    }

    @Override
    public void removeAvMaterial(int material_id) {
    }


    @Override
    public void removeCheckoutRecord(int item_id, int user_id, String item_type, LocalDate dueDate) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("item_id", item_id);
        parameters.put("user_id", user_id);
        parameters.put("item_type", item_type);
        parameters.put("due_date", dueDate.toString());
        try{
            deleteOne("checkout", parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        super.closeConnection();
    }
}
