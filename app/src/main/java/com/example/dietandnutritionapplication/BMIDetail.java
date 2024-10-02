package com.example.dietandnutritionapplication;

public class BMIDetail {
    private double bmi;
    private long timestamp;
    private String documentId; // Add this field

    public BMIDetail(double bmi, long timestamp, String documentId) { // Update constructor
        this.bmi = bmi;
        this.timestamp = timestamp;
        this.documentId = documentId; // Set documentId
    }

    public double getBmi() {
        return bmi;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDocumentId() {
        return documentId; // Add this getter
    }
}
