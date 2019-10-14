package com.stark.smartwearableheadset.models;

public class User {
    String name;
    String username;
    String password;
    String phone;
    String usertype;
    String associates[];

    public User(String name, String username, String password, String phone, String usertype, String[] associates) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.usertype = usertype;
        this.associates = associates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return usertype;
    }

    public void setUserType(String userType) {
        this.usertype = userType;
    }

    public String[] getAssociates() {
        return associates;
    }

    public void setAssociates(String[] associates) {
        this.associates = associates;
    }
}
