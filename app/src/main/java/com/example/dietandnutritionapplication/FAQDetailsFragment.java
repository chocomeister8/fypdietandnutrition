package com.example.dietandnutritionapplication;

import android.app.admin.FactoryResetProtectionPolicy;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class FAQDetailsFragment extends Fragment {
    private FAQ selectedFAQ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_faq_details, container, false);

        // Retrieve the profile from the arguments
        if (getArguments() != null) {
            selectedFAQ = (FAQ) getArguments().getSerializable("selectedFAQ");
        }

        // Find your TextView or other UI elements to display profile details
        TextView titleTextView = view.findViewById(R.id.title);
        TextView questionTextView = view.findViewById(R.id.question);
        TextView answerTextView = view.findViewById(R.id.answer);
        TextView datecreatedTextView = view.findViewById(R.id.datecreated);


        // Set the details in the UI
        if (selectedFAQ != null) {
            titleTextView.setText(selectedFAQ.getTitle());
            questionTextView.setText(selectedFAQ.getQuestion());
            answerTextView.setText(selectedFAQ.getAnswer());
            datecreatedTextView.setText(selectedFAQ.getDateCreated());
        }

        return view;
    }

}
