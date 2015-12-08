package com.cms.cms.model;

/**
 * Created by Nishok on 12/5/2015.
 */

public class User {

    public String name, email, password, address, department, ssn, account;
    public int age;

    public User(String name, int age, String email, String password, String address, String department, String ssn, String account) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
        this.address = address;
        this.department = department;
        this.ssn = ssn;
        this.account = account;
    }

    public User(String email, String password) {
        this("", -1, email, password, "", "", "", "");
    }

    public User(String email, String password, String account) {
        this("", -1, email, password, "", "", "", account);
    }
}

