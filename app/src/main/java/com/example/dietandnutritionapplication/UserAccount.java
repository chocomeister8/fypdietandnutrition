package com.example.dietandnutritionapplication;

public class UserAccount {
    private String username ;
    private String password ;
    private String status ;

    public UserAccount() {
        this.username = "null";
        this.password = "null";
        this.status = "null";
    }
    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.status   = "active";
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
