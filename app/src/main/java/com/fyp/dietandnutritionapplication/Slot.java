package com.fyp.dietandnutritionapplication;

public class Slot {
    private String consultationId;
    private String date;
    private String time;
    private String nutritionistName;
    private String clientName;
    private String status;

    // Empty constructor required for Firestore
    public Slot() {
    }

    public Slot(String consultationId, String date, String time, String nutritionistName, String clientName, String status) {
        this.consultationId = consultationId;
        this.date = date;
        this.time = time;
        this.nutritionistName = nutritionistName;
        this.clientName = clientName;
        this.status = status;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNutritionistName() {
        return nutritionistName;
    }

    public void setNutritionistName(String nutritionistName) {
        this.nutritionistName = nutritionistName;
    }
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
