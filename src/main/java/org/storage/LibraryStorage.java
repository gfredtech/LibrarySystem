package org.storage;

import javafx.util.Pair;
import org.storage.resources.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This class is a wrapper for the Storage class. It contains some auxiliary methods
 * which manage the database according to the library needs.
 */
public class LibraryStorage extends SqlStorage {

    /**
     * @param c contains data of the overdue check out
     * @return the fee, which is 100 rubles per day. Zero if not overdue
     */
    public int caluclateFee(CheckoutEntry c) {
        final int feePerDay = 100;
        if(LocalDate.now().isAfter(c.getDueDate())) {
            final int dateDifference = c.getDueDate().until(LocalDate.now()).getDays();
            final int calculated = dateDifference * feePerDay;
            return Math.min(calculated, c.getItem().getItem().getPrice());

        } else return 0;
    }

    public List<UserEntry> getQueueFor(ItemEntry item) {
        List<PendingRequestEntry> requests = find(Resource.PendingRequest,
                new QueryParameters().add("item_id", item.getId()));
        List<UserEntry> patrons =
                requests.stream().map(PendingRequestEntry::getUser)
                        .collect(Collectors.toCollection(LinkedList::new));
        sortAwaitingPatronsList(patrons);
        return patrons;
    }



    public static LibraryStorage getInstance() {
        if(instance == null)
            throw new RuntimeException("SQL Storage has not been initialized");
        return instance;
    }

    public static void connect(String databaseName, String userName, String userPassword) {
        try {
            SqlStorage.connect(databaseName, userName, userPassword);
            instance = new LibraryStorage(databaseName, userName, userPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected LibraryStorage(String databaseName, String userName, String userPassword) throws  SQLException {
        super(databaseName, userName, userPassword);
    }

    private void sortAwaitingPatronsList(List<UserEntry> patrons) {
        HashMap<Pair<String, String>, Integer> priorities = new HashMap<>();
        priorities.put(new Pair<>("Student", null), 1);
        priorities.put(new Pair<>("Faculty", "Instructor"), 2);
        priorities.put(new Pair<>("Faculty", "TA"), 3);
        priorities.put(new Pair<>("Visiting", null), 4);
        priorities.put(new Pair<>("Faculty", "Professor"), 5);
        patrons.sort((o1, o2) -> {
            Pair<String, String> o1type =
                    new Pair<>(o1.getUser().getType(), o1.getUser().getSubtype());
            Pair<String, String> o2type =
                    new Pair<>(o2.getUser().getType(), o2.getUser().getSubtype());
            return priorities.get(o1type) - priorities.get(o2type);
        });
    }

    private static LibraryStorage instance;
}
