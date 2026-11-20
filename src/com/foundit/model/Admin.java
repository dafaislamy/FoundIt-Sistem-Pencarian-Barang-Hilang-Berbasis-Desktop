package com.foundit.model;

public class Admin extends Account {

    public Admin() {
    }

    @Override
    public String getAccountType() {
        return "Admin";
    }
}
