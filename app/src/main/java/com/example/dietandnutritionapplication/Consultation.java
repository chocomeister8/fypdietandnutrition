package com.example.dietandnutritionapplication;

public class Consultation {
    private String dateTime;
    private String nutriName;
    private String status;

    public Consultation(String dateTime, String clientName, String status) {
        this.dateTime = dateTime;
        this.nutriName = nutriName;
        this.status = status;
    }

    // Getters and Setters
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getNutriName() {
        return nutriName;
    }

    public void setNutriName(String nutriName) {
        this.nutriName = nutriName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
