package com.example.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class UserConsultationsController extends ArrayAdapter<Nutritionist> {

    public UserConsultationsController(Context context, List<Nutritionist> nutritionists) {
        super(context, 0, nutritionists);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate a new view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_nutritionist, parent, false);
        }

        // Get the data item for this position
        Nutritionist nutritionist = getItem(position);

        // Lookup view for data population
        ImageView profilePicture = convertView.findViewById(R.id.profile_picture);
        TextView name = convertView.findViewById(R.id.nutritionist_name);
        RatingBar rating = convertView.findViewById(R.id.nutritionist_rating);
        TextView info = convertView.findViewById(R.id.nutritionist_info);

        // Populate the data into the template view using the data object
        if (nutritionist != null) {
            name.setText(nutritionist.getName());
            rating.setRating(getRatingFromExpertise(nutritionist.getExpertise())); // Method to convert expertise to rating
            info.setText(nutritionist.getBio());

            // You can set the profile picture if available
            // profilePicture.setImageBitmap(nutritionist.getProfilePicture());
        }

        // Return the completed view to render on screen
        return convertView;
    }

    // Example method to convert expertise to rating (customize as needed -)
    private float getRatingFromExpertise(String expertise) {
        switch (expertise) {
            case "Weight Loss":
                return 4.5f;
            case "Sports Nutrition":
                return 4.0f;
            default:
                return 3.5f;
        }
    }
}
