package com.example.dietandnutritionapplication;

public class Nutritionist extends Profile{
    private String education;
    private String licenseID;


    public Nutritionist(){
        super();
        this.setRole("nutritionist");
        this.setStatus("active");
    }
    public Nutritionist(String education,String licenseID){
        this.education = education;
        this.licenseID = licenseID;
        this.setRole("nutritionist");
        this.setStatus("active");
    }

    public String getEducation(){
        return this.education;
    }
    public void setEducation(String education){
        this.education = education;
    }
    public String getLicenseID(){
        return this.licenseID;
    }
    public void setLicenseID(String licenseID){
        this.licenseID = licenseID;
    }




}
