package com.fyp.dietandnutritionapplication;

public class Admin extends Profile{
    private String gender;
    private String profileImageUrl;

    public Admin() {
        super("", "", "", "", "", "", "","");
    }

    public Admin(String firstName, String lastName, String username, String phone, String email, String gender, String role , String dateJoined, String profileImageUrl) {
        super(firstName, lastName, username, phone, email, gender, role, dateJoined);
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

}
