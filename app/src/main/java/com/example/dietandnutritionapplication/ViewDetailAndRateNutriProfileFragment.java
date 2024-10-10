package com.example.dietandnutritionapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewDetailAndRateNutriProfileFragment extends Fragment {
    private Profile selectedProfile;
    private Nutritionist selectedNutri;
    private Consultation selectedConsultation;
    private List<AppRatingsReviews> reviews = new ArrayList<>();
    private ListView reviewsListView;
    private Button filterAllButton,rateButton,bookConsultationButton;
    private Spinner filterSortSpinner;
    private TextView ratingTextView;
    private AppReviewController adapter;
    private String usernameNutri;
    private String totalRatingString;

    // TODO: Rename and change types and number of parameters
    public static ViewDetailAndRateNutriProfileFragment newInstance(String param1, String param2) {
        ViewDetailAndRateNutriProfileFragment fragment = new ViewDetailAndRateNutriProfileFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_detail_and_rate_nutri_profile, container, false);
        // Inflate the layout for this fragment
        if (getArguments() != null) {
            selectedProfile = (Profile) getArguments().getSerializable("selectedProfile");
            if (selectedProfile instanceof Nutritionist) {
                selectedNutri= ((Nutritionist) selectedProfile);
            }
        }
        TextView fullNameTextView = view.findViewById(R.id.fullName);
        TextView emailTextView = view.findViewById(R.id.email);
        TextView educationTextView = view.findViewById(R.id.education);
        TextView bioTextView = view.findViewById(R.id.bio);
        TextView phonenumberTextView = view.findViewById(R.id.phoneNumber);
        TextView expertiseTextView = view.findViewById(R.id.expertise);

        if (selectedNutri != null) {
            fullNameTextView.setText("Full Name - " + selectedNutri.getFullName());
            emailTextView.setText("Email - " + selectedNutri.getEmail());
            educationTextView.setText("Education - " + selectedNutri.getEducation());
            bioTextView.setText("Bio - "+ selectedNutri.getBio());
            phonenumberTextView.setText("Phone - "+ selectedNutri.getPhoneNumber());
            expertiseTextView.setText("Expertise - " + selectedNutri.getExpertise());
        }
        usernameNutri = selectedNutri.getUsername();

        reviewsListView = view.findViewById(R.id.listView);
        adapter = new AppReviewController(getContext(), reviews);
        reviewsListView.setAdapter(adapter);

        ViewDetailAndRateNutriProfileController viewDetailAndRateNutriProfileController = new ViewDetailAndRateNutriProfileController();
        viewDetailAndRateNutriProfileController.retrieveRatingsByNutritionist(usernameNutri, new NutritionistRatingEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<AppRatingsReviews> ratingList) {
                reviews.clear();
                reviews.addAll(ratingList);
                adapter.notifyDataSetChanged(); // Refresh UI
                filterAllButton.setText("All (" + reviews.size() + ")");
                float averageRating = calculateAverageRating();
                ratingTextView.setTextSize(25);
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                String ratingText = decimalFormat.format(averageRating);
                ratingTextView.setText(ratingText);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Failed to retrieve ratings", e);
            }
        });

        // Setup ListView


        // Setup Button and Spinner
        filterAllButton = view.findViewById(R.id.filterAllButton);
        filterSortSpinner = view.findViewById(R.id.filterSortSpinner);
        ratingTextView = view.findViewById(R.id.ratingTextView);
        rateButton = view.findViewById(R.id.rateButton);
        bookConsultationButton = view.findViewById(R.id.bookButton);

        // Set the button text with total reviews count
        filterAllButton.setText("All (" + reviews.size() + ")");

        // Set up Spinner for star ratings
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("Highest to Lowest Rating");
        sortOptions.add("Most Recent");
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSortSpinner.setAdapter(sortAdapter);

        // Set the average rating manually for now with HTML-like formatting


        // Sort reviews by highest rating initially
        sortReviewsByRating();

        // Set up Spinner item selected listener
        filterSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Sort by highest to lowest rating
                        sortReviewsByRating();
                        break;
                    case 1:
                        // Sort by most recent
                        sortReviewsByDate();
                        break;
                }
                // Update ListView with sorted reviews
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no selection is made if needed
            }
        });

        // Set up button click listener
        filterAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show all reviews when "All" button is clicked
                adapter = new AppReviewController(getContext(), reviews);
                reviewsListView.setAdapter(adapter);
                // Reset Spinner selection to default sorting option
                filterSortSpinner.setSelection(0);
            }
        });

        rateButton.setOnClickListener(v -> {
            // Create an instance of RateNutriFragment
            RateNutriFragment rateNutriFragment = new RateNutriFragment();

            // Create a bundle to pass data
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedProfile", selectedProfile); // Pass the profile object

            // Set the arguments for the new fragment
            rateNutriFragment.setArguments(bundle);

            // Replace the current fragment with RateNutriFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, rateNutriFragment) // Make sure to replace with the correct container ID
                    .addToBackStack(null) // Add to back stack so you can navigate back
                    .commit();
        });

// Set up the button click listener
        bookConsultationButton.setOnClickListener(v -> {
//            ViewConsultationDetailsFragment viewConsultationDetailsFragment = new ViewConsultationDetailsFragment();
//
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("selectedProfile", selectedProfile);
//            viewConsultationDetailsFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ViewConsultationDetailsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private float calculateAverageRating() {
        if (reviews.isEmpty()) {
            return 0;
        }
        float totalRating = 0;
        for (AppRatingsReviews review : reviews) {
            totalRating += review.getRating();
        }
        return totalRating / reviews.size();
    }

    // Method to sort reviews by rating (highest to lowest)
    private void sortReviewsByRating() {
        Collections.sort(reviews, new Comparator<AppRatingsReviews>() {
            @Override
            public int compare(AppRatingsReviews r1, AppRatingsReviews r2) {
                return Float.compare(r2.getRating(), r1.getRating()); // Descending order
            }
        });
        // Notify the adapter of the change
        adapter.notifyDataSetChanged();
    }

    // Method to sort reviews by date (most recent)
    private void sortReviewsByDate() {
        Collections.sort(reviews, new Comparator<AppRatingsReviews>() {
            @Override
            public int compare(AppRatingsReviews r1, AppRatingsReviews r2) {
                // Assuming the date is in "dd-MM-yyyy HH:mm" format
                return r2.getDateTime().compareTo(r1.getDateTime()); // Most recent first
            }
        });
        // Notify the adapter of the change
        adapter.notifyDataSetChanged();
    }
}