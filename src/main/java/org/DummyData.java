package org;

import org.resources.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * THIS FILE WILL BE REMOVED. Solely for testing purposes.
 */

public class DummyData {



   public ArrayList<Book> createBooks() {

    // Introduction to algorithms
    BookFactory factory = new BookFactory();
    factory.setTitle("Introduction to Algorithms");
    factory.setPublicationDate(LocalDate.of(2009, 9, 1));
    factory.setAuthors(new ArrayList<String>() {{
        add("Thomas H. Cormen");
        add("Charles E. Leiserson");
        add("Ronald L. Rivest");
        add("Clifford Stein");
    }});
    factory.setAsBestseller();
    factory.setPublisher("MIT Press");
    factory.setKeywords(new ArrayList<String>() {{
        add("algorithms");
        add("complexity");
        add("data structures");
        add("computer science");
    }});
    factory.setAsNonReference();
    factory.setPrice(1500);
    factory.setCopiesNum(5);
    Book clrs =  factory.build(1);

    // Structure and Interpretation of Computer Programs
    factory = new BookFactory();
    factory.setTitle("Structure and Interpretation of Computer Programs");
    factory.setCopiesNum(2);
    factory.setAuthors(new ArrayList<String>(){{
        add("Harold Abelson");
        add("Gerald Sussman");
        add("Julie Sussman");
    }});
    factory.setAsBestseller();
    factory.setPublicationDate(LocalDate.of(1996, 5, 1));
    factory.setPrice(1000);
    factory.setPublisher("MIT Press");
    factory.setAsNonReference();
    factory.setKeywords(new ArrayList<String>() {{
        add("lisp");
        add("scheme");
        add("programming");
        add("intelligent systems");
        add("computer science");
    }});
    Book sicp = factory.build(2);


    // Deep Learning with Python
    factory = new BookFactory();
    factory.setTitle("Deep Learning with Python");
    factory.setPublisher("Manning Publications");
    factory.setKeywords(new ArrayList<String>() {{
        add("python");
        add("deep learning");
        add("artificial intelligence");
        add("machine learning");
        add("neural networks");
        add("computer vision");
    }});
    factory.setAsNonReference();
    factory.setPrice(2000);
    factory.setPublicationDate(LocalDate.of(2017, 12, 5));
    factory.setAsBestseller();
    factory.setAuthors(new ArrayList<String>(){{
        add("Francois Chollet");
    }});
    factory.setCopiesNum(2);
    Book dlwp = factory.build(3);

    //The C Programming Language, 2nd Edition
       factory = new BookFactory();
       factory.setTitle("The C Programming Language, 2nd Edition");
       factory.setCopiesNum(20);
       factory.setAuthors(new ArrayList<String>() {{
           add("Brian Kernighan");
           add("Dennis Ritchie");
       }});
       factory.setKeywords(new ArrayList<String>() {{
           add("C");
           add("programming");
           add("systems");
           add("ansi c");
       }});
       factory.setAsBestseller();
       factory.setPublicationDate(LocalDate.of(1988, 12, 1));
       factory.setPrice(100);
       factory.setPublisher("Prentice Hall Inc.");
       Book cbook = factory.build(4);




return new ArrayList<Book>(){{
    add(clrs);
    add(sicp);
    add(dlwp);
    add(cbook);
}};

}

    public ArrayList<User> createUsers() {
       User user1 = new User(1000, "Robert Chavez", "Patron", "Student");
       user1.setLogin("robertron");
       User user2 = new User(1001, "Godfred Asamoah", "Patron", "Student");
       user2.setLogin("gfred");
       User user3 = new User(1002, "Vladimir Scherba", "Patron", "Faculty");
       user3.setLogin("harrm");
       User user4 = new User(1003, "Prof. Carvalho", "Patron", "Faculty");
       user4.setLogin("bitconnect");
       User user5 = new User(1004, "Mr. Max", "Librarian", "Librarian");
       user5.setLogin("root");

       return new ArrayList<User>() {{
           add(user1);
           add(user2);
           add(user3);
           add(user4);
           add(user5);
       }};
       }

