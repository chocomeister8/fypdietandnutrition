package com.fyp.dietandnutritionapplication;

import static androidx.fragment.app.FragmentManager.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NavPendingRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private FirebaseFirestore db;
    private RecyclerView recipesRecyclerView;
    private RecipeAdapter recipesAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private ViewRecipesController viewRecipesController;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private TextView notificationBadgeTextView;
    private NotificationUController notificationUController;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nav_pending_recipes, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            notificationBadgeTextView = view.findViewById(R.id.notificationBadgeTextView);

            notificationUController = new NotificationUController();
            notificationUController.fetchNotifications(userId, new Notification.OnNotificationsFetchedListener() {
                @Override
                public void onNotificationsFetched(List<Notification> notifications) {
                    // Notifications can be processed if needed

                    // After fetching notifications, count them
                    notificationUController.countNotifications(userId, new Notification.OnNotificationCountFetchedListener() {
                        @Override
                        public void onCountFetched(int count) {
                            if (count > 0) {
                                notificationBadgeTextView.setText(String.valueOf(count));
                                notificationBadgeTextView.setVisibility(View.VISIBLE);
                            } else {
                                notificationBadgeTextView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });

        }

        ImageView notiImage = view.findViewById(R.id.noti_icon);
        notiImage.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NotificationUFragment())
                    .addToBackStack(null)
                    .commit();

        });

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recipesRecyclerView = view.findViewById(R.id.recipe_status_recycle_view);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with an empty list and set it to the RecyclerView
        recipesAdapter = new RecipeAdapter(recipeList, this, false);
        recipesRecyclerView.setAdapter(recipesAdapter);

        ViewRecipesController viewRecipesController = new ViewRecipesController();


        setupNavigationButtons(view);

        String userId = getCurrentUserId(); // Get the current user ID
        // Fetch pending recipes for the current user and update the UI accordingly
        viewRecipesController.fetchPendingRecipesForUser(userId, new ViewRecipesController.OnRecipesFetchedListener() {
            @Override
            public void onRecipesFetched(List<Recipe> fetchedRecipes) {
                recipeList.clear();
                recipeList.addAll(fetchedRecipes);
                recipesAdapter.notifyDataSetChanged(); // Notify adapter of data change
            }
        });

        return view;
    }

    private void setupNavigationButtons(View view) {
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_vegetarian = view.findViewById(R.id.button_vegetarian);
        Button button_favourite = view.findViewById(R.id.button_favourite);
        Button button_personalise_recipes = view.findViewById(R.id.button_personalise);
        Button button_recipes_status = view.findViewById(R.id.button_recipes_status);
        Button button_recommendedRecipes = view.findViewById(R.id.button_recommendRecipes);

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> navigateToFragment(new NavAllRecipesFragment()));
        button_vegetarian.setOnClickListener(v -> navigateToFragment(new NavVegetarianRecipesFragment()));
        button_favourite.setOnClickListener(v -> navigateToFragment(new ViewFavouriteRecipesFragment()));
        button_personalise_recipes.setOnClickListener(v -> navigateToFragment(new NavCommunityRecipesFragment()));
        button_recipes_status.setOnClickListener(v -> navigateToFragment(new NavPendingRecipesFragment()));
        button_recommendedRecipes.setOnClickListener(v -> navigateToFragment(new NavRecommendedRecipesFragment()));
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)  // Add to back stack to enable back navigation
                .commit();
    }

    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        String recipeId = recipe.getRecipe_id(); // Get the recipe ID

        // Create a new instance of NutriRecipeDetailsFragment
        UserViewCommunityRecipeDetails recipeDetailsFragment = new UserViewCommunityRecipeDetails();

        // Create a bundle to pass the recipe ID
        Bundle args = new Bundle();
        args.putString("recipeId", recipeId);

        // Set the arguments for NutriRecipeDetailsFragment
        recipeDetailsFragment.setArguments(args);

        // Navigate to NutriRecipeDetailsFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, recipeDetailsFragment)
                .addToBackStack(null)  // Add to back stack to enable back navigation
                .commit();
    }
}

