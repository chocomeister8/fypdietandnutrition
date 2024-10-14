package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FAQAdapter extends ArrayAdapter<FAQ> {
    private Context context;
    private List<FAQ> faqList;

    public FAQAdapter(Context context, List<FAQ> faqList) {
        super(context, 0, faqList);
        this.context = context;
        this.faqList = faqList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.faq_item, parent, false);
        }

        FAQ currentFAQ = faqList.get(position);
        TextView categoryTextView = convertView.findViewById(R.id.faqCategory);
        TextView questionTextView = convertView.findViewById(R.id.faqQuestion);
        TextView answerTextView = convertView.findViewById(R.id.faqAnswer);
        TextView dateCreatedTextView = convertView.findViewById(R.id.faqDateCreated);

        categoryTextView.setText(currentFAQ.getCategory());
        questionTextView.setText(currentFAQ.getQuestion());
        answerTextView.setText(currentFAQ.getAnswer());
        dateCreatedTextView.setText(currentFAQ.getDateCreated());

        return convertView;
    }
}