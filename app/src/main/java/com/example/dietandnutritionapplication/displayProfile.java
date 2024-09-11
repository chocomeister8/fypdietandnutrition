package com.example.dietandnutritionapplication;

public class displayProfile {
    private String username;
    private String role;

    public displayProfile(String name, String email) {
        this.username = name;
        this.role = email;
    }

    // Getters
    public String getName() {
        return username;
    }

    public String getEmail() {
        return role;
    }

    @Override
    public String toString() {
        String str = "Username : "+this.username + "    Role: "+ this.role;
        return str;  // This will be shown in the ListView
    }
}
