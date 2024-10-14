package com.fyp.dietandnutritionapplication;

import java.util.ArrayList;

public class ViewFAQController {
    private ArrayList<FAQ> allFAQ = new ArrayList<>();

    public void getAllFAQ(FAQEntity.DataCallback callback) {
        FAQEntity faqEntity = new FAQEntity();
        faqEntity.fetchFAQ(new FAQEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<FAQ> faqs) {
                allFAQ.clear();
                allFAQ.addAll(faqs);
                callback.onSuccess(allFAQ); // Return the fetched FAQs to the callback
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                callback.onFailure(e); // Pass the failure to the callback
            }
        });
    }
}