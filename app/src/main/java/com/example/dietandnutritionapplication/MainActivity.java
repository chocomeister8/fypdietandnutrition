package com.example.dietandnutritionapplication;

import static android.app.PendingIntent.getActivity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.dietandnutritionapplication.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isAdminMode = false;
    private ArrayList<Profile> accountArray = new ArrayList<>();
    private ArrayList<FAQ> faqArray = new ArrayList<>();
    FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        User user1 = new User();
        user1.setUsername("zaw");
        user1.setPassword("123");
        user1.setRole("user");
        accountArray.add(user1);

        User user2 = new User();
        user2.setUsername("zero");
        user2.setPassword("123");
        user2.setRole("user");
        accountArray.add(user2);

        User user3 = new User();
        user3.setUsername("ally");
        user3.setPassword("123");
        user3.setRole("user");
        accountArray.add(user3);

        Admin admin1 = new Admin();
        admin1.setUsername("admin");
        admin1.setPassword("admin123");
        admin1.setRole("admin");
        accountArray.add(admin1);

        Nutritionist nutritionist1 = new Nutritionist();
        nutritionist1.setUsername("sim");
        nutritionist1.setPassword("123");
        nutritionist1.setRole("nutritionist");
        accountArray.add(nutritionist1);

        FAQ faq1 = new FAQ();
        faq1.setTitle("What is the purpose of this app?");
        faq1.setQuestion("What can this app do to help with my diet and nutrition?");
        faq1.setAnswer("This app helps you track your daily food intake, monitor your calorie consumption, and provide nutritional information. It also offers personalized meal plans based on your dietary goals and preferences.");
        faq1.setDateCreated(" 09-10-2024 10:00");
        faqArray.add(faq1);

        FAQ faq2 = new FAQ();
        faq2.setTitle("How do I set dietary goals?");
        faq2.setQuestion("What steps should I follow to set my dietary goals in the app?");
        faq2.setAnswer("Navigate to the 'Goals' section, where you can set specific dietary objectives such as weight loss, muscle gain, or maintaining your current weight. The app will provide recommendations and track your progress towards these goals.");
        faq2.setDateCreated(" 09-11-2024 10:00");
        faqArray.add(faq2);

        FAQ faq3 = new FAQ();
        faq3.setTitle("Are there any recipes available in the app?");
        faq3.setQuestion("Can I access recipes that fit my dietary preferences?");
        faq3.setAnswer("Yes, the app features a variety of recipes that align with different dietary preferences and restrictions. You can filter recipes based on ingredients, cooking time, and nutritional content.");
        faq3.setDateCreated(" 09-12-2024 10:00");
        faqArray.add(faq3);

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        setupGuestNavigation();
        replaceFragment(new LandingFragment());

    }

    public Task<AuthResult> createUserAccount(Context context, String firstname, String username, String dob, String email, String phNum, String gender, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the newly created user
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid(); // Unique ID for the user

                            // Create a new user object with additional fields
                            User userCreate = new User();
                            userCreate.setFirstName(firstname);
                            userCreate.setUsername(username);
                            userCreate.setDob(dob);
                            userCreate.setEmail(email);
                            userCreate.setPhoneNumber(phNum);
                            userCreate.setGender(gender);
                            userCreate.setPassword(password); // Ideally, don't store raw passwords!

                            // Save additional user data to Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users").document(userId).set(userCreate)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Data successfully saved
                                            Log.d("Firestore", "User profile saved successfully");
                                            Toast.makeText(context, "Account registered", Toast.LENGTH_SHORT).show();
                                            // You need to implement replaceFragment() method properly
                                            replaceFragment(new LandingFragment());
                                        } else {
                                            // Handle failure to save data in Firestore
                                            Log.e("Firestore", "Failed to save user profile", task1.getException());
                                            Toast.makeText(context, "Failed to register account in Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Handle user registration failure
                        Log.e("Auth", "User registration failed", task.getException());
                        Toast.makeText(context, "Account registration failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }
        public void createFAQ(String title, String question, String answer, String dateCreated){
            FAQ faqcreate = new FAQ();
            faqcreate.setTitle(title);
            faqcreate.setQuestion(question);
            faqcreate.setAnswer(answer);
            faqcreate.setDateCreated(dateCreated);
            faqArray.add(faqcreate);
        }

        public void createAdminAccount(String firstname,String lastname,String username, String password,String phNum,String dob,String email,String gender, String role) {
            Admin adminCreate = new Admin();
            adminCreate.setUsername(username);
            adminCreate.setPhoneNumber(phNum);
            adminCreate.setPassword(password);
            adminCreate.setRole(role);
            adminCreate.setFirstName(firstname);
            adminCreate.setLastName(lastname);
            adminCreate.setDob(dob);
            adminCreate.setEmail(email);
            adminCreate.setGender(gender);
            accountArray.add(adminCreate);
        }

        public ArrayList<Profile> getAccountArray() {
            return accountArray;
        }

        public ArrayList<FAQ> getFAQArray() {

            return faqArray;
        }


    public void switchToAdminMode() {
        isAdminMode = true;
        setupAdminNavigation();
        replaceFragment(new AdminHomeFragment());
    }

    public void switchToGuestMode() {
        isAdminMode = false;
        setupGuestNavigation();
        replaceFragment(new LandingFragment());
    }

    public void switchToUserMode() {
        isAdminMode = false;
        setupUserNavigation();
        replaceFragment(new userHomePageFragment());
    }

    public void switchToNutriMode() {
        isAdminMode = false;
        setupNutriNavigation();
        replaceFragment(new NutriHomeFragment());
    }

    private void setupAdminNavigation() {
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.getMenu().clear();
        binding.bottomNavigationView.inflateMenu(R.menu.admin_bottom_menu);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.adminhome:
                    replaceFragment(new AdminHomeFragment());
                    break;
                case R.id.viewallaccounts:
                    replaceFragment(new viewAccountsFragment());
                    break;
                case R.id.addfaqpage:
                    replaceFragment(new FAQFragment());
                    break;
                case R.id.adminviewprofile:
                    replaceFragment(new ProfileAFragment());
                    break;
            }
            return true;
        });
    }
    private void setupUserNavigation() {
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.getMenu().clear();
        binding.bottomNavigationView.inflateMenu(R.menu.user_bottom_menu);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.userHomepage:
                    replaceFragment(new userHomePageFragment());
                    break;
                case R.id.recipe:
                    replaceFragment(new navCreateFolderFragment());
                    break;
                case R.id.consultations:
                    replaceFragment(new ConsultationsUFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileUFragment());
                    break;
            }
            return true;
        });
    }


    private void setupGuestNavigation() {
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.getMenu().clear();
        binding.bottomNavigationView.inflateMenu(R.menu.guest_bottom_menu);


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.landing:
                    replaceFragment(new LandingFragment());
                    break;
                case R.id.recipe:
                    replaceFragment(new navGuestRecipesFolderFragment());
                    break;
                case R.id.meallog:
                    replaceFragment(new MealLogPreviewFragment());
                    break;
                case R.id.reviews:
                    replaceFragment(new AppReviewsFragment());
                    break;
            }
            return true;
        });
    }

    private void setupNutriNavigation() {
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.getMenu().clear();
        binding.bottomNavigationView.inflateMenu(R.menu.nutri_bottom_menu);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nutriHome:
                    replaceFragment(new NutriHomeFragment());
                    break;
                case R.id.recipe:
                    replaceFragment(new NutriRecipesFolderFragment());
                    break;
//                case R.id.addRecipe: // Link API
//                    replaceFragment(new AddRecipeFragment());
//                    break;
                case R.id.bookingPage:
                    replaceFragment(new BookingHistoryFragment());
                    break;
                case R.id.NutriViewProfile:
                    replaceFragment(new NutriViewProfileFragment());
                    break;
            }
            return true;
        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}