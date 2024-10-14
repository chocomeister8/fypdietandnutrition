package com.fyp.dietandnutritionapplication;

public class displayFAQ {
    private String title;
    private String question;
    private String answer;
    private String dateCreated;

    public displayFAQ(String title, String question, String answer, String dateCreated) {
        this.title = title;
        this.question = question;
        this.answer = answer;
        this.dateCreated = dateCreated;
    }

    public String getTitle(){ return title; }

    public String getQuestion() { return question; }

    public String getAnswer() { return answer;}

    public String getDateCreated() {return dateCreated;}

    public String toString() {
        String str = "Title : "+this.title + " Question: "+ this.question + " DateCreated: "+ this.dateCreated;
        return str;  // This will be shown in the ListView
    }

}
