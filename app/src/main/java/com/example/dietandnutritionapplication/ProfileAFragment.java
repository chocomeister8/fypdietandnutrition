package com.example.dietandnutritionapplication;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileAFragment extends Fragment {

    private ViewAdminProfileController viewAdminProfileController;
    private EditText firstNameData, lastNameData, usernameData, phoneData, emailData;
    private Admin adminProfile;
    private Button saveButton, uploadImageButton;
    private Spinner genderSpinner;
    private ImageView adminImageView;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentAdmin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adminviewprofile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        currentAdmin = firebaseAuth.getCurrentUser();

        if (currentAdmin != null) {
            String adminId = currentAdmin.getUid();

            // Initialize TextViews
            firstNameData = view.findViewById(R.id.firstName);
            lastNameData = view.findViewById(R.id.lastName);
            usernameData = view.findViewById(R.id.username);
            genderSpinner = view.findViewById(R.id.gender_spinner);
            phoneData = view.findViewById(R.id.phone);
            emailData = view.findViewById(R.id.email);
            uploadImageButton = view.findViewById(R.id.upload_picture_button);
            saveButton = view.findViewById(R.id.save_button);
            adminImageView = view.findViewById(R.id.imageView);

            //adminProfile = new Admin("Weiss", "Low", "admin123", "81889009", "weiss@gmail.com", "Male", "admin", "11-09-2024");
            viewAdminProfileController = new ViewAdminProfileController((MainActivity) requireActivity());
            loadAdminProfile();

            ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.gender_array, android.R.layout.simple_spinner_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genderSpinner.setAdapter(genderAdapter);


            uploadImageButton.setOnClickListener(v -> openFileChooser());
            saveButton.setOnClickListener(v -> updateProfile(adminId));
        }
        return view;
    }

    private void loadAdminProfile() {
        if (currentAdmin != null) {
            viewAdminProfileController.getAdminById(currentAdmin.getUid(), new UserAccountEntity.AdminFetchCallback() {
                @Override
                public void onAdminFetched(Admin admin) {
                    populateProfileFields(admin);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), "Error loading profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                Glide.with(getContext())
                        .load(selectedImageUri)
                        .into(adminImageView);

                // Store the URI for later use
                imageUri = selectedImageUri;
                Log.d("ProfileImage", "Selected image URI: " + selectedImageUri.toString());

                uploadImageToFirebaseStorage();
            } else {
                Log.e("ProfileImage", "Selected image URI is null");
                Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("ProfileImage", "Image selection failed or request code mismatch");
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            Log.d("ProfileImage", "Uploading image to Firebase Storage");

            // Show a progress dialog or progress bar
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading Image");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference()
                    .child("profile_pictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("ProfileImage", "Image uploaded successfully");
                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Log.d("ProfileImage", "Download URL obtained: " + uri.toString());
                                    viewAdminProfileController.uploadProfilePic(uri.toString(), getContext());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ProfileImage", "Failed to get download URL: " + e.getMessage());
                                    Toast.makeText(getContext(), "Failed to retrieve download URL", Toast.LENGTH_SHORT).show();
                                })
                                .addOnCompleteListener(task -> progressDialog.dismiss()); // Dismiss on completion
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileImage", "Failed to upload image: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss(); // Dismiss on failure
                    });
        } else {
            Log.e("ProfileImage", "Image URI is null");
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateProfileFields(Admin admin) {
        firstNameData.setText(admin.getFirstName());
        lastNameData.setText(admin.getLastName());
        usernameData.setText(admin.getUsername());
        phoneData.setText(admin.getPhoneNumber());
        emailData.setText(admin.getEmail());
        genderSpinner.setSelection(getSpinnerIndex(genderSpinner, admin.getGender()));


        if (admin.getProfileImageUrl() != null) {
            Glide.with(this)
                    .load(admin.getProfileImageUrl())
                    .placeholder(R.drawable.profile)
                    .into(adminImageView);
        }

    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

    private void updateProfile(String adminId) {
        viewAdminProfileController.getAdminById(adminId, new UserAccountEntity.AdminFetchCallback() {
            @Override
            public void onAdminFetched(Admin currentAdmin) {
                if (currentAdmin == null) {
                    Toast.makeText(getContext(), "Admin data not found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String username = usernameData.getText().toString();
                String firstName = firstNameData.getText().toString();
                String lastName = lastNameData.getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String phone = phoneData.getText().toString();
                String email = emailData.getText().toString();

                Admin updatedAdmin = new Admin();
                updatedAdmin.setUsername(username.equals(currentAdmin.getUsername()) ? currentAdmin.getUsername() : username);
                updatedAdmin.setFirstName(firstName.equals(currentAdmin.getFirstName()) ? currentAdmin.getFirstName() : firstName);
                updatedAdmin.setLastName(lastName.equals(currentAdmin.getLastName()) ? currentAdmin.getLastName() : lastName);
                updatedAdmin.setEmail(email.equals(currentAdmin.getEmail()) ? currentAdmin.getEmail() : email);
                updatedAdmin.setGender(gender.equals(currentAdmin.getGender()) ? currentAdmin.getGender() : gender);
                updatedAdmin.setPhoneNumber(phone.equals(currentAdmin.getPhoneNumber()) ? currentAdmin.getPhoneNumber() : phone);

                viewAdminProfileController.updateAdminProfile(adminId, updatedAdmin, getContext(), new UserAccountEntity.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), "Error updating profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Error fetching admin data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}