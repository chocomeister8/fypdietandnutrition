package com.example.dietandnutritionapplication;

import java.util.List;

public class RecipeResponse {
    private List<Hit> hits;

    public List<Hit> getHits() {
        return hits;
    }

    public static class Hit {
        private Recipe recipe;

        public Recipe getRecipe() {
            return recipe;
        }
    }
}

