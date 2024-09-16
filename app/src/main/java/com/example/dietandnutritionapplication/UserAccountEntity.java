package com.example.dietandnutritionapplication;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class UserAccountEntity {
    private FirebaseFirestore db;
    private ArrayList<Admin> admins = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Nutritionist> nutritionists = new ArrayList<>();
    private ArrayList<Profile> accounts = new ArrayList<>();


    public UserAccountEntity() {
        db = FirebaseFirestore.getInstance();
    }

    public interface DataCallback {
        void onSuccess(ArrayList<Profile> accounts);
        void onFailure(Exception e);
    }

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

                            callback.onSuccess(accounts);
                        } else {
                            callback.onFailure(new Exception("QuerySnapshot is null"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    private User createUserFromDocument(QueryDocumentSnapshot document) {
//        return new User(
//                document.getString("firstName"),
//                document.getString("lastName"),
//                document.getString("username"),
//                document.getString("phoneNumber"),
//                document.getString("password"),
//                document.getString("email"),
//                document.getString("gender"),
//                document.getString("role"),
//                document.getString("dateJoined"),
//                document.getLong("calorieLimit").intValue(),
//                document.getString("dietPreference"),
//                document.getString("foodAllergies"),
//                document.getString("healthGoal"),
//                document.getDouble("currentWeight"),
//                document.getDouble("currentHeight")
//        );
        User user = new User();
        user.setUsername(document.getString("username"));
        user.setRole(document.getString("role"));
        user.setLastName(document.getString("lastName"));
        user.setPhoneNumber(document.getString("phoneNumber"));
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
                document.getString("dateJoined")
        );
//        Admin user = new Admin();
//        user.setUsername(document.getString("username"));
//        user.setRole(document.getString("role"));
//        return user;
    }

    private Nutritionist createNutritionistFromDocument(QueryDocumentSnapshot document) {
        // Assuming profilePicture is a string path; handle conversion if needed
//        return new Nutritionist(
//                document.getString("firstName"),
//                document.getString("lastName"),
//                document.getString("username"),
//                document.getString("phoneNumber"),
//                document.getString("password"),
//                document.getString("email"),
//                document.getString("gender"),
//                document.getString("role"),
//                document.getString("dateJoined"),
//                document.getString("education"),
//                document.getString("contactInfo"),
//                document.getString("expertise"),
//                document.getString("bio"),
//                document.getString("profilePicture") // Convert to Bitmap if needed
//        );
        Nutritionist user = new Nutritionist();
        user.setUsername(document.getString("username"));
        user.setRole(document.getString("role"));
        user.setLastName(document.getString("lastName"));
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

    // Method to fetch accounts with callback
    public void fetchAccounts(DataCallback callback) {
        retrieveAndClassifyUsers(callback);
    }
}