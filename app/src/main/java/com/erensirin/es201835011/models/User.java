package com.erensirin.es201835011.models;

public class User {
    private String uid;
    private String fullname;
    private String email;

    public User() {
    }

    public User(String uid, String fullname, String email) {
        this.uid = uid;
        this.fullname = fullname;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
