package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class userHomePageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_homepage, container, false);

        // Initialize buttons using view.findViewById
        Button button_recipes = view.findViewById(R.id.button_recipes);
        Button button_mealLog = view.findViewById(R.id.button_MealLog);
//        Button button_diary = view.findViewById(R.id.diary);
        Button button_bmiCalculator = view.findViewById(R.id.bmiCalculator);
//        Button button_consultation = view.findViewById(R.id.consultation);
        Button button_healthReport = view.findViewById(R.id.healthReport);
//        Button button_faq = view.findViewById(R.id.FAQ);
        Button button_profile = view.findViewById(R.id.profile);
        Button button_mealLog1 = view.findViewById(R.id.button_MealLog1);

        // Set up button click listeners to navigate between fragments
        button_recipes.setOnClickListener(v -> {
            // Replace current fragment with NavAllRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new RecipesFolderFragment())
                    .addToBackStack(null)  // Add to back stack to enable back navigation
                    .commit();
        });

        // Define the OnClickListener
        View.OnClickListener buttonClickListener = v -> {
            // Replace current fragment with MealLogPreviewFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new MealLogUFragment())
                    .addToBackStack(null)
                    .commit();
        };

// Assign the OnClickListener to both buttons
        button_mealLog.setOnClickListener(buttonClickListener);
        button_mealLog1.setOnClickListener(buttonClickListener);

//        button_diary.setOnClickListener(v -> {
//            // Replace current fragment with NavFavouriteRecipesFragment
//            requireActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_layout, new NavFavouriteRecipesFragment())
//                    .addToBackStack(null)
//                    .commit();
//        });

        button_bmiCalculator.setOnClickListener(v -> {
            // Replace current fragment with NavRecipesStatusFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new BMICalculatorFragment())
                    .addToBackStack(null)
                    .commit();
        });

//        button_consultation.setOnClickListener(v -> {
//            // Replace current fragment with NavConsultationFragment
//            requireActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_layout, new NavConsultationFragment())
//                    .addToBackStack(null)
//                    .commit();
//        });

        button_healthReport.setOnClickListener(v -> {
            // Replace current fragment with NavHealthReportFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new healthReportFragment() )
                    .addToBackStack(null)
                    .commit();
        });

//        button_faq.setOnClickListener(v -> {
//            // Replace current fragment with NavFAQFragment
//            requireActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_layout, new displayFAQ())
//                    .addToBackStack(null)
//                    .commit();
//        });
//
        button_profile.setOnClickListener(v -> {
            // Replace current fragment with NavProfileFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ProfileUFragment())
                    .addToBackStack(null)
                    .commit();
        });



        return view;
    }
}
