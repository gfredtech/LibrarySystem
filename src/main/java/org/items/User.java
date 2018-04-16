package org.items;


import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Data structure representing a user record in the system
 */

public class User {

    public enum Privilege {Modification, Addition, Deletion}

    public User(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public User(int cardNumber, String name, String type, String subtype) {
        this.cardNumber = cardNumber;
        setType(type);
        setSubtype(subtype);
        this.name = name;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<String> getTypes() {
        return Arrays.asList("Admin", "Librarian", "Student", "Faculty", "Visiting");
    }

    public void setType(String type) {
        if (getTypes().contains(type)) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Invalid type: "+type+"; Should be one of "+getTypes());
        }
    }

    public static List<String> getSubypes() {
        return Arrays.asList("Instructor", "TA", "Professor");
    }

    public void setSubtype(String subtype) {
        if (subtype == null || subtype.isEmpty() || getSubypes().contains(subtype)) {
            this.subtype = subtype;
        } else {
            throw new IllegalArgumentException(
                    "Invalid subtype: "+subtype+"; Should be one of "+getSubypes());
        }
    }

    public boolean hasPrivilege(Privilege privilege) {
        return privileges.contains(privilege);
    }

    public void setPrivilege(Privilege privilege, boolean isGranted) {
        if(isGranted) {
            privileges.add(privilege);
        } else {
            privileges.remove(privilege);
        }
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    @Override
    public String toString() {
        return String.format("User{name: %s, id: %s, type: %s}", name, cardNumber, type);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.passwordHash = password.hashCode();
    }

    public void setPasswordHash(int passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getLogin() {
        return login;
    }

    public int getPasswordHash() { return passwordHash; }

    private String name;
    private String login;
    private String type;
    private String subtype;
    private int cardNumber;
    private String phoneNumber;
    private String address;
    private int passwordHash;
    final private Set<Privilege> privileges = new TreeSet<>();
}