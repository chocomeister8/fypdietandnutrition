package com.example.dietandnutritionapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewDetailAndRateNutriProfileFragment extends Fragment {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_detail_and_rate_nutri_profile, container, false);
    }
}