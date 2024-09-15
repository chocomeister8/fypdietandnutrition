package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ProfileAFragment extends Fragment {

    private TextView firstNameTextView, lastNameTextView, usernameTextView, genderTextView, phoneTextView, emailTextView, accountActiveSinceTextView;
    private Admin adminProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewprofile, container, false);

        // Initialize TextViews
        firstNameTextView = view.findViewById(R.id.firstName);
        lastNameTextView = view.findViewById(R.id.lastName);
        usernameTextView = view.findViewById(R.id.username);
        genderTextView = view.findViewById(R.id.gender);
        phoneTextView = view.findViewById(R.id.phone);
        emailTextView = view.findViewById(R.id.email);
        accountActiveSinceTextView = view.findViewById(R.id.accountActiveSince);

        adminProfile = new Admin("Weiss", "Low", "admin123", "81889009", "weiss@gmail.com", "Male", "admin", "11-09-2024");

        // Set data to TextViews
        firstNameTextView.setText(adminProfile.getFirstName());
        lastNameTextView.setText(adminProfile.getLastName());
        usernameTextView.setText(adminProfile.getUsername());
        phoneTextView.setText(adminProfile.getPhoneNumber());
        emailTextView.setText(adminProfile.getEmail());
        genderTextView.setText(adminProfile.getGender());
        accountActiveSinceTextView.setText(adminProfile.getDateJoined());

        Button updateProfileButton = view.findViewById(R.id.updateProfile);
        updateProfileButton.setOnClickListener(v -> {
            // Replace the current fragment with AccountsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new UpdateProfileAFragment()); // Ensure R.id.frame_layout is the container in your activity
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        return view;
    }


}
