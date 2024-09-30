package com.example.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.content.Context;

public class AddFAQController {
    public AddFAQController(){

    }
    public void checkFAQ(String title, String category, String question, String answer, String date, ProgressDialog pd, Context context){
        FAQEntity faqEntity = new FAQEntity();
        faqEntity.insertFAQ(title,category,question,answer,date,pd,context);
    }
}
