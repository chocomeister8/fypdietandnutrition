package com.example.dietandnutritionapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LandingFragment extends Fragment {

    public LandingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.landingpage, container, false);

        // Initialize ImageView and set click listeners
        ImageView userImageView = view.findViewById(R.id.userimg);
        userImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserRegisterActivity.class);
            startActivity(intent);
        });

        ImageView nutriImageView = view.findViewById(R.id.nutriimg);
        nutriImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NutritionRegister.class);
            startActivity(intent);
        });

        ImageView adminImageView = view.findViewById(R.id.adminimg);
        adminImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AdminHomeFragment.class);
            startActivity(intent);
        });

        return view;
    }
}//