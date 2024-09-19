package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class UpdateFAQFragment extends Fragment {

    private FAQ selectedFAQ;
    private EditText titleEditText;
    private EditText questionEditText;
    private EditText answerEditText;
    private EditText datecreatedEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_faq, container, false);

        // Find EditText fields
        titleEditText = view.findViewById(R.id.titleEdit);
        questionEditText = view.findViewById(R.id.questionEdit);
        answerEditText = view.findViewById(R.id.answerEdit);
        datecreatedEditText = view.findViewById(R.id.dateEdit);

        // Retrieve the selected FAQ from the arguments
        if (getArguments() != null) {
            selectedFAQ = (FAQ) getArguments().getSerializable("selectedFAQ");

            // Populate EditText fields with selectedFAQ details
            if (selectedFAQ != null) {
                titleEditText.setText(selectedFAQ.getTitle());
                questionEditText.setText(selectedFAQ.getQuestion());
                answerEditText.setText(selectedFAQ.getAnswer());
                datecreatedEditText.setText(selectedFAQ.getDateCreated());
            }
        }

        return view;
    }
}
