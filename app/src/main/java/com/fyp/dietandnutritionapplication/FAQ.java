package com.fyp.dietandnutritionapplication;

import java.io.Serializable;

public class FAQ implements Serializable {
    private String faqId;
    private String title;
    private String category;
    private String question;
    private String answer;
    private String dateCreated;

    public FAQ(){

    }

    public FAQ(String faqId, String category, String question, String answer, String dateCreated) {
        this.faqId = faqId;
        this.category = category;
        this.question = question;
        this.answer = answer;
        this.dateCreated = dateCreated;
    }

    public String getFaqId(){ return faqId;
    }

    public void setFaqId(String faqId){ this.faqId = faqId;
    }

    public String getCategory() { return category;
    }

    public void setCategory(String category) { this.category = category;
    }

    public String getQuestion() { return question;
    }

    public void setQuestion(String question) { this.question = question;
    }

    public String getAnswer() { return answer;
    }

    public void setAnswer(String answer) { this.answer = answer;
    }

    public String getDateCreated() { return dateCreated;
    }

    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated;
    }
}


