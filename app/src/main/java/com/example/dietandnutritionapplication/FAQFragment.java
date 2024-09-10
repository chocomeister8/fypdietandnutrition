package com.example.dietandnutritionapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class FAQFragment extends Fragment{
    private ListView FAQListView;
    private List<FAQ> faq = new ArrayList<>();
    private FAQController adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.viewallfaqs, container, false);

        // Populate some dummy faq
        faq.add(new FAQ(
                "FAQ 1",
                "What does the app offer for tracking my diet?.",
                "The app provides tools to log meals, track calories, and monitor macro- and micronutrient intake. You can scan barcodes or manually input food data to maintain a detailed food diary.",
                "09-08-2024 15:55"));

        faq.add(new FAQ(
                "FAQ 2",
                "How can I personalize my diet plan?",
                "After completing a short assessment that includes your health goals, dietary preferences, and lifestyle, the app generates a personalized diet plan tailored to your specific needs. You can adjust it as you progress.",
                "09-08-2024 15:55"));

        faq.add(new FAQ(
                "FAQ 3",
                "How does the app help with weight loss or muscle gain?",
                "Based on your goals, the app will provide tailored recommendations for calorie intake, meal planning, and macronutrient distribution to help you either lose weight, gain muscle, or maintain your current physique.",
                "09-08-2024 15:55"));

        faq.add(new FAQ(
                "FAQ 4",
                "Are there recipes included in the app?",
                "Yes, the app offers a wide variety of healthy recipes that align with your nutritional goals and dietary preferences. You can filter recipes based on ingredients, cuisine, cooking time, and dietary needs.",
                "09-08-2024 15:55"));

        faq.add(new FAQ(
                "FAQ 5",
                "Is there expert guidance available through the app?",
                "The app provides access to nutrition articles, tips, and sometimes virtual consultations with certified dietitians or nutrition coaches to help you make informed decisions about your health.",
                "09-08-2024 15:55"));

        FAQListView = view.findViewById(R.id.faqListView);
        adapter = new FAQController(getContext(), faq);
        FAQListView.setAdapter(adapter);

        return view;
    }
}
