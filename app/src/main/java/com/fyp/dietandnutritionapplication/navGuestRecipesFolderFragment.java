package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class navGuestRecipesFolderFragment extends Fragment {

    private TextView register1ToAccessTextView, register2ToAccessTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_guest_recipes_folder, container, false);

        // Initialize buttons using view.findViewById
        Button button_all_recipes = view.findViewById(R.id.folder_all_recipes);
        register1ToAccessTextView = view.findViewById(R.id.register_to_access_1);
        register2ToAccessTextView = view.findViewById(R.id.register_to_access_2);

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> {
            // Replace current fragment with NavAllRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new navGuestRecipesFragment())
                    .addToBackStack(null)  // Add to back stack to enable back navigation
                    .commit();
        });

        register1ToAccessTextView.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new URegisterFragment());
            }
        });

        register2ToAccessTextView.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new URegisterFragment());
            }
        });



        return view;
    }
}
