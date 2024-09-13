package com.example.dietandnutritionapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.dietandnutritionapplication.databinding.ActivityMainBinding;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isAdminMode = false;
    private ArrayList<Profile> accountArray = new ArrayList<>();
    private ArrayList<FAQ> faqArray = new ArrayList<>();


    public void createUserAccount(String firstname, String username, String dob, String email, String phNum, String gender, String password) {
        User userCreate = new User();
        userCreate.setFirstName(firstname);
        userCreate.setUsername(username);
        userCreate.setDob(dob);
        userCreate.setEmail(email);
        userCreate.setPhoneNumber(phNum);
        userCreate.setGender(gender);
        userCreate.setPassword(password);
        accountArray.add(userCreate);
    }

    public void createFAQ(String title, String question, String answer, String dateCreated){
        FAQ faqcreate = new FAQ();
        faqcreate.setTitle(title);
        faqcreate.setQuestion(question);
        faqcreate.setAnswer(answer);
        faqcreate.setDateCreated(dateCreated);
        faqArray.add(faqcreate);
    }

    public void createAdminAccount(String username, String phNum, String password, String role) {
        Admin adminCreate = new Admin();
        adminCreate.setUsername(username);
        adminCreate.setPhoneNumber(phNum);
        adminCreate.setPassword(password);
        adminCreate.setRole(role);
        accountArray.add(adminCreate);
    }

    public ArrayList<Profile> getAccountArray() {
        return accountArray;
    }

    public ArrayList<FAQ> getFAQArray() {

        return faqArray;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        User user1 = new User();
        user1.setUsername("zaw");
        user1.setPassword("123");
        user1.setRole("user");
        accountArray.add(user1);
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
        replaceFragment(new MealLogUFragment());
    }

    public void switchToNutriMode() {
        isAdminMode = false;
        setupNutriNavigation();
        replaceFragment(new LandingFragment());
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
                case R.id.meallog:
                    replaceFragment(new MealLogUFragment());
                    break;
                case R.id.recipe:
                    replaceFragment(new RecipesFolderFragment());
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
                    replaceFragment(new RecipesFolderFragment());
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
                    replaceFragment(new RecipesFolderFragment());
                    break;
//                case R.id.addRecipe: // Handle Add Recipe fragment
//                    replaceFragment(new AddRecipeFragment());
//                    break;
                case R.id.bookingPage:
                    replaceFragment(new BookingConsultationsFragment());
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
