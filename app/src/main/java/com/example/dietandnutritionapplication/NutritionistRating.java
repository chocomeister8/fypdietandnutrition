package com.example.dietandnutritionapplication;

public class NutritionistRating {
    private String title;
    private String review;
    private float rating;
    private String dateTime;
    private String user;
    private String nutriName;

    public NutritionistRating(String title, String review, float rating, String dateTime, String user,String nutriName) {
        this.title = title;
        this.review = review;
        this.rating = rating;
        this.dateTime = dateTime;
        this.user = user;
        this.nutriName = nutriName;
    }

    public String getTitle() {
        return title;
    }


    public String getReview() {
        return review;
    }

    public float getRating() {
        return rating;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getUser() {
        return user;
    }

    public String getNutriName() {
        return nutriName;
    }

}
