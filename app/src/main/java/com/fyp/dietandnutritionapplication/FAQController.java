package com.fyp.dietandnutritionapplication;

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

        TextView questionTextView = convertView.findViewById(R.id.faqQuestion);
        TextView dateCreatedTextView = convertView.findViewById(R.id.faqDateCreated);
        TextView faqAnswerTextView = convertView.findViewById(R.id.faqAnswer);

        String answerTextView = "Your long text here...";
        int maxLength = 100; // Set your max character limit
        if (answerTextView.length() > maxLength) {
            answerTextView = answerTextView.substring(0, maxLength) + "...";
        }
        FAQ faq = getItem(position);

        // Set review details
        if (faq != null) {
            questionTextView.setText(faq.getQuestion());

            // Handle answer text
            String answer = faq.getAnswer();
            if (answer.length() > 100) {
                answer = answer.substring(0, 100) + "...";
            }
            faqAnswerTextView.setText(answer);

            // Set date created
            dateCreatedTextView.setText(faq.getDateCreated());
        }

        return convertView;
    }
}
