package org.storage;


import org.resources.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;


/**
 * This class encapsulates the communication between the database and the system
 */
public class SqlStorage extends SqlQueryExecutor implements Storage {

    public static SqlStorage getInstance() {
        return instance;
    }

    public static void connect(String databaseName, String userName, String userPassword) throws ClassNotFoundException {
        try {
            instance = new SqlStorage(databaseName, userName, userPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls the constructor of the superclass
     * @see SqlQueryExecutor#SqlQueryExecutor(String, String, String)
     */
    private SqlStorage(String databaseName, String userName, String userPassword)
            throws SQLException, ClassNotFoundException {
        super(databaseName, userName, userPassword);
    }



    @Override
    public List<User> findUsers(QueryParameters searchParameters) {
        List<User> result = new LinkedList<>();
        try {
            ResultSet rs = select("user_card", searchParameters);

            while(rs.next()) {

                int id = rs.getInt("user_id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String subType = rs.getString("subtype");
                String login = rs.getString("login");
                String phoneNumber = rs.getString("phone_number");
                String address = rs.getString("address");
                int passwordHash = rs.getInt("password_hash");
                User u = new User(id, name, type, subType);
                u.setLogin(login);
                u.setAddress(address);
                u.setPhoneNumber(phoneNumber);
                u.setPasswordHash(passwordHash);
                result.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }



    @Override
    public Optional<User> getUser(int id) {
        QueryParameters params = new QueryParameters()
            .add("user_id", id);
        List<User> users = findUsers(params);
        if(!users.isEmpty()) {
            return Optional.of(users.get(0));
        } else {
            return Optional.empty();
        }
    }



    @Override
    public List<Book> findBooks(QueryParameters searchParameters) {
        return findItems("book", searchParameters, new BookSerializer());
    }



    @Override
    public List<JournalIssue> findJournals(QueryParameters searchParameters) {
        return findItems("journal_issue", searchParameters,
                new JournalIssueSerializer());
    }

    @Override
    public Optional<Book> getBook(int id) {
        return getItem("book", id, new BookSerializer());
    }

    @Override
    public Optional<JournalIssue> getJournal(int id) {
        return getItem("journal_issue", id, new JournalIssueSerializer());

    }

    @Override
    public List<JournalArticle> findArticles(QueryParameters searchParameters) {
        List<JournalArticle> items = findItems("article", searchParameters,
                new JournalArticleSerializer());
        for(JournalArticle item: items) {
            try{
                ResultSet rs = select("article",
                        new QueryParameters().add("article_id", item.getId()),
                        Arrays.asList("journal_id"));
                rs.next();
                JournalIssue j = getJournal(rs.getInt("journal_id")).get();
                item.initializeJournal(j);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return items;
    }

    @Override
    public Optional<JournalArticle> getArticle(int id) {
        JournalArticle article =  getItem("article", id,
                new JournalArticleSerializer()).get();
        try{
            ResultSet rs = select("article",
                    new QueryParameters().add("article_id", id),
                    Arrays.asList("journal_id"));
            rs.next();
            JournalIssue j = getJournal(rs.getInt("journal_id")).get();
            article.initializeJournal(j);
            return Optional.of(article);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AvMaterial> findAvMaterials(QueryParameters searchParameters) {
        return findItems("av_material", searchParameters,
                new AvMaterialSerializer());
    }

    @Override
    public Optional<AvMaterial> getAvMaterial(int id) {
        return getItem("av_material", id, new AvMaterialSerializer());
    }

    @Override
    public int getNumOfCheckouts(int item_id) {
        try {
            QueryParameters p = new QueryParameters()
                    .add("item_id", item_id);
            ResultSet rs = select("checkout", p,
                    Collections.singletonList("count(item_id)"));
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<CheckoutRecord> getCheckoutRecordsFor(int user_id) {
        try {
            List<CheckoutRecord> records = new LinkedList<>();
            ResultSet rs = select("checkout",
                    new QueryParameters().add("user_id", user_id));
            List<String> itemTypes = new ArrayList<>();
            List<Integer> itemIds = new ArrayList<>();
            List<LocalDate> overdueDates = new ArrayList<>();
            List<Integer> userIds = new ArrayList<>();
            while (rs.next()) {
                itemIds.add(rs.getInt("item_id"));
                itemTypes.add(rs.getString("item_type"));
                overdueDates.add(rs.getDate("due_date").toLocalDate());
                userIds.add(rs.getInt("user_id"));
            }
            for(int i = 0; i < itemTypes.size(); i++) {
                String item_type = itemTypes.get(i);
                int item_id = itemIds.get(i);
                LocalDate due_date = overdueDates.get(i);
                User patron = getUser(userIds.get(i)).get();

                ItemSerializer s;
                switch (item_type) {
                    case "book":
                        s = new BookSerializer();
                        break;
                    case "journal_issue":
                        s = new JournalIssueSerializer();
                        break;
                    case "av_material":
                        s = new AvMaterialSerializer();
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid item type: "+item_type);
                }
                Item item = (Item)getItem(item_type,
                        item_id,
                        s).get();
                CheckoutRecord c = new CheckoutRecord(patron, item, due_date);
                records.add(c);
            }
            return records;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User addUser(User user) {
        try {
            QueryParameters p = new QueryParameters();
            p.add("login", user.getLogin());
            p.add("password_hash", user.getPasswordHash());
            p.add("name", user.getName());
            p.add("address", user.getAddress());
            p.add("phone_number", user.getPhoneNumber());
            p.add("type", user.getType());
            p.add("subtype", user.getSubtype());
            insert("user_card", p);
            ResultSet rs = select("user_card",
                    new QueryParameters().add("login",
                            user.getLogin()));
            rs.next();
            user.setCardNumber(rs.getInt("user_id"));
            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Book addBook(BookFactory book) {
        try {
            insert("book",
                    new BookSerializer().toQueryParameters(
                            book.getCarcass()));
            ResultSet rs = select("book",
                    new QueryParameters().add("title",
                            book.getCarcass().getTitle()));
            rs.next();
            return book.build(rs.getInt("book_id"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JournalArticle addJournalArticle(JournalArticleFactory article) {
        try {
            int journalId = article.getCarcass().getJournal().getId();
            QueryParameters data =
                    new JournalArticleSerializer()
                            .toQueryParameters(article.getCarcass());
            data.add("journal_id", journalId);
            insert("article", data);
            int id = findArticles(data).get(0).getId();
            return article.build(id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JournalIssue addJournal(JournalIssueFactory journal) {
        try {
            insert("journal_issue",
                    new JournalIssueSerializer().toQueryParameters(
                            (JournalIssue)journal.getCarcass()));
            ResultSet rs = select("journal_issue",
                    new QueryParameters().add("title",
                            journal.getCarcass().getTitle()));
            rs.next();
            return journal.build(rs.getInt("journal_issue_id"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AvMaterial addAvMaterial(AvMaterialFactory material) {
        try {
            insert("av_material",
                    new AvMaterialSerializer().toQueryParameters(material.getCarcass()));
            ResultSet rs = select("av_material",
                    new QueryParameters().add("title",
                            material.getCarcass().getTitle()));
            rs.next();
            return material.build(rs.getInt("av_material_id"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addCheckoutRecord(CheckoutRecord record) {
        QueryParameters data = new QueryParameters();

        String itemType = record.item.getType();

        try {
            ResultSet rs = select(itemType,
                    new QueryParameters().add("title",
                            record.item.getTitle()));
            rs.next();
            data.add("item_id", rs.getInt(itemType+"_id"));
            data.add("item_type", itemType);

            ResultSet userRs = select("user_card",
                    new QueryParameters().add("login",
                            record.patron.getLogin()));

            userRs.next();
            data.add("user_id", userRs.getInt("user_id"));
            data.add("due_date", record.dueDate);

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
        QueryParameters parameters = new QueryParameters().add("book_id", book_id);
        try{
            deleteOne("checkout", parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeJournalArticle(int article_id) {
        QueryParameters parameters = new QueryParameters().add("journal_article_id", article_id);
        try{
            deleteOne("checkout", parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeJournal(int journal_id) {
        QueryParameters parameters = new QueryParameters().add("journal_issue_id", journal_id);
        try{
            deleteOne("checkout", parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAvMaterial(int material_id) {
        QueryParameters parameters = new QueryParameters().add("av_material_id", material_id);
        try{
            deleteOne("checkout", parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void removeCheckoutRecord(CheckoutRecord c) {
        QueryParameters parameters = new QueryParameters()
        .add("item_id", c.item.getId())
        .add("user_id", c.patron.getCardNumber())
        .add("item_type", c.item.getType())
        .add("due_date", c.dueDate);
        try{
            deleteOne("checkout", parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void updateUser(int user_id, User data) {
        QueryParameters p = new QueryParameters();
        p.add("name", data.getName());
        p.add("phone_number", data.getPhoneNumber());
        p.add("address", data.getAddress());
        p.add("login", data.getLogin());
        p.add("type", data.getType());
        p.add("subtype", data.getSubtype());
        p.add("password_hash", data.getPasswordHash());
        try {
            update("user_card", p);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updateBook(int book_id, Book book) {
        QueryParameters p = new BookSerializer().toQueryParameters(book);
        p.remove("book_id");
        try {
            update("book", p);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updateJournalArticle(int article_id, JournalArticle article) {
        QueryParameters p = new JournalArticleSerializer().toQueryParameters(article);
        p.remove("article_id");
        try {
            update("article", p);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateJournal(int journal_id, JournalIssue journal) {
        QueryParameters p = new JournalIssueSerializer().toQueryParameters(journal);
        p.remove("journal_issue_id");
        try {
            update("journal_issue", p);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateAvMaterial(int material_id, AvMaterial material) {
        QueryParameters p = new AvMaterialSerializer().toQueryParameters(material);
        p.remove("av_material_id");
        try {
            update("av_material", p);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        super.closeConnection();
    }

    private<T extends Item> Optional<T>  getItem(String table,
                                                 int id,
                                                 ItemSerializer<T> serializer) {
        List<T> items = findItems(table, new QueryParameters()
                .add(table+"_id", id), serializer);
        if(!items.isEmpty()) {
            return Optional.of(items.get(0));
        } else {
            return Optional.empty();
        }
    }

    private <T extends Item> List<T> findItems(String table,
                                               QueryParameters searchParameters,
                                               ItemSerializer<T> serializer) {
        List<T> result = new LinkedList<>();
        try {
            ResultSet rs = select(table, searchParameters);

            while(rs.next()) {
                result.add(serializer.fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static SqlStorage instance;
}
