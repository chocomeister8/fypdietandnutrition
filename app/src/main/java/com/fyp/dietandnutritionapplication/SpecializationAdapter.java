package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpecializationAdapter extends ArrayAdapter<Specialization> {
    private Context context;
    private List<Specialization> specList;

    public SpecializationAdapter(Context context, List<Specialization> specList) {
        super(context, 0, specList);
        this.context = context;
        this.specList = specList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.specialization_item, parent, false);
        }

        Specialization currentspec = specList.get(position);
        TextView nameTextView = convertView.findViewById(R.id.specialization);

        nameTextView.setText(currentspec.getName());


        return convertView;
    }
}