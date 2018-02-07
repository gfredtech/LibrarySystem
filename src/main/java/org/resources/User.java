package org.resources;

public class User {

    public User(int cardNumber, String name, String type, String subtype) {
        this.cardNumber = cardNumber;
        this.type = type;
        this.subtype = subtype;
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
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

    private String name;
    private String type;
    private String subtype;
    private int cardNumber;
    private String phoneNumber;
    private String address;

}
