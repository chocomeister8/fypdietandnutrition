package com.fyp.dietandnutritionapplication;

import android.app.admin.FactoryResetProtectionPolicy;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


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
        TextView categoryTextView = view.findViewById(R.id.category);
        TextView questionTextView = view.findViewById(R.id.question);
        TextView answerTextView = view.findViewById(R.id.answer);
        TextView datecreatedTextView = view.findViewById(R.id.datecreated);
        Button updateFAQButton = view.findViewById(R.id.updateFAQ);  // Find the updateFAQ button
        Button backButton = view.findViewById(R.id.faqBack);  // Find the back button



        // Set the details in the UI
        if (selectedFAQ != null) {
            categoryTextView.setText(selectedFAQ.getCategory());
            questionTextView.setText(selectedFAQ.getQuestion());
            answerTextView.setText(selectedFAQ.getAnswer());
            datecreatedTextView.setText(selectedFAQ.getDateCreated());
        }

        updateFAQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the new fragment instance
                UpdateFAQFragment updateFAQFragment = new UpdateFAQFragment();

                // Pass the selected FAQ to the update fragment using a Bundle
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedFAQ", selectedFAQ);
                updateFAQFragment.setArguments(bundle);

                // Perform the fragment transaction to navigate to the UpdateFAQFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, updateFAQFragment); // Assuming your container ID is fragment_container
                transaction.addToBackStack(null); // This allows the user to navigate back
                transaction.commit();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment in the back stack
                getParentFragmentManager().popBackStack();
            }
        });


        return view;
    }

}
