package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class AccountDetailsFragment extends Fragment {

    private Profile selectedProfile; // Declare selectedProfile at class level
    private Nutritionist nutritionist;
    private SuspendUserController suspendUserController;
    private ReactivateUserController reactivateUserController; // Declare an instance for ReactivateUserController
    private approveNutritionistController approveNutritionist;
    private rejectNutritionistController rejectNutritionist;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_account_details, container, false);
        suspendUserController = new SuspendUserController();
        reactivateUserController = new ReactivateUserController(); // Initialize the controller
        approveNutritionist = new approveNutritionistController();
        rejectNutritionist = new rejectNutritionistController();


        // Retrieve the profile from the arguments
        if (getArguments() != null) {
            selectedProfile = (Profile) getArguments().getSerializable("selectedProfile");
            nutritionist = (Nutritionist) getArguments().getSerializable("nutritionist");
        }

        // Find your TextView or other UI elements to display profile details
        TextView firstnameTextView = view.findViewById(R.id.firstName);
        TextView lastnameTextView = view.findViewById(R.id.lastName);
        TextView usernameTextView = view.findViewById(R.id.username);
        TextView phonenumberTextView = view.findViewById(R.id.phone);
        TextView genderTextView = view.findViewById(R.id.gender);
        TextView emailTextView = view.findViewById(R.id.email);
        TextView roleTextView = view.findViewById(R.id.role);
        TextView datejoinedTextView = view.findViewById(R.id.accountActiveSince);
        Button suspendUserButton = view.findViewById(R.id.suspendUserButton);
        Button reactivateUserButton = view.findViewById(R.id.ReactivateUserButton);

        TextView specializationTextView = view.findViewById(R.id.specialization);
        TextView experienceTextView = view.findViewById(R.id.experience);
        Button approveNutritionistButton = view.findViewById(R.id.approveNutritionists);
        Button rejectNutritionistButton = view.findViewById(R.id.rejectNutritionists);
        Button backButton = view.findViewById(R.id.backButton);

        CardView nutritionistDetails = view.findViewById(R.id.nutriprofileCard);

        String userRole = selectedProfile.getRole();
        String userStatus = selectedProfile.getStatus();
        Log.d("AccountDetailsFragment", "User role: " + userRole + ", status: " + userStatus);

        // Set the details in the UI
        if (selectedProfile != null) {
            firstnameTextView.setText(selectedProfile.getFirstName());
            lastnameTextView.setText(selectedProfile.getLastName());
            usernameTextView.setText(selectedProfile.getUsername());
            phonenumberTextView.setText(selectedProfile.getPhoneNumber());
            genderTextView.setText(selectedProfile.getGender());
            emailTextView.setText(selectedProfile.getEmail());
            roleTextView.setText(selectedProfile.getRole());
            datejoinedTextView.setText(selectedProfile.getDateJoined());

            // Check the user's status
            if ("active".equalsIgnoreCase(userStatus)) {
                suspendUserButton.setVisibility(View.VISIBLE);  // Show suspend button for active users
                reactivateUserButton.setVisibility(View.GONE);   // Hide reactivate button
                approveNutritionistButton.setVisibility(View.GONE);   // Hide approve button
                rejectNutritionistButton.setVisibility(View.GONE);    // Hide reject button
            } else if ("deactivated".equalsIgnoreCase(userStatus)) {
                suspendUserButton.setVisibility(View.GONE);   // Hide suspend button
                reactivateUserButton.setVisibility(View.VISIBLE);  // Show reactivate button
                approveNutritionistButton.setVisibility(View.GONE);   // Hide approve button
                rejectNutritionistButton.setVisibility(View.GONE);    // Hide reject button
            } else if ("pending".equalsIgnoreCase(userStatus) && "nutritionist".equalsIgnoreCase(userRole)) {
                suspendUserButton.setVisibility(View.GONE);  // Hide suspend button
                reactivateUserButton.setVisibility(View.GONE);  // Hide reactivate button
                approveNutritionistButton.setVisibility(View.VISIBLE);   // Show approve button for pending nutritionists
                rejectNutritionistButton.setVisibility(View.VISIBLE);    // Show reject button for pending nutritionists
            } else {
                // Handle unexpected status or roles
                Log.d("AccountDetailsFragment", "Unexpected status or role: " + userRole + ", " + userStatus);
                suspendUserButton.setVisibility(View.GONE);  // Hide all action buttons if status/role is unexpected
                reactivateUserButton.setVisibility(View.GONE);
                approveNutritionistButton.setVisibility(View.GONE);
                rejectNutritionistButton.setVisibility(View.GONE);
            }

            // Additional handling for Nutritionist-specific fields
            if (selectedProfile instanceof Nutritionist) {
                Nutritionist nutritionistProfile = (Nutritionist) selectedProfile;
                specializationTextView.setText(nutritionistProfile.getSpecialization());
                experienceTextView.setText(nutritionistProfile.getExperience());

                // Show nutritionist-specific fields
                specializationTextView.setVisibility(View.VISIBLE);
                experienceTextView.setVisibility(View.VISIBLE);
            } else {
                // Hide nutritionist-specific fields if not a Nutritionist
                specializationTextView.setVisibility(View.GONE);
                experienceTextView.setVisibility(View.GONE);
            }

            // Additional check for role
            if ("admin".equalsIgnoreCase(userRole) || "user".equalsIgnoreCase(userRole)) {
                // Hide specialization and experience for admin or user roles
                nutritionistDetails.setVisibility(View.GONE);
                specializationTextView.setVisibility(View.GONE);
                experienceTextView.setVisibility(View.GONE);
            }
        } else {
            // Handle the case when selectedProfile is null
            Toast.makeText(getActivity(), "No profile selected.", Toast.LENGTH_SHORT).show();
        }

        suspendUserButton.setOnClickListener(v -> {
            if (selectedProfile != null) {
                String usernameToSuspend = selectedProfile.getUsername();

                // Call the suspendUser method with the callback
                suspendUserController.suspendUser(usernameToSuspend, new SuspendUserController.SuspendUserCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("SuspendUserController", "User profile suspended successfully.");
                        selectedProfile.setStatus("deactivated"); // Update the profile status locally
                        Toast.makeText(getActivity(), "Suspended user: " + usernameToSuspend, Toast.LENGTH_SHORT).show();

                        // Redirect to ViewAccountsFragment after successful suspension
                        redirectToViewAccountsFragment();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("SuspendUserController", "Failed to suspend user profile: " + errorMessage);
                        Toast.makeText(getActivity(), "Failed to suspend user: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "No user selected to suspend.", Toast.LENGTH_SHORT).show();
            }
        });

        reactivateUserButton.setOnClickListener(v -> {
            if (selectedProfile != null) {
                String usernameToReactivate = selectedProfile.getUsername();

                reactivateUserController.ReactivateUser(usernameToReactivate, new ReactivateUserController.ReactivateUserCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("ReactivateUserController", "User profile reactivated successfully.");
                        selectedProfile.setStatus("active"); // Update the profile status locally
                        Toast.makeText(getActivity(), "Reactivated user: " + usernameToReactivate, Toast.LENGTH_SHORT).show();

                        // Redirect to ViewAccountsFragment after successful suspension
                        redirectToViewAccountsFragment();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("ReactivateUserController", "Failed to reactivate user profile: " + errorMessage);
                        Toast.makeText(getActivity(), "Failed to reactivate user: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "No user selected to reactivate.", Toast.LENGTH_SHORT).show();
            }
        });

        approveNutritionistButton.setOnClickListener(v -> {
            if (selectedProfile != null) {
                String nutritionistToApprove  = selectedProfile.getUsername();
                approveNutritionist.approveNutritionist(nutritionistToApprove);
                selectedProfile.setStatus("active"); // Update the profile status
                Toast.makeText(getActivity(), "Nutritionist Approved: " + nutritionistToApprove, Toast.LENGTH_SHORT).show();

                // Redirect to ViewAccountsFragment after reactivation
                redirectToViewAccountsFragment();
            } else {
                Toast.makeText(getActivity(), "No Nutritionist to approve.", Toast.LENGTH_SHORT).show();
            }
        });

        rejectNutritionistButton.setOnClickListener(v -> {
            if (selectedProfile != null) {
                String nutritionistToReject = selectedProfile.getUsername();
                rejectNutritionist.rejectNutritionist(nutritionistToReject );
                selectedProfile.setStatus("rejected"); // Update the profile status
                Toast.makeText(getActivity(), "Nutritionist Rejected: " + nutritionistToReject , Toast.LENGTH_SHORT).show();

                // Redirect to ViewAccountsFragment after reactivation
                redirectToAdminHomeFragment();
            } else {
                Toast.makeText(getActivity(), "No Nutritionist to approve.", Toast.LENGTH_SHORT).show();
            }
        });
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void redirectToViewAccountsFragment() {
        viewAccountsFragment viewAccountsFragment = new viewAccountsFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, viewAccountsFragment)  // Replace with your fragment container ID
                .addToBackStack(null)  // Optionally add to backstack
                .commit();
    }

    private void redirectToAdminHomeFragment() {
        AdminHomeFragment adminHomeFragment = new AdminHomeFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, adminHomeFragment)  // Replace with your fragment container ID
                .addToBackStack(null)  // Optionally add to backstack
                .commit();
    }
}