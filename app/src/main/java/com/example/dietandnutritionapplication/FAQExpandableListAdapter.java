package com.example.dietandnutritionapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class FAQExpandableListAdapter extends BaseExpandableListAdapter {

    private List<String> listGroupTitles;
    private HashMap<String, List<String>> listChildItems; // Questions
    private HashMap<String, List<String>> listChildAnswers; // Answers

    public FAQExpandableListAdapter(List<String> listGroupTitles, HashMap<String, List<String>> listChildItems, HashMap<String, List<String>> listChildAnswers) {
        this.listGroupTitles = listGroupTitles;
        this.listChildItems = listChildItems;
        this.listChildAnswers = listChildAnswers;
    }

    @Override
    public int getGroupCount() {
        return listGroupTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChildItems.get(listGroupTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroupTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChildItems.get(listGroupTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_group_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.group_title);
        titleTextView.setText((String) getGroup(groupPosition));

        // Set the arrow indicator based on the expanded state
        ImageView arrowImageView = convertView.findViewById(R.id.group_arrow);
        arrowImageView.setImageResource(isExpanded ? android.R.drawable.arrow_down_float : android.R.drawable.arrow_up_float);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView questionTextView = convertView.findViewById(android.R.id.text1);
        TextView answerTextView = convertView.findViewById(android.R.id.text2);

        String question = listChildItems.get(listGroupTitles.get(groupPosition)).get(childPosition);
        String answer = listChildAnswers.get(listGroupTitles.get(groupPosition)).get(childPosition);

        questionTextView.setText(question);
        answerTextView.setText(answer);

        // Add padding for better spacing
        questionTextView.setPadding(16, 8, 16, 0); // Top padding for question
        answerTextView.setPadding(16, 4, 16, 8); // Top and bottom padding for answer

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