    public ArrayList<AvMaterial> createAVMaterial() {
        AvMaterialFactory factory = new AvMaterialFactory();
        factory.setTitle("Linear Algebra Fall 2009");
        factory.setCopiesNum(5);
        factory.setKeywords(new ArrayList<String>() {{
            add("mathematics");
            add("linear algebra");
            add("algebra");
            add("vectors");
        }});
        factory.setPrice(400);
        factory.setAsNonReference();
        AvMaterial linearAlgebra = factory.build(5);
        System.out.println("AV material " + linearAlgebra.getTitle());

        factory = new AvMaterialFactory();
        factory.setTitle("Calculus");

        factory.setKeywords(new ArrayList<String>() {{
            add("calculus audio lessons");
            add("mathematics");
            add("analysis");
            add("vectors");
        }});
        factory.setPrice(400);
        factory.setAsNonReference();
        factory.setCopiesNum(5);
        AvMaterial calculus = factory.build(6);

        System.out.println("AV material " + calculus.getTitle());

        return new ArrayList<AvMaterial>() {{
            add(calculus);
            add(linearAlgebra);
        }};
    }

    public ArrayList<JournalIssue> createJournalIssue(){
        JournalIssueFactory factory = new JournalIssueFactory();
        factory.setEditors(new ArrayList<String>(){{
            add("Carlos de Mesa");
            add("Luis Quintana");
        }});
        factory.setPublicationDate(LocalDate.of(2018, 3, 4));
        factory.setPublisher("La Hoguera");
        factory.setAsNonReference();
        factory.setCopiesNum(20);
        factory.setTitle("Scientific Relations");
        factory.setPrice(200);
        factory.setKeywords(new ArrayList<String>(){{
            add("Science");
            add("Relations");
            add("Sientific");
        }});
        JournalIssue scientificRelations = factory.build(111);


        factory = new JournalIssueFactory();
        factory.setEditors(new ArrayList<String>(){{
            add("Carlos de Mesa");
            add("Luis Quintana");
        }});
        factory.setPublicationDate(LocalDate.of(2018, 3, 3));
        factory.setPublisher("La Hoguera");
        factory.setAsNonReference();
        factory.setCopiesNum(25);
        factory.setTitle("World of Coding");
        factory.setPrice(250);
        factory.setKeywords(new ArrayList<String>(){{
            add("programming");
            add("world");
            add("coding");
        }});
        JournalIssue worldOfCoding = factory.build(222);

        return new ArrayList<JournalIssue>(){{
            add(scientificRelations);
            add(worldOfCoding);
        }};

    }

    public ArrayList<JournalArticle> createJournalArticle(){

        ArrayList<JournalIssue> journalIssues = createJournalIssue();

        JournalArticleFactory factory = new JournalArticleFactory();
        factory.setTitle("Science in History");
        factory.setKeywords(new ArrayList<String>(){{
            add("History");
            add("Science");
            add("Scientific");
        }});
        //factory.setPrice(300);
        //factory.setAsNonReference();
        //factory.setCopiesNum(20);
        factory.setAuthors(new ArrayList<String>(){{
            add("Carlos Quintana");
            add("Jose Perales");
        }});

        factory.setJournalIssue(journalIssues.get(1));
        JournalArticle scienceInHistory = factory.build(1112);



        factory = new JournalArticleFactory();
        factory.setTitle("The discovery of coding");
        factory.setKeywords(new ArrayList<String>(){{
            add("discovery");
            add("coding");
            add("timeline of programming");
        }});
        //factory.setPrice(300);
        //factory.setAsNonReference();
        //factory.setCopiesNum(20);

        factory.setAuthors(new ArrayList<String>(){{
            add("Carlos Quintana");
            add("Jose Luis Morales");
        }});

        factory.setJournalIssue(journalIssues.get(0));

        JournalArticle discoveryOfCoding = factory.build(1113);


        return new ArrayList<JournalArticle>(){{
            add(scienceInHistory);
            add(discoveryOfCoding);
        }};
    }



}
