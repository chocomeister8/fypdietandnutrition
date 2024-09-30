package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class userViewFAQFragment extends Fragment {

    private ExpandableListView faqExpandableListView;
    private FAQExpandableListAdapter faqAdapter;
    private ViewFAQController viewFAQController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_u_viewfaqs, container, false);

        // Initialize the ExpandableListView
        faqExpandableListView = view.findViewById(R.id.faqExpandableListView);
        viewFAQController = new ViewFAQController();

        // Load FAQs
        loadFAQs();

        return view;
    }

    private void loadFAQs() {
        viewFAQController.getAllFAQ(new FAQEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<FAQ> faqs) {
                // Convert FAQs into categories for the adapter
                List<String> groupTitles = new ArrayList<>();
                HashMap<String, List<String>> childItems = new HashMap<>();
                HashMap<String, List<String>> answers = new HashMap<>();

                for (FAQ faq : faqs) {
                    String category = faq.getCategory();
                    String question = faq.getQuestion();
                    String answer = faq.getAnswer();

                    if (!groupTitles.contains(category)) {
                        groupTitles.add(category);
                        childItems.put(category, new ArrayList<>());
                        answers.put(category, new ArrayList<>());
                    }
                    childItems.get(category).add(question);
                    answers.get(category).add(answer);
                }

                // Set up the adapter with the fetched data
                faqAdapter = new FAQExpandableListAdapter(groupTitles, childItems, answers);
                faqExpandableListView.setAdapter(faqAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load FAQs.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
