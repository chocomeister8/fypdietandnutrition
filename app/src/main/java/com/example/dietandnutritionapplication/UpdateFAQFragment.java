package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateFAQFragment extends Fragment {

    private FAQ selectedFAQ;
    private TextView faqIdTextView;
    private EditText titleEditText;
    private EditText questionEditText;
    private EditText answerEditText;
    private EditText datecreatedEditText;
    private Button updateButton;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_faq, container, false);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Find EditText fields
        faqIdTextView = view.findViewById(R.id.faqId);
        titleEditText = view.findViewById(R.id.titleEdit);
        questionEditText = view.findViewById(R.id.questionEdit);
        answerEditText = view.findViewById(R.id.answerEdit);
        datecreatedEditText = view.findViewById(R.id.dateEdit);
        updateButton = view.findViewById(R.id.saveFAQ);  // Assume you have an update button in the layout

        faqIdTextView.setVisibility(View.GONE);  // Programmatically hides the TextView


        // Retrieve the selected FAQ from the arguments
        if (getArguments() != null) {
            selectedFAQ = (FAQ) getArguments().getSerializable("selectedFAQ");

            // Populate EditText fields with selectedFAQ details
            if (selectedFAQ != null) {
                faqIdTextView.setText(selectedFAQ.getFaqId());
                titleEditText.setText(selectedFAQ.getTitle());
                questionEditText.setText(selectedFAQ.getQuestion());
                answerEditText.setText(selectedFAQ.getAnswer());
                datecreatedEditText.setText(selectedFAQ.getDateCreated());
            }
        }

        // Set an onClickListener on the update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to update FAQ in Firestore
                updateFAQInFirestore();
            }
        });

        return view;
    }

    private void updateFAQInFirestore() {
        // Get updated values from the EditText fields
        String updatedTitle = titleEditText.getText().toString();
        String updatedQuestion = questionEditText.getText().toString();
        String updatedAnswer = answerEditText.getText().toString();
        String updatedDate = datecreatedEditText.getText().toString();

        // Check if the selectedFAQ and its ID are not null
        if (selectedFAQ != null && selectedFAQ.getFaqId() != null) {
            String id = selectedFAQ.getFaqId();  // Get the document ID

            // Prepare the map with specific fields to update
            Map<String, Object> updatedFields = new HashMap<>();
            updatedFields.put("id", id);          // Update the id field
            updatedFields.put("date", updatedDate);  // Update the date field
            updatedFields.put("title", updatedTitle);
            updatedFields.put("question", updatedQuestion);
            updatedFields.put("answer", updatedAnswer);

            // Update specific fields in the FAQ document in Firestore
            firestore.collection("FAQ").document(id)
                    .update(updatedFields)  // Use update to update specific fields
                    .addOnSuccessListener(aVoid -> {
                        // Display a success message
                        Toast.makeText(getActivity(), "FAQ updated successfully", Toast.LENGTH_SHORT).show();

                        // Create a new FAQ object with the updated values
                        FAQ updatedFAQ = new FAQ(id, updatedTitle, updatedQuestion, updatedAnswer, updatedDate);

                        // Create a new instance of ViewFAQDetailsFragment and pass the updated FAQ
                        FAQDetailsFragment viewFAQDetailsFragment = new FAQDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("selectedFAQ", updatedFAQ);  // Pass updated FAQ
                        viewFAQDetailsFragment.setArguments(bundle);

                        // Perform the fragment transaction to replace UpdateFAQFragment with ViewFAQDetailsFragment
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, viewFAQDetailsFragment)  // R.id.frame_layout is the container ID
                                .addToBackStack(null)  // Optional: adds to the back stack
                                .commit();
                    })
                    .addOnFailureListener(e -> {
                        // Display an error message
                        Toast.makeText(getActivity(), "Failed to update FAQ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getActivity(), "Error: FAQ ID is null", Toast.LENGTH_SHORT).show();
        }
    }
}
