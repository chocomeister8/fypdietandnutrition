package com.example.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NutriProfileAdapter extends ArrayAdapter<Profile> {
    private Context context;
    private ArrayList<Profile> profiles;
    private ArrayList<Nutritionist> nutritionists;

    public NutriProfileAdapter(Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
        this.context = context;
        this.profiles = profiles;
        this.nutritionists = new ArrayList<>(); // Initialize the nutritionists list

        // Filter and add only Nutritionist objects


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NutriAdapterTest.ViewHolder viewHolder;
        nutritionists.clear();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nutri_item, parent, false);
            viewHolder = new NutriAdapterTest.ViewHolder();
            viewHolder.expertiseTextView = convertView.findViewById(R.id.expertise);
            viewHolder.fullnameTextView = convertView.findViewById(R.id.fullname);
            viewHolder.bioTextView = convertView.findViewById(R.id.bio);
            viewHolder.educationTextView = convertView.findViewById(R.id.education);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NutriAdapterTest.ViewHolder) convertView.getTag();
        }

        for (Profile profile : profiles) {
            if (profile instanceof Nutritionist) {
                nutritionists.add((Nutritionist) profile);
            }
        }
        Nutritionist currentProfile = nutritionists.get(position);
//        if (currentProfile instanceof Nutritionist) {
//            viewHolder.usernameTextView.setText(((Nutritionist) currentProfile).getEducation());
//        }
        String size = String.valueOf(nutritionists.size());
        viewHolder.expertiseTextView.setText(currentProfile.getExpertise());
        String fullName = currentProfile.getFirstName() + " " + currentProfile.getLastName();
        viewHolder.fullnameTextView.setText(fullName);
        viewHolder.bioTextView.setText(currentProfile.getBio());
        viewHolder.educationTextView.setText(currentProfile.getEducation());

        return convertView;
    }

    // ViewHolder class to improve performance
    static class ViewHolder {
        TextView expertiseTextView;
        TextView bioTextView;
        TextView fullnameTextView;
        TextView educationTextView;
    }
}