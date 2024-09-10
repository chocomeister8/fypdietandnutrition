package com.example.dietandnutritionapplication;

public class Admin extends Profile{
    private String gender;
    private String status;
    private String role;

    public Admin(){
        super();
    }
    public Admin(String gender,String role){
        this.gender = gender;
        this.status = "active";
        this.role = "admin";
    }
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
