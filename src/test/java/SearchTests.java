import org.controller.*;
import org.items.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.storage.ItemSerializer;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.BookEntry;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchTests {

    @BeforeAll
    void init() {
        LibraryStorage.connect("library", "librarian", "tabula_rasa");
        storage = LibraryStorage.getInstance();
        manager = new LibraryManager(storage);
        admin = storage.find(Resource.User, new QueryParameters().add("type", "Admin")).get(0);
        this.l1 = storage.get(Resource.User, 4049).orElse(null);
        this.l2 = storage.get(Resource.User, 4050).orElse(null);
        this.l3 = storage.get(Resource.User, 4051).orElse(null);

    }

    @Test
    void test1() {
        User u = new User(5436575, "Test", "Admin", null);
        assert !manager.execute(new AddUserCommand(u, admin.getUser())).successful();
    }

    @Test
    void test2() {
        User l1 = new User(4049, "Librarian One", "Librarian", null);
        l1.setLogin("l1");
        l1.setPassword("l1");
        l1.setAddress("...");
        l1.setPhoneNumber("+123131");
        manager.execute(new AddUserCommand(l1, admin.getUser())).validate();
        this.l1 = storage.get(Resource.User, l1.getCardNumber()).get();

        User l2 = new User(4050, "Librarian Two", "Librarian", null);
        l2.setLogin("l2");
        l2.setPassword("l2");
        l2.setAddress("...");
        l2.setPhoneNumber("+123131");
        l2.setPrivilege(User.Privilege.Addition, true);
        manager.execute(new AddUserCommand(l2, admin.getUser())).validate();
        this.l2 = storage.get(Resource.User, l2.getCardNumber()).get();

        User l3 = new User(4051, "Librarian Three", "Librarian", null);
        l3.setLogin("l3");
        l3.setPassword("l3");
        l3.setAddress("...");
        l3.setPhoneNumber("+123131");
        l3.setPrivilege(User.Privilege.Addition, true);
        manager.execute(new AddUserCommand(l3, admin.getUser())).validate();
        this.l3 = storage.get(Resource.User, l3.getCardNumber()).get();

    }

    @Test
    void test3() {
        test2();
        assert !manager.execute(new AddItemCommand(items.d1, l1.getUser())).successful();
        assert !manager.execute(new AddItemCommand(items.d2, l1.getUser())).successful();
        assert !manager.execute(new AddItemCommand(items.d3, l1.getUser())).successful();
    }

    @Test
    void test4() {
        test2();
        items.d1.setCopiesNum(3);
        manager.execute(new AddItemCommand(items.d1, l2.getUser())).validate();
        items.d2.setCopiesNum(3);
        manager.execute(new AddItemCommand(items.d2, l2.getUser())).validate();
        items.d3.setCopiesNum(3);
        manager.execute(new AddItemCommand(items.d3, l2.getUser())).validate();

        manager.execute(new AddUserCommand(items.s, l2.getUser())).validate();
        manager.execute(new AddUserCommand(items.v, l2.getUser())).validate();
        manager.execute(new AddUserCommand(items.p1, l2.getUser())).validate();
        manager.execute(new AddUserCommand(items.p2, l2.getUser())).validate();
        manager.execute(new AddUserCommand(items.p3, l2.getUser())).validate();
    }

    @Test
    void test5() {
        test4();
        storage.updateAll(Resource.Book, ItemSerializer.serialize(items.d1), new QueryParameters().add("copy_num", 2));
        items.d1.setCopiesNum(2);
        assert storage.find(Resource.Book, new QueryParameters().add("title", items.d1.getTitle())).get(0).getItem().getCopiesNum() == 2;
    }

    @Test
    void test6() {
        test4();
        ItemEntry d3 = storage.find(Resource.Book, ItemSerializer.serialize(items.d3).subset("title")).get(0);
        UserEntry p1 = storage.get(Resource.User, items.p1.getCardNumber()).get();
        manager.execute(new CheckOutCommand(p1, d3)).validate();
        UserEntry p2 = storage.get(Resource.User, items.p2.getCardNumber()).get();
        manager.execute(new CheckOutCommand(p2, d3)).validate();
        UserEntry s = storage.get(Resource.User, items.s.getCardNumber()).get();
        manager.execute(new CheckOutCommand(s, d3)).validate();
        UserEntry v = storage.get(Resource.User, items.v.getCardNumber()).get();
        manager.execute(new CheckOutCommand(v, d3)).validate();
        UserEntry p3 = storage.get(Resource.User, items.p3.getCardNumber()).get();
        manager.execute(new CheckOutCommand(p3, d3)).validate();
        assert !manager.execute(new OutstandingRequestCommand(l1, d3)).successful();
    }

    @Test
    void test7() {
        test4();
        ItemEntry d3 = storage.find(Resource.Book, ItemSerializer.serialize(items.d3).subset("title")).get(0);
        UserEntry p1 = storage.get(Resource.User, items.p1.getCardNumber()).get();
        manager.execute(new CheckOutCommand(p1, d3)).validate();
        UserEntry p2 = storage.get(Resource.User, items.p2.getCardNumber()).get();
        manager.execute(new CheckOutCommand(p2, d3)).validate();
        UserEntry s = storage.get(Resource.User, items.s.getCardNumber()).get();
        manager.execute(new CheckOutCommand(s, d3)).validate();
        UserEntry v = storage.get(Resource.User, items.v.getCardNumber()).get();
        manager.execute(new CheckOutCommand(v, d3)).validate();
        UserEntry p3 = storage.get(Resource.User, items.p3.getCardNumber()).get();
        manager.execute(new CheckOutCommand(p3, d3)).validate();
        manager.execute(new OutstandingRequestCommand(l3, d3)).validate();
        assert storage.getNumOfEntries(Resource.PendingRequest, new QueryParameters().add("item_id", d3.getId())) == 1;
    }

    @Test
    void test8() {
        storage.removeAll(Resource.ActionLog, new QueryParameters());
        test6();
    }

    @Test
    void test9() {
        storage.removeAll(Resource.ActionLog, new QueryParameters());
        test7();
    }

    @Test
    void test10() {
        test4();
        List<BookEntry> b = storage.searchFor(Resource.Book, new QueryParameters().add("title", ".*to Algo.*"));
        assert b.size() == 1 : b.toString();
    }


    @Test
    void test11() {
        test4();
        List<BookEntry> b = storage.searchFor(Resource.Book, new QueryParameters().add("title", ".*Algo.*"));
        assert b.size() == 2 : b.toString();
    }


    @Test
    void test12() {
        test4();
        List<BookEntry> b = storage.searchFor(Resource.Book, new QueryParameters().add("keywords", Arrays.asList("Algorithms")));
        assert b.size() == 3 : b.toString();
    }

    @Test
    void test13() {
        test4();
        List<BookEntry> b = storage.searchFor(Resource.Book, new QueryParameters().add("title", "(Algorithms and Programming)"));
        assert b.size() == 0 : b.toString();
    }


    @Test
    void test14() {
        test4();
        List<BookEntry> b = storage.searchFor(Resource.Book, new QueryParameters().add("title", "(Algorithms|Programming)"));
        assert b.size() == 3 : b.toString();
    }

    @AfterEach
    void cleanUp() {
        try {
            UserEntry p1 = storage.get(Resource.User, items.p1.getCardNumber()).get();
            manager.execute(new RemoveUserCommand(admin, p1)).validate();
            UserEntry p2 = storage.get(Resource.User, items.p2.getCardNumber()).get();
            manager.execute(new RemoveUserCommand(admin, p2)).validate();
            UserEntry p3 = storage.get(Resource.User, items.p3.getCardNumber()).get();
            manager.execute(new RemoveUserCommand(admin, p3)).validate();
            UserEntry s = storage.get(Resource.User, items.s.getCardNumber()).get();
            manager.execute(new RemoveUserCommand(admin, s)).validate();
            UserEntry v = storage.get(Resource.User, items.v.getCardNumber()).get();
            manager.execute(new RemoveUserCommand(admin, v)).validate();
            storage.removeAll(Resource.Book, ItemSerializer.serialize(items.d3));
            storage.removeAll(Resource.Book, ItemSerializer.serialize(items.d1));
            storage.removeAll(Resource.Book, ItemSerializer.serialize(items.d2));
        } catch (NoSuchElementException e) {
            ;
        }

        if(l1 != null) {
            manager.execute(new RemoveUserCommand(admin, l1)).validate();
            manager.execute(new RemoveUserCommand(admin, l2)).validate();
            manager.execute(new RemoveUserCommand(admin, l3)).validate();
        }
    }


    TestItems items = new TestItems();
    UserEntry admin;
    UserEntry l1, l2, l3;
    LibraryManager manager;
    LibraryStorage storage;
}
