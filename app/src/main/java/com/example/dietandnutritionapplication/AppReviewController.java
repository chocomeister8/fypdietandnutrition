package com.example.dietandnutritionapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class AppReviewController extends ArrayAdapter<AppRatingsReviews> {
    private final Context context;
    private final List<AppRatingsReviews> reviews;

    public AppReviewController(Context context, List<AppRatingsReviews> reviews) {
        super(context, R.layout.appreview_item, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.appreview_item, parent, false);
        }

        // Initialize views
        TextView titleTextView = convertView.findViewById(R.id.reviewTitleTextView);
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);
        TextView reviewTextView = convertView.findViewById(R.id.reviewTextView);
        TextView dateTimeTextView = convertView.findViewById(R.id.dateTimeTextView);

        // Set review details
        AppRatingsReviews review = getItem(position);
        titleTextView.setText(review.getTitle());
        ratingBar.setRating(review.getRating());
        reviewTextView.setText(review.getReview());

        // Format date and username
        String formattedDateTime = String.format("%s | %s", review.getDateTime(), review.getUsername());
        dateTimeTextView.setText(formattedDateTime);

        // Customize the RatingBar programmatically
        Drawable stars = ratingBar.getProgressDrawable();
        stars.setColorFilter(Color.parseColor("#FBDD70"), PorterDuff.Mode.SRC_ATOP); // Yellow color
        ratingBar.setScaleX(0.75f); // Adjust size
        ratingBar.setScaleY(0.75f); // Adjust size

        return convertView;
    }
}
