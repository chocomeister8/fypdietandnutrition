package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AdminHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.adminhomepage, container, false);

        ImageView logoutImage = view.findViewById(R.id.right_icon);
        logoutImage.setOnClickListener(v -> {
            // Switch to guest mode (for nutritionists as guests)
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new LandingFragment());
            }
        });

        // Find the buttons in the layout
        Button viewAccountsButton = view.findViewById(R.id.viewAccountsButton);
        Button addAdminButton = view.findViewById(R.id.addAdminButton);
        Button addFAQButton = view.findViewById(R.id.addFAQbutton);

        // Set an OnClickListener on the viewAccountsButton
        viewAccountsButton.setOnClickListener(v -> {
            // Replace the current fragment with AccountsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new viewAccountsFragment()); // Ensure R.id.frame_layout is the container in your activity
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        addAdminButton.setOnClickListener(v -> {
            // Replace the current fragment with AccountsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new AddAdminFragment()); // Ensure R.id.frame_layout is the container in your activity
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        // Set an OnClickListener on the addFAQButton
        addFAQButton.setOnClickListener(v -> {
            // Replace the current fragment with FAQFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new AddFAQFragment()); // Ensure R.id.frame_layout is the container in your activity
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        return view;
    }

   /* public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.adminhomepage, container, false);

        // Initialize ImageView and set click listeners
        Button viewAccountsButton = view.findViewById(R.id.viewAccountsButton);
        viewAccountsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountsFragment.class);
            startActivity(intent);
        });

        Button addFAQButton = view.findViewById(R.id.addFAQbutton);
        addFAQButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FAQFragment.class);
            startActivity(intent);
        });
        return view;
    }*/
}//