package com.example.dietandnutritionapplication;

import android.content.Context;

public class UpdateFAQController {
    public UpdateFAQController(){

    }
    public void checkUpdateFAQ(String id, String updatedCategory, String updatedQuestion, String updatedAnswer, String updatedDate, Context context){
        FAQEntity faqEntity = new FAQEntity();
        faqEntity.updateFAQInFirestore( id, updatedCategory, updatedQuestion,  updatedAnswer,  updatedDate,context);
    }
}
