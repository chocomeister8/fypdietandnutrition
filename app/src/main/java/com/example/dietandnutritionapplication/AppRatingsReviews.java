package com.example.dietandnutritionapplication;

public class AppRatingsReviews {
    private String title;
    private String review;
    private float rating;
    private String dateTime;
    private String username;

    public AppRatingsReviews(String title, String review, float rating, String dateTime, String username) {
        this.title = title;
        this.review = review;
        this.rating = rating;
        this.dateTime = dateTime;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

}
