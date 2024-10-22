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
            // Switch to user mode and set role as "user"
            if (getActivity() instanceof MainActivity) {
                URegisterFragment uRegisterFragment = new URegisterFragment();
                Bundle bundle = new Bundle();
                bundle.putString("role", "user"); // Set the role to "user"
                uRegisterFragment.setArguments(bundle);
                ((MainActivity) getActivity()).replaceFragment(uRegisterFragment);
            }
        });

        nutriImageView.setOnClickListener(v -> {
            // Switch to nutritionist mode and set role as "nutritionist"
            if (getActivity() instanceof MainActivity) {
                URegisterFragment uRegisterFragment = new URegisterFragment();
                Bundle bundle = new Bundle();
                bundle.putString("role", "nutritionist"); // Set the role to "nutritionist"
                uRegisterFragment.setArguments(bundle);
                ((MainActivity) getActivity()).replaceFragment(uRegisterFragment);
            }
        });

        return view;
    }
}
