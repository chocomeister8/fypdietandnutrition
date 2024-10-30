package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AllergyOptionsAdapter extends ArrayAdapter<AllergyOptions> {
    private Context context;
    private List<AllergyOptions> allergyOptionsList;

    public AllergyOptionsAdapter(Context context, List<AllergyOptions> allergyOptionsList) {
        super(context, 0, allergyOptionsList);
        this.context = context;
        this.allergyOptionsList = allergyOptionsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.allergyoption_item, parent, false);
        }

        AllergyOptions allergyOptions = allergyOptionsList.get(position);
        TextView allergyOptionsTextView = convertView.findViewById(R.id.allergyoption);

        allergyOptionsTextView.setText(allergyOptions.getAllergyOption());

        return convertView;
    }
}