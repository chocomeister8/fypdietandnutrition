package com.example.dietandnutritionapplication;

public class Admin extends Profile{
    private String gender;


    public Admin(){
        super();
    }
    public Admin(String gender,String role){
        this.gender = gender;
        this.setStatus("active");
        this.setRole("admin");

    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
