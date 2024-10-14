package com.fyp.dietandnutritionapplication;

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

import com.squareup.picasso.Picasso;

import java.util.List;

public class UserConsultationsController extends ArrayAdapter<Nutritionist> {

    private Context context;
    private List<Nutritionist> nutritionists;

    public UserConsultationsController(Context context, List<Nutritionist> nutritionists) {
        super(context, R.layout.fragment_consultations_u, nutritionists);
        this.context = context;
        this.nutritionists = nutritionists;
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
        ImageView profilePicture = convertView.findViewById(R.id.profile_picture);
        TextView name = convertView.findViewById(R.id.nutritionist_name);
        RatingBar rating = convertView.findViewById(R.id.nutritionist_rating);
        TextView info = convertView.findViewById(R.id.nutritionist_info);
        name.setText(nutritionist.getName());

        // Use Picasso to load the profile image into the ImageView
        if (nutritionist.getProfilePicture() != null && !nutritionist.getProfilePicture().isEmpty()) {
            Picasso.get().load(nutritionist.getProfilePicture()).into(profilePicture);
        } else {
            // Set a default placeholder image if the profile picture is missing
            profilePicture.setImageResource(R.drawable.profile);
        }

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
