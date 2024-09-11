package com.example.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class FAQController extends ArrayAdapter<FAQ> {
    public FAQController(Context context, List<FAQ> reviews) {
        super(context, 0, reviews);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.faq_item, parent, false);
        }
        TextView titleTextView = convertView.findViewById(R.id.faqTitle);
        TextView questionTextView = convertView.findViewById(R.id.faqQuestion);
        TextView answerTextView = convertView.findViewById(R.id.faqAnswer);
        TextView dateCreatedTextView = convertView.findViewById(R.id.faqDateCreated);

        // Set review details
        FAQ faq = getItem(position);

        titleTextView.setText(faq.getTitle());
        questionTextView.setText(faq.getQuestion());
        answerTextView.setText(faq.getAnswer());
        dateCreatedTextView.setText(faq.getDateCreated());

        // Format date and username
        String formattedDateTime = String.format(faq.getDateCreated());
        dateCreatedTextView.setText(formattedDateTime);


        return convertView;
    }
}
