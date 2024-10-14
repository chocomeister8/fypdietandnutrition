package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class LandingFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.landingpage, container, false);

        ImageView userImageView = view.findViewById(R.id.userimg);
        ImageView nutriImageView = view.findViewById(R.id.nutriimg);

        userImageView.setOnClickListener(v -> {
            // Switch to guest mode
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToGuestMode();
            }
        });

        nutriImageView.setOnClickListener(v -> {
            // Switch to nutri mode (for nutritionists as guests)
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new LoginFragment());
            }
        });

        userImageView.setOnClickListener(v -> {
            // Switch to admin mode
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new LoginFragment());
            }
        });

        return view;
    }
}//