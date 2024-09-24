package com.example.dietandnutritionapplication;

import android.content.Context;

public class UpdateFAQController {
    public UpdateFAQController(){

    }
    public void chechUpdateFAQ(String id, String updatedTitle, String updatedQuestion, String updatedAnswer, String updatedDate, Context context){
        FAQEntity faqEntity = new FAQEntity();
        faqEntity.updateFAQInFirestore( id,  updatedTitle,  updatedQuestion,  updatedAnswer,  updatedDate,context);
    }
}
