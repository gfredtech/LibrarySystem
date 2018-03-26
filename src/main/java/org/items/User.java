package org.items;


/**
 * Data structure representing a user record in the system
 */

public class User {

    public User(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public User(int cardNumber, String name, String type, String subtype) {
        this.cardNumber = cardNumber;
        this.type = type;
        this.subtype = subtype;
        this.name = name;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
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

}