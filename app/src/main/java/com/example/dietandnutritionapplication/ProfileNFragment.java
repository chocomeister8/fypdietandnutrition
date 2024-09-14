package com.example.dietandnutritionapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ProfileNFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nprofile, container, false);

        Button button_update = view.findViewById(R.id.button);

        button_update.setOnClickListener(v -> {
            // Replace current fragment with NavProfileFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NutriUpdateProfilePage())
                    .addToBackStack(null)
                    .commit();
        });

        return view;

    }
}
//