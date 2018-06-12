import org.controller.*;
import org.junit.jupiter.api.*;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FineRenewTest {

    @BeforeAll
    void init() {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");
        storage = LibraryStorage.getInstance();
        manager = new LibraryManager(storage);
        clean();
        storage.add(Resource.Book, data.books.get("cormen"));
        storage.add(Resource.Book, data.books.get("patterns"));
        storage.add(Resource.AvMaterial, data.av.get("null"));
        storage.add(Resource.User, data.users.get("nadia"));
        storage.add(Resource.User, data.users.get("sergey"));
        storage.add(Resource.User, data.users.get("andrey"));
        storage.add(Resource.User, data.users.get("rama"));
        storage.add(Resource.User, data.users.get("elvira"));
        storage.add(Resource.User, data.users.get("alice"));

        d1 = storage.find(Resource.Book, data.books.get("cormen")).get(0);
        d2 = storage.find(Resource.Book, data.books.get("patterns")).get(0);
        d3 = storage.find(Resource.AvMaterial, data.av.get("null")).get(0);
        p1 = storage.find(Resource.User, data.users.get("sergey")).get(0);
        p2 = storage.find(Resource.User, data.users.get("nadia")).get(0);
        p3 = storage.find(Resource.User, data.users.get("elvira")).get(0);
        s = storage.find(Resource.User, data.users.get("andrey")).get(0);
        v = storage.find(Resource.User, data.users.get("rama")).get(0);
        alice = storage.find(Resource.User, new QueryParameters().add("login", "alice")).get(0);
    }

    @Test
    void test1() {
        manager.execute(new CheckOutCommand(p1, d1, LocalDate.now().minusWeeks(4))).validate();
        manager.execute(new CheckOutCommand(p1, d2, LocalDate.now().minusWeeks(4))).validate();

        manager.execute(new ReturnCommand(p1, d2)).validate();

        CheckoutEntry c = storage.find(Resource.Checkout,
                new QueryParameters().add("user_id", p1.getId()).add("item_id", d1.getId())).get(0);
        assert storage.caluclateFee(c) == 0;
    }

    @Test
    void test2() {
        List<Integer> fines = new LinkedList<>();
        for(UserEntry u: Arrays.asList(p1, s, v)) {
            for (ItemEntry i: Arrays.asList(d1, d2)) {
                manager.execute(new CheckOutCommand(u, i, LocalDate.now().minusWeeks(4))).validate();
                CheckoutEntry c = storage.getCheckoutRecord(u, i);
                fines.add(storage.caluclateFee(c));
            }
        }
        assert fines.equals(Arrays.asList(0, 0, 700, 1400, 2100, 1700)) : fines;
    }

    @Test
    void test3() {
        manager.execute(new CheckOutCommand(p1, d1, LocalDate.now().minusDays(4))).validate();
        manager.execute(new CheckOutCommand(s, d2, LocalDate.now().minusDays(4))).validate();
        manager.execute(new CheckOutCommand(v, d2, LocalDate.now().minusDays(4))).validate();

        CheckoutEntry c =  storage.getCheckoutRecord(p1, d1);
        manager.execute(new RenewCommand(c)).validate();
        c =  storage.getCheckoutRecord(p1, d1);
        assert c.getDueDate().equals(LocalDate.now().plusWeeks(4)) : c.getDueDate();

        c = storage.getCheckoutRecord(s, d2);
        manager.execute(new RenewCommand(c)).validate();
        c =  storage.getCheckoutRecord(s, d2);
        assert c.getDueDate().equals(LocalDate.now().plusWeeks(2)) : c.getDueDate();

        c = storage.getCheckoutRecord(v, d2);
        manager.execute(new RenewCommand(c)).validate();
        c =  storage.getCheckoutRecord(v, d2);
        assert c.getDueDate().equals(LocalDate.now().plusWeeks(1)) : c.getDueDate();
    }

    @Test
    void test4() {
        manager.execute(new CheckOutCommand(p1, d1, LocalDate.now().minusDays(4))).validate();
        manager.execute(new CheckOutCommand(s, d2, LocalDate.now().minusDays(4))).validate();
        manager.execute(new CheckOutCommand(v, d2, LocalDate.now().minusDays(4))).validate();

        manager.execute(new OutstandingRequestCommand(alice, d2)).validate();
        manager.execute(new RenewCommand(storage.getCheckoutRecord(p1, d1)));
        manager.execute(new RenewCommand(storage.getCheckoutRecord(s, d2)));
        manager.execute(new RenewCommand(storage.getCheckoutRecord(v, d2)));

        assert storage.getCheckoutRecord(p1, d1).getDueDate().equals(LocalDate.now().plusWeeks(4));
        assert storage.getCheckoutRecord(s, d2).getDueDate().equals(LocalDate.now().plusDays(10));
        assert storage.getCheckoutRecord(v, d2).getDueDate().equals(LocalDate.now().plusDays(3));
    }

    @Test
    void test5() {
        manager.execute(new CheckOutCommand(p1, d3)).validate();
        manager.execute(new CheckOutCommand(s, d3)).validate();
        manager.execute(new CheckOutCommand(v, d3)).validate();
        assert storage.getQueueFor(d3).get(0).getId() == v.getId();
    }

    @Test
    void test6() {
        manager.execute(new CheckOutCommand(p1, d3)).validate();
        manager.execute(new CheckOutCommand(p2, d3)).validate();
        manager.execute(new CheckOutCommand(s, d3)).validate();
        manager.execute(new CheckOutCommand(v, d3)).validate();
        manager.execute(new CheckOutCommand(p3, d3)).validate();
        List<Integer> result = storage.getQueueFor(d3).stream().mapToInt(UserEntry::getId).boxed().collect(Collectors.toList());
        List<Integer> expected = Stream.of(s, v, p3).mapToInt(UserEntry::getId).boxed().collect(Collectors.toList());
        assert result.equals(expected);
    }

    @Test
    void test7() {
        test6();
        manager.execute(new OutstandingRequestCommand(alice, d3)).validate();
        assert storage.getQueueFor(d3).size() == 1;
    }

    @Test
    void test8() {
        test6();
        manager.execute(new ReturnCommand(p2, d3)).validate();
        List<Integer> result = storage.getQueueFor(d3).stream().mapToInt(UserEntry::getId).boxed().collect(Collectors.toList());
        List<Integer> expected = Stream.of(s, v, p3).mapToInt(UserEntry::getId).boxed().collect(Collectors.toList());
        assert result.equals(expected);
    }

    @Test
    void test9() {
        test6();
        manager.execute(new RenewCommand(storage.getCheckoutRecord(p1, d3))).validate();
        assert storage.getCheckoutRecord(p1, d3).getDueDate().equals(LocalDate.now().plusWeeks(2))
                : storage.getCheckoutRecord(p1, d3).getDueDate();
        List<Integer> result = storage.getQueueFor(d3).stream().mapToInt(UserEntry::getId).boxed().collect(Collectors.toList());
        List<Integer> expected = Stream.of(s, v, p3).mapToInt(UserEntry::getId).boxed().collect(Collectors.toList());
        assert result.equals(expected);
    }

    @Test
    void test10() {
        manager.execute(new CheckOutCommand(p1, d1, LocalDate.now().minusWeeks(1))).validate();
        manager.execute(new RenewCommand(storage.getCheckoutRecord(p1, d1), LocalDate.now().minusDays(4))).validate();
        manager.execute(new CheckOutCommand(v, d1, LocalDate.now().minusWeeks(1))).validate();
        manager.execute(new RenewCommand(storage.getCheckoutRecord(v, d1), LocalDate.now().minusDays(4))).validate();

        assert manager.execute(new RenewCommand(storage.getCheckoutRecord(p1, d1))) == Command.Result.Failure;
        manager.execute(new RenewCommand(storage.getCheckoutRecord(v, d1))).validate();

        assert storage.getCheckoutRecord(p1, d1).getDueDate().equals(LocalDate.now().plusDays(24));
        assert storage.getCheckoutRecord(v, d1).getDueDate().equals(LocalDate.now().plusDays(7));
    }

    @AfterEach
    void cleanCheckouts() {
        for (QueryParameters u : data.users.values()) {
            List<UserEntry> es = storage.find(Resource.User, u);
            if (!es.isEmpty()) {
                UserEntry e = es.get(0);
                storage.removeAll(Resource.Checkout, new QueryParameters().add("user_id", e.getId()));
                storage.removeAll(Resource.PendingRequest, new QueryParameters().add("user_id", e.getId()));
            }
        }
    }

    @AfterAll
    void clean() {
        storage.removeAll(Resource.Book, data.books.get("cormen"));
        storage.removeAll(Resource.Book, data.books.get("patterns"));
        storage.removeAll(Resource.AvMaterial, data.av.get("null"));
        for (QueryParameters u: data.users.values()) {
            List<UserEntry> es = storage.find(Resource.User, u);
            if(!es.isEmpty()) {
                storage.removeAll(Resource.User, u);
            }
        }
    }

    private TestItems data = new TestItems();
    private LibraryStorage storage;
    private LibraryManager manager;
    private UserEntry p1, p2, p3, s, v, alice;
    private ItemEntry d1, d2, d3;
}
