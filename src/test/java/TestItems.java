import org.items.User;
import org.storage.EntrySerializer;
import org.storage.QueryParameters;

import java.time.LocalDate;
import java.util.*;


public class TestItems {

    public Map<String, QueryParameters> users = new HashMap();
    public Map<String, QueryParameters> books = new HashMap();
    public Map<String, QueryParameters> av = new HashMap();
    public Map<String, QueryParameters> articles = new HashMap();

    public TestItems() {
        QueryParameters b1 = new QueryParameters()
                .add("title", "Introduction to Algorithms, Third edition")
                .add("authors", Arrays.asList("Thomas H. Cormen", "Charles E. Leiserson",
                        "Ronald L. Rivest", "Clifford Stein"))
                .add("publisher", "MIT Press")
                .add("publication_date", LocalDate.of(2009, 1, 1))
                .add("copy_num", 3)
                .add("price", 0)
                .add("keywords", Collections.emptyList());
        books.put("cormen", b1);

        QueryParameters b2 = new QueryParameters()
                .add("title", "Design Patterns: Elements of Reusable Object-Oriented Software, First edition")
                .add("authors", Arrays.asList("Erich Gamma", "Ralph Johnson", "John Vlissides", "Richard Helm"))
                .add("publisher", "Addison-Wesley Professional")
                .add("publication_date", LocalDate.of(2003, 1, 1))
                .add("is_bestseller", true)
                .add("price", 0)
                .add("copy_num", 2)
                .add("keywords", Collections.emptyList());
        books.put("patterns", b2);


        QueryParameters b3 = new QueryParameters()
                .add("title", "The Mythical Man-month, Second edition")
                .add("authors", Arrays.asList("Brooks", "Jr.", "Frederick P."))
                .add("publisher", "Addison-Wesley Longman Publishing Co., Inc.")
                .add("publication_date", LocalDate.of(1995, 1, 1))
                .add("is_reference", true)
                .add("price", 0)
                .add("copy_num", 1)
                .add("keywords", Collections.emptyList());
        books.put("brooks", b3);
        QueryParameters av1 = new QueryParameters()
                .add("title", "Null References: The Billion Dollar Mistake")
                .add("authors", Collections.singletonList("Tony Hoare"))
                .add("price", 0)
                .add("copy_num", 1)
                .add("keywords", Collections.emptyList());
        av.put("null", av1);
        QueryParameters av2 = new QueryParameters()
                .add("title", "Information Entropy")
                .add("authors", Arrays.asList("Claude Shannon"))
                .add("price", 0)
                .add("copy_num", 1)
                .add("keywords", Collections.emptyList());
        av.put("entropy", av2);

        User p1 = new User(
                1010, "Sergey Afonso", "Faculty", "Professor");
        p1.setPhoneNumber("30001");
        p1.setAddress("Via Margutta, 3");
        p1.setLogin("s.afonso");
        users.put("sergey", EntrySerializer.serialize(p1));

        User p2 = new User(
                1011, "Nadia Teixeira", "Student", null);
        p2.setPhoneNumber("30002");
        p2.setAddress("Via Sacra, 13");
        p2.setLogin("n.teixeira");
        users.put("nadia", EntrySerializer.serialize(p2));

        User p3 = new User(
                1100, "Elvira Espindola", "Student", null);
        p3.setPhoneNumber("30003");
        p3.setAddress("Via del Corso, 22");
        p3.setLogin("e.espindola");
        users.put("elvira", EntrySerializer.serialize(p3));
    }

}
