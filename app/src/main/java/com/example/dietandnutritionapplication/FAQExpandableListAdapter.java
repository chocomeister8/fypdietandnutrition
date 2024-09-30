package com.example.dietandnutritionapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.HashMap;
import java.util.List;

public class FAQExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context; // Add context variable
    private List<String> listGroupTitles;
    private HashMap<String, List<String>> listChildItems;

    public FAQExpandableListAdapter(Context context, List<String> listGroupTitles, HashMap<String, List<String>> listChildItems) {
        this.context = context; // Initialize the context
        this.listGroupTitles = listGroupTitles;
        this.listChildItems = listChildItems;
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
        // Inflate or return the view for the group (question)
        // TODO: Implement the actual view layout for group
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Inflate or return the view for the child (answer)
        // TODO: Implement the actual view layout for child
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
