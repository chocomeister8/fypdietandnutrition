package com.example.dietandnutritionapplication;

import java.io.Serializable;

public class Profile implements Serializable {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String phoneNumber;
    private String dob;
    private String email;
    private String gender;
    private String dateJoined;
    private String role;
    private String status;

    public String toString() {
        return firstName + " (" + lastName + ")"; // Customize as needed
    }

    public Profile(){
        this.setStatus("active");
    }

    public Profile(String firstName,String lastName,String username,String phoneNumber, String password, String email,String gender, String role, String dateJoined){
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateJoined = dateJoined;
        this.gender = gender;
        this.role = role;
        this.setStatus("active");
        this.password = password;


    }

    public Profile(String firstName,String lastName,String username,String phoneNumber, String email,String gender){
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) { this.gender = gender;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public void setRole (String role){
        this.role= role;
    }

    public String getRole(){
        return this.role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
