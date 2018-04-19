import org.items.*;
import org.storage.ItemSerializer;
import org.storage.QueryParameters;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


class TestItems {

    Map<String, QueryParameters> users = new HashMap<>();
    Map<String, QueryParameters> books = new HashMap<>();
    Map<String, QueryParameters> av = new HashMap<>();
    Map<String, QueryParameters> articles = new HashMap<>();

    User alice, p1, p2, p3, v, s;
    Book b1, b2, b3;
    AvMaterial av1, av2;

    TestItems() {
        b1 = new BookFactory()
                .authors(Arrays.asList("Thomas H. Cormen", "Charles E. Leiserson",
                        "Ronald L. Rivest", "Clifford Stein"))
                .publisher("MIT Press")
                .publicationDate(LocalDate.of(2009, 1, 1))
                .title("Introduction to Algorithms, Third edition")
                .copiesNum(3)
                .price(5000)
                .keywords(Collections.emptyList()).build();
        books.put("cormen", ItemSerializer.serialize(b1));

        b2 = new BookFactory()
                .title("Design Patterns: Elements of Reusable Object-Oriented Software, First edition")
                .authors(Arrays.asList("Erich Gamma", "Ralph Johnson", "John Vlissides", "Richard Helm"))
                .publisher("Addison-Wesley Professional")
                .publicationDate(LocalDate.of(2003, 1, 1))
                .isBestseller()
                .price(1700)
                .copiesNum(3)
                .keywords(Collections.emptyList()).build();
        books.put("patterns", ItemSerializer.serialize(b2));

        b3 = new BookFactory()
                .title("The Mythical Man-month, Second edition")
                .authors(Arrays.asList("Brooks", "Jr.", "Frederick P."))
                .publisher("Addison-Wesley Longman Publishing Co., Inc.")
                .publicationDate(LocalDate.of(1995, 1, 1))
                .isReference()
                .price(0)
                .copiesNum(2)
                .keywords(Collections.emptyList()).build();
        books.put("brooks", ItemSerializer.serialize(b3));

        av1 = new AvMaterialFactory()
                .title("Null References: The Billion Dollar Mistake")
                .authors(Collections.singletonList("Tony Hoare"))
                .price(700)
                .copiesNum(2)
                .keywords(Collections.emptyList()).build();
        av.put("null", ItemSerializer.serialize(av1));

        av2 = new AvMaterialFactory()
                .title("Information Entropy")
                .authors(Arrays.asList("Claude Shannon"))
                .price(0)
                .copiesNum(1)
                .keywords(Collections.emptyList()).build();
        av.put("entropy", ItemSerializer.serialize(av2));

        p1 = new User(
                1010, "Sergey Afonso", "Faculty", "Professor");
        p1.setPhoneNumber("30001");
        p1.setAddress("Via Margutta, 3");
        p1.setLogin("s.afonso");
        users.put("sergey", ItemSerializer.serialize(p1));

        p2 = new User(
                1011, "Nadia Teixeira", "Faculty", "Professor");
        p2.setPhoneNumber("30002");
        p2.setAddress("Via Sacra, 13");
        p2.setLogin("n.teixeira");
        users.put("nadia", ItemSerializer.serialize(p2));

        p3 = new User(
                1100, "Elvira Espindola", "Faculty", "Professor");
        p3.setPhoneNumber("30003");
        p3.setAddress("Via del Corso, 22");
        p3.setLogin("e.espindola");
        users.put("elvira",ItemSerializer.serialize(p3));

        alice = new User(
                2017, "Alice", "Librarian", null);
        alice.setPhoneNumber("...");
        alice.setAddress("...");
        alice.setLogin("alice");
        alice.setPrivilege(User.Privilege.Addition, true);

        alice.setPrivilege(User.Privilege.Addition, true);
        alice.setPrivilege(User.Privilege.Deletion, true);
        alice.setPrivilege(User.Privilege.Modification, true);
        users.put("alice", ItemSerializer.serialize(alice));

        s = new User(
                1101, "Andrey Velo", "Student", null);
        s.setPhoneNumber("30004");
        s.setAddress("Avenida Mazatlan 250");
        s.setLogin("a.velo");
        users.put("andrey", ItemSerializer.serialize(s));

        v = new User(
                1110, "Veronika Rama", "Visiting", null);
        v.setPhoneNumber("30005");
        v.setAddress("Stret Atocha, 27");
        v.setLogin("v.rama");
        users.put("rama", ItemSerializer.serialize(v));
    }

}
