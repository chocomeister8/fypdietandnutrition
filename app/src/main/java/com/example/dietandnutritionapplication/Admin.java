package com.example.dietandnutritionapplication;

public class Admin extends Profile{
    private String gender;

    public Admin() {
        super("", "", "", "", "", "", "","","");
    }

    public Admin(String firstName, String lastName, String username, String phone, String email, String gender, String role , String dateJoined) {
        super(firstName, lastName, username, phone, gender, email, gender, role, dateJoined);
    }


}
