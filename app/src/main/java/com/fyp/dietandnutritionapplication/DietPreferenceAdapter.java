package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DietPreferenceAdapter extends ArrayAdapter<DietPreference> {
    private Context context;
    private List<DietPreference> dietPreferenceList;

    public DietPreferenceAdapter(Context context, List<DietPreference> dietPreferenceList) {
        super(context, 0, dietPreferenceList);
        this.context = context;
        this.dietPreferenceList = dietPreferenceList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dietpreference_item, parent, false);
        }

        DietPreference dietPreference = dietPreferenceList.get(position);
        TextView dietpreferenceTextView = convertView.findViewById(R.id.dietpreference);

        dietpreferenceTextView.setText(dietPreference.getDietPreference());


        return convertView;
    }
}