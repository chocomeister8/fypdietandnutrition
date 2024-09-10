package com.example.dietandnutritionapplication;

public class FAQ {
    private String title;
    private String question;
    private String answer;
    private String dateCreated;

    public FAQ(String title, String question, String answer, String dateCreated) {
        this.title = title;
        this.question = question;
        this.answer = answer;
        this.dateCreated = dateCreated;
    }

    public String getTitle() { return title;
    }
    public String getQuestion() { return question;
    }
    public String getAnswer() { return answer;
    }
    public String getDateCreated() { return dateCreated;
    }
}


