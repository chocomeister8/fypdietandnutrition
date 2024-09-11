package com.example.dietandnutritionapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FAQFragment extends Fragment{
    private ListView FAQListView;
    private List<FAQ> faq = new ArrayList<>();
    private FAQController adapter;
    private Spinner filterFAQspinner;

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
                "07-18-2024 15:55"));

        faq.add(new FAQ(
                "FAQ 2",
                "How can I personalize my diet plan?",
                "After completing a short assessment that includes your health goals, dietary preferences, and lifestyle, the app generates a personalized diet plan tailored to your specific needs. You can adjust it as you progress.",
                "07-20-2024 15:55"));

        faq.add(new FAQ(
                "FAQ 3",
                "How does the app help with weight loss or muscle gain?",
                "Based on your goals, the app will provide tailored recommendations for calorie intake, meal planning, and macronutrient distribution to help you either lose weight, gain muscle, or maintain your current physique.",
                "07-25-2024 15:55"));

        faq.add(new FAQ(
                "FAQ 4",
                "Are there recipes included in the app?",
                "Yes, the app offers a wide variety of healthy recipes that align with your nutritional goals and dietary preferences. You can filter recipes based on ingredients, cuisine, cooking time, and dietary needs.",
                "08-18-2024 15:55"));

        faq.add(new FAQ(
                "FAQ 5",
                "Is there expert guidance available through the app?",
                "The app provides access to nutrition articles, tips, and sometimes virtual consultations with certified dietitians or nutrition coaches to help you make informed decisions about your health.",
                "08-20-2024 15:55"));

        FAQListView = view.findViewById(R.id.faqListView);
        adapter = new FAQController(getContext(), faq);
        FAQListView.setAdapter(adapter);

        filterFAQspinner = view.findViewById(R.id.filterFAQSpinner);

        List<String> sortFAQ = new ArrayList<>();
        sortFAQ.add("Latest to oldest");
        sortFAQ.add("Oldest to latest");
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortFAQ);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterFAQspinner.setAdapter(sortAdapter);

        filterFAQspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Determine whether to sort latest to oldest or oldest to latest
                boolean latestToOldest = position == 0; // Assuming position 0 is "Latest to oldest"
                sortFAQByDate(latestToOldest);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if no item is selected
            }
        });

        return view;
    }
    private void sortFAQByDate(final boolean latestToOldest) {
        Collections.sort(faq, new Comparator<FAQ>() {
            @Override
            public int compare(FAQ r1, FAQ r2) {
                // Sort based on the latestToOldest flag
                return latestToOldest ? r2.getDateCreated().compareTo(r1.getDateCreated())
                        : r1.getDateCreated().compareTo(r2.getDateCreated());
            }
        });
        adapter.notifyDataSetChanged();
    }

}
