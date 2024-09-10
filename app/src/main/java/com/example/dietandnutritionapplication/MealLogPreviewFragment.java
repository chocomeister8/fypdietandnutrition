package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class MealLogPreviewFragment extends Fragment {
    private ListView reviewsListView;
    private Button filterAllButton;
    private Spinner filterStarSpinner;
    private TextView ratingTextView;
    private List<AppRatingsReviews> reviews = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guest_meallogpreview, container, false);


        return view;
    }
}
//