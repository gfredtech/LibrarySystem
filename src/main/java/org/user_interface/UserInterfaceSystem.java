package org.user_interface;


import org.resources.BookFactory;
import org.resources.CheckoutInfo;
import org.resources.User;
import org.storage.Storage;
import org.user_interface.command.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;



/**
 * Represents an interface between a user and the library system
 */
public class UserInterfaceSystem {

    /**
     * Initializes an instance of the interface system
     * @param args command line arguments passed to an executable
     */
    public UserInterfaceSystem(String[] args) {
        if(args.length != 2) {
            throw new IllegalArgumentException("The program takes exactly two arguments: the name and the password of a database owner");
        }

        initializeStorage(args[0], args[1]);
    }

    /**
     * Start processing user commands from the standard input and
     * send responses to the standard output
     * Note: it is a blocking method that will stop execution only upon an exit command from the user
     */
    public void start() {
        reader = new Scanner(System.in);
        Command c;
        do {
            c = parseInput();
            System.out.println(c.run());
        } while(!(c instanceof ExitCommand));
        reader.close();
    }

    /**
     * Parses a line from stdin, producing an initialized command
     * @return command corresponding to the user request
     */
    private Command parseInput() {
        Command c;
        switch(reader.next()) {
            case "exit":
                c = new ExitCommand(storage);
                break;

            case "find":
                List<String> args = new LinkedList<>();

                c = new SearchCommand(storage, Arrays.asList(reader.nextLine().split(" ")));
                break;

            case "checkout":
                try {
                    int cardNumber = reader.nextInt();
                    String bookName = reader.nextLine().trim();
                    System.out.println("Attempt to checkout '"+bookName+"'");
                    c = new CheckoutCommand(storage, cardNumber, bookName);
                } catch(InputMismatchException e) {
                    c = new MessageCommand("The first argument should be an integer!");
                }
                break;

            case "show":
                c = new ShowCommand(storage, reader.nextLine().trim());
                break;

            default:
                c = new MessageCommand("Invalid command.");
        }
        return c;
    }



    /**
     * TODO: Establishes connection to a database
     * ! Temporary solution:
     * Reads users, books and checkouts from text files in the project root
     */
    private void initializeStorage(String userName, String userPassword) {
        storage = new Storage("library", userName, userPassword);
        try(Scanner s = new Scanner(new File("users"))) {
            while (s.hasNext()) {
                int cardNumber = s.nextInt();
                s.nextLine();
                String name = s.nextLine();
                String type = s.nextLine();
                String subtype = null;
                String[] l = type.split(" ");
                if (l.length > 1) {
                    type = l[0];
                    subtype = l[1];
                }
                String phone = s.nextLine();
                String address = s.nextLine();
                User u = new User(cardNumber, name, type, subtype);
                u.setPhoneNumber(phone);
                u.setAddress(address);
                storage.users.add(u);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try(Scanner s = new Scanner(new File("books"))) {
            while (s.hasNext()) {
                String title = s.nextLine();
                List<String> authors = Arrays.asList(s.nextLine().split(", "));
                String publisher = s.nextLine();
                int year = s.nextInt();
                s.nextLine();
                List<String> keywords = Arrays.asList(s.nextLine().split(", "));
                int copies = s.nextInt();
                boolean bestseller = false;
                boolean reference = false;
                s.nextLine();
                String nextToken = s.nextLine();
                if(!nextToken.isEmpty()) {
                    if(nextToken.equals("bestseller")) {
                        bestseller = true;
                    } else if(nextToken.equals("reference")) {
                        reference = true;
                    }
                    s.nextLine();
                }

                BookFactory f = new BookFactory();
                f.setTitle(title);
                f.setAuthors(authors);
                f.setKeywords(keywords);
                f.setCopiesNum(copies);
                f.setPublisher(publisher);
                f.setPublicationYear(LocalDate.ofYearDay(year, 1));
                if(reference) {
                    f.setAsReference();
                }
                if(bestseller) {
                    f.setAsBestseller();
                }
                storage.books.add(f.build());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try(Scanner s = new Scanner(new File("checkouts"))) {
            while (s.hasNext()) {
                String title = s.nextLine();
                int userCard = s.nextInt();
                s.nextLine();
                LocalDate overdue = LocalDate.parse(s.nextLine());
                s.nextLine();

                CheckoutInfo c = new CheckoutInfo();
                c.patron = storage.users.stream().filter(u -> u.getCardNumber() == userCard).findFirst().get();
                c.item = storage.books.stream().filter(b -> b.getTitle().equals(title)).findFirst().get();
                c.overdue = overdue;
                storage.checkouts.add(c);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Storage storage;
    private Scanner reader;
}
