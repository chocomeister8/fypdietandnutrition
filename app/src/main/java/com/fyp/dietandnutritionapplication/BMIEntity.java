package com.fyp.dietandnutritionapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BMIEntity {
    private double bmi;
    private long timestamp;
    private String id;

    public BMIEntity(double bmi, long timestamp, String id) {
        this.bmi = bmi;
        this.timestamp = timestamp;
        this.id = id;
    }

    public double getBmi() {
        return bmi;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    // Method to get a formatted date string
    public String getFormattedDate() {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
