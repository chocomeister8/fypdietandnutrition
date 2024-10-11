package com.example.dietandnutritionapplication;

import static java.security.AccessController.getContext;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class UserAccountEntity {
    private FirebaseFirestore db;
    private ArrayList<Admin> admins = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Nutritionist> nutritionists = new ArrayList<>();
    private ArrayList<Profile> accounts = new ArrayList<>();
    FirebaseAuth mAuth;
    private ArrayList<Profile> nutriProfiles = new ArrayList<>();


    public UserAccountEntity() {

        db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public interface DataCallback {
        void onSuccess(ArrayList<Profile> accounts);
        void onFailure(Exception e);
    }

//    public interface UserProfileUpdateCallback {
//        void onSuccess();
//        void onFailure(String errorMessage);
//    }

    public void retrieveAndClassifyUsers(final DataCallback callback) {
        db.collection("Users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            admins.clear();
                            users.clear();
                            nutritionists.clear();
                            accounts.clear();

                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String role = document.getString("role");

                                switch (role) {
                                    case "user":
                                        User user = createUserFromDocument(document);
                                        users.add(user);
                                        user.setStatus(document.getString("status"));
                                        accounts.add(user);
                                        break;
                                    case "admin":
                                        Admin admin = createAdminFromDocument(document);
                                        admins.add(admin);
                                        admin.setStatus(document.getString("status"));
                                        accounts.add(admin);

                                        break;
                                    case "nutritionist":
                                        Nutritionist nutritionist = createNutritionistFromDocument(document);
                                        nutritionists.add(nutritionist);
                                        accounts.add(nutritionist);
                                        break;
                                }
                            }

                            callback.onSuccess(accounts);
                        } else {
                            callback.onFailure(new Exception("QuerySnapshot is null"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }


    public void retrieveNutritionists(final DataCallback callback) {
        db.collection("Users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            admins.clear();
                            users.clear();
                            nutritionists.clear();
                            accounts.clear();

                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String role = document.getString("role");

                                switch (role) {
                                    case "user":
                                        User user = createUserFromDocument(document);
                                        users.add(user);
                                        accounts.add(user);
                                        break;
                                    case "admin":
                                        Admin admin = createAdminFromDocument(document);
                                        admins.add(admin);
                                        accounts.add(admin);
                                        break;
                                    case "nutritionist":
                                        Nutritionist nutritionist = createNutritionistFromDocument(document);
                                        nutritionists.add(nutritionist);
                                        accounts.add(nutritionist);
                                        break;
                                }
                            }
                            nutriProfiles.addAll(nutritionists);
                            callback.onSuccess(nutriProfiles);
                        } else {
                            callback.onFailure(new Exception("QuerySnapshot is null"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    private User createUserFromDocument(QueryDocumentSnapshot document) {
        User user = new User();
        user.setUsername(document.getString("username"));
        user.setRole(document.getString("role"));
        user.setFirstName(document.getString("firstName"));
        user.setLastName(document.getString("lastName"));
        user.setPhoneNumber(document.getString("phoneNumber"));
        user.setDob(document.getString("dob"));
        user.setPassword(document.getString("password"));
        user.setEmail(document.getString("email"));
        user.setGender(document.getString("gender"));
        user.setRole(document.getString("role"));
        user.setDateJoined(document.getString("dateJoined"));
        user.setCalorieLimit(document.getLong("calorieLimit").intValue());
        user.setDietaryPreference(document.getString("dietPreference"));
        user.setFoodAllergies(document.getString("healthGoal"));
        user.setHealthGoal(document.getString("foodAllergies"));
        user.setCurrentWeight(document.getDouble("currentWeight"));
        user.setCurrentHeight(document.getDouble("currentHeight"));
        user.setActivityLevel(document.getString("activityLevel"));
        user.setStatus(document.getString("status"));
        return user;
    }

    private Admin createAdminFromDocument(QueryDocumentSnapshot document) {
        return new Admin(
                document.getString("firstName"),
                document.getString("lastName"),
                document.getString("username"),
                document.getString("phoneNumber"),
                document.getString("email"),
                document.getString("gender"),
                document.getString("role"),
                document.getString("dateJoined"),
                document.getString("profilePicture")

        );
    }

    private Nutritionist createNutritionistFromDocument(QueryDocumentSnapshot document) {
        // Assuming profilePicture is a string path; handle conversion if needed
        Nutritionist user = new Nutritionist();
        user.setUsername(document.getString("username"));
        user.setRole(document.getString("role"));
        user.setLastName(document.getString("lastName"));
        user.setFirstName(document.getString("firstName"));
        user.setPhoneNumber(document.getString("phoneNumber"));
        user.setPassword(document.getString("password"));
        user.setEmail(document.getString("email"));
        user.setGender(document.getString("gender"));
        user.setRole(document.getString("role"));
        user.setDateJoined(document.getString("dateJoined"));
        user.setEducation(document.getString("education"));
        user.setContactInfo(document.getString("contactInfo"));
        user.setExpertise(document.getString("expertise"));
        user.setBio(document.getString("bio"));
        user.setProfilePicture(document.getString("profilePicture"));

        return user;
    }


    public void fetchAccounts(DataCallback callback) {
        retrieveAndClassifyUsers(callback);
    }


    public void registerUser(String firstName, String lastName, String userName, String dob, String email, String phone, String gender, String password, String datejoined, Context context, RegisterCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            User userCreate = new User();
                            userCreate.setFirstName(firstName);
                            userCreate.setLastName(lastName);
                            userCreate.setUsername(userName);
                            userCreate.setDob(dob);
                            userCreate.setPassword(password);
                            userCreate.setEmail(email);
                            userCreate.setPhoneNumber(phone);
                            userCreate.setGender(gender);
                            userCreate.setDateJoined(datejoined);
                            userCreate.setRole("user");

                            // Save user data to Firestore
                            db.collection("Users").document(userId).set(userCreate)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Send verification email
                                            sendVerificationEmail(firebaseUser, callback);
                                        } else {
                                            callback.onFailure("Failed to save user data");
                                        }
                                    });
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user, RegisterCallback callback) {
        user.sendEmailVerification()
                .addOnCompleteListener(emailTask -> {
                    if (emailTask.isSuccessful()) {
                        callback.onSuccess(); // Notify success after sending email
                    } else {
                        callback.onFailure("Failed to send verification email: " + emailTask.getException().getMessage());
                    }
                });
    }


    public void registerNutri(String firstName, String lastName, String userName, String dob, String email, String phone, String gender, String password, String datejoined, Context context, RegisterCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            Nutritionist userCreate = new Nutritionist();
                            userCreate.setFirstName(firstName);
                            userCreate.setLastName(lastName);
                            userCreate.setUsername(userName);
                            userCreate.setDob(dob);
                            userCreate.setPassword(password);
                            userCreate.setEmail(email);
                            userCreate.setPhoneNumber(phone);
                            userCreate.setGender(gender);
                            userCreate.setDateJoined(datejoined);
                            userCreate.setRole("nutritionist");
                            userCreate.setBio(" ");
                            userCreate.setExpertise(" ");
                            userCreate.setEducation(" ");
                            userCreate.setContactInfo(" ");
                            db.collection("Users").document(userId).set(userCreate)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            callback.onSuccess();
                                        } else {
                                            callback.onFailure("Failed to save user data");
                                        }
                                    });
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public interface RegisterCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public void addAdmin(String firstName, String lastName, String userName, String dob, String email, String phone, String gender, String password, String date, Context context, RegisterCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            Admin adminCreate = new Admin();
                            adminCreate.setFirstName(firstName);
                            adminCreate.setLastName(lastName);
                            adminCreate.setUsername(userName);
                            adminCreate.setDob(dob);
                            adminCreate.setPassword(password);
                            adminCreate.setEmail(email);
                            adminCreate.setPhoneNumber(phone);
                            adminCreate.setGender(gender);
                            adminCreate.setDateJoined(date);
                            adminCreate.setRole("admin");


                            db.collection("Users").document(userId).set(adminCreate)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            callback.onSuccess();
                                        } else {
                                            callback.onFailure("Failed to save user data");
                                        }
                                    });
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void login(String enteredUsername, String enteredPassword, Context context, MainActivity mainActivity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Query Firestore to get the user's email by their username
        db.collection("Users")
                .whereEqualTo("username", enteredUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Get the email associated with the entered username
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String email = document.getString("email"); // Assume email is stored in Firestore

                        // Check the status of the user
                        String status = document.getString("status"); // Retrieve the status field

                        // If the status is not "active", show an error message and return early
                        if (!"active".equalsIgnoreCase(status)) {
                            Toast.makeText(context, "Account deactivated. Please contact the admin.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Sign in the user using FirebaseAuth
                        auth.signInWithEmailAndPassword(email, enteredPassword)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        // Authentication successful, retrieve additional user data from Firestore
                                        FirebaseUser firebaseUser = auth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            String userId = firebaseUser.getUid();

                                            // Fetch the user's role and other details from Firestore
                                            db.collection("Users").document(userId)
                                                    .get()
                                                    .addOnCompleteListener(userTask -> {
                                                        if (userTask.isSuccessful()) {
                                                            DocumentSnapshot userDoc = userTask.getResult();
                                                            if (userDoc.exists()) {
                                                                // Retrieve additional data like role
                                                                String role = userDoc.getString("role");
                                                                String username = userDoc.getString("username");

                                                                // Save the username and role in SharedPreferences
                                                                SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                editor.putString("loggedInUserName", username);
                                                                editor.putString("userRole", role); // Store role for future reference
                                                                editor.apply();

                                                                // Display login success message
                                                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show();

                                                                // Redirect based on the user's role
                                                                switch (role) {
                                                                    case "user":
                                                                        mainActivity.switchToUserMode();
                                                                        ViewUserProfileController profileController = new ViewUserProfileController(mainActivity);
                                                                        profileController.checkUserProfileCompletion(userId, context, mainActivity);
                                                                        break;
                                                                    case "admin":
                                                                        mainActivity.switchToAdminMode();
                                                                        break;
                                                                    case "nutritionist":
                                                                        mainActivity.switchToNutriMode();
                                                                        break;
                                                                    default:
                                                                        Toast.makeText(context, "Unknown role", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } else {
                                                                Toast.makeText(context, "User data not found in Firestore", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            Log.e("FirestoreError", "Error getting user data", userTask.getException());
                                                            Toast.makeText(context, "Error retrieving user data: " + userTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        // Authentication failed
                                        Exception e = authTask.getException();
                                        Log.e("AuthError", "Authentication failed: " + e.getMessage(), e);
                                        Toast.makeText(context, "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        // No user found with the entered username
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error querying username", e);
                    Toast.makeText(context, "Error querying username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public void fetchUserProfile(String userId, final UserProfileCallback callback) {
        db.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            Log.d("ProfileUFragment", "User retrieved: " + user);
                            callback.onUserProfileFetched(user);
                        } else {
                            callback.onFailure("No user found with the given ID.");
                        }
                    } else {
                        Log.e("ProfileUFragment", "Error fetching user profile", task.getException());
                        Log.e("ProfileUFragment", "Error details: " + task.getException().getMessage());
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public interface UserProfileCallback {
        void onUserProfileFetched(User user);
        void onFailure(String errorMessage);
    }

    public interface UserProfileUpdateCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface UserFetchCallback {
        void onUserFetched(User user);
        void onFailure(String errorMessage);
    }

    public interface AdminFetchCallback {
        void onAdminFetched(Admin admin);
        void onFailure(String errorMessage);
    }

    public void getUserById(String userId, UserFetchCallback callback) {
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    callback.onUserFetched(user);
                } else {
                    callback.onFailure("User data is null");
                }
            } else {
                callback.onFailure("User does not exist");
            }
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    public void getAdminById(String adminId, AdminFetchCallback callback) {
        DocumentReference docRef = db.collection("Users").document(adminId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Admin admin = documentSnapshot.toObject(Admin.class);
                if (admin != null) {
                    callback.onAdminFetched(admin);
                } else {
                    callback.onFailure("Admin data is null");
                }
            } else {
                callback.onFailure("Admin does not exist");
            }
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    public void updateUserProfile(String userId, Map<String, Object> updatedFields, UserProfileUpdateCallback callback) {
        db.collection("Users") // Replace with your Firestore collection name
                .document(userId)
                .update(updatedFields) // Use update to change only the specified fields
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("UserAccountEntity", "Error updating user profile", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void suspendUserProfileByUsername(String username, UserProfileUpdateCallback callback) {
        if (username == null) {
            Log.e("UserAccountEntity", "Username cannot be null");
            callback.onFailure("Invalid input: Username cannot be null");
            return;
        }

        // Define the hard-coded updates for status
        final Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("status", "deactivated"); // Change status to inactive

        Log.d("UserAccountEntity", "Attempting to suspend user: " + username);

        db.collection("Users") // Replace with your Firestore collection name
                .whereEqualTo("username", username) // Query to find the user by username
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("UserAccountEntity", "User found: " + document.getId());

                                db.collection("Users").document(document.getId())
                                        .update(updatedFields)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("UserAccountEntity", "User profile updated successfully.");
                                            callback.onSuccess();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("UserAccountEntity", "Error updating user profile", e);
                                            callback.onFailure(e.getMessage());
                                        });
                                break; // Exit the loop after updating the first match
                            }
                        } else {
                            Log.e("UserAccountEntity", "User not found for username: " + username);
                            callback.onFailure("User not found");
                        }
                    } else {
                        Log.e("UserAccountEntity", "Query failed: " + task.getException());
                        callback.onFailure("Query failed: " + task.getException().getMessage());
                    }
                });
    }

    public void reactivateUserProfileByUsername(String username, UserProfileUpdateCallback callback) {
        if (username == null) {
            Log.e("UserAccountEntity", "Username cannot be null");
            callback.onFailure("Invalid input: Username cannot be null");
            return;
        }

        // Define the hard-coded updates for status
        final Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("status", "active"); // Change status to inactive

        Log.d("UserAccountEntity", "Attempting to suspend user: " + username);

        db.collection("Users") // Replace with your Firestore collection name
                .whereEqualTo("username", username) // Query to find the user by username
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("UserAccountEntity", "User found: " + document.getId());

                                db.collection("Users").document(document.getId())
                                        .update(updatedFields)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("UserAccountEntity", "User profile updated successfully.");
                                            callback.onSuccess();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("UserAccountEntity", "Error updating user profile", e);
                                            callback.onFailure(e.getMessage());
                                        });
                                break; // Exit the loop after updating the first match
                            }
                        } else {
                            Log.e("UserAccountEntity", "User not found for username: " + username);
                            callback.onFailure("User not found");
                        }
                    } else {
                        Log.e("UserAccountEntity", "Query failed: " + task.getException());
                        callback.onFailure("Query failed: " + task.getException().getMessage());
                    }
                });
    }

    private void retrieveUsername(String userId,Context context) {
        // Retrieve username from Firestore

        db.collection("Users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("username");
                        } else {
                            Toast.makeText(context, "No user document found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Failed to retrieve username: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void saveProfileImageUriToFirestore(String imageUrl,Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("ProfileImage", "entity");
        // Save the image URL in Firestore under the user's profile
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to update profile picture: " + e.getMessage());
                    Toast.makeText(context, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                });
    }


    public void updateAdminProfile(String adminId, Map<String, Object> updatedFields, AdminProfileUpdateCallback callback) {

        db.collection("Users")
                .document(adminId)
                .update(updatedFields)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("UserAccountEntity", "Error updating user profile", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public interface AdminProfileUpdateCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }


}