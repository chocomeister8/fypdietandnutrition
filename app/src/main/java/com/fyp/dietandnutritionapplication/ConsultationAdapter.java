package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ConsultationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Consultation> consultationList;
    private LayoutInflater inflater;

    public ConsultationAdapter(Context context, ArrayList<Consultation> consultationList) {
        super();
        this.context = context;
        this.consultationList = consultationList; // Initialize with the provided list
        this.inflater = LayoutInflater.from(context); // Initialize inflater here
    }

    @Override
    public int getCount() {
        return consultationList.size();
    }

    @Override
    public Object getItem(int position) {
        return consultationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // Reuse the convertView if possible, to save memory
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.consultation_item, parent, false);
            viewHolder = new ViewHolder();
            // Initialize the views
            viewHolder.consultationIdTextView = convertView.findViewById(R.id.consultation_id);
            viewHolder.nutritionistNameTextView = convertView.findViewById(R.id.nutritionist_name);
            viewHolder.clientNameTextView = convertView.findViewById(R.id.client_name);
            viewHolder.dateTextView = convertView.findViewById(R.id.date);
            viewHolder.timeTextView = convertView.findViewById(R.id.time);
            viewHolder.statusTextView = convertView.findViewById(R.id.status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the Consultation object for the current position
        Consultation currentConsultation = consultationList.get(position);

        // Set data to the views
        viewHolder.consultationIdTextView.setText(currentConsultation.getConsultationId());
        viewHolder.nutritionistNameTextView.setText(currentConsultation.getNutritionistName());
        viewHolder.clientNameTextView.setText(currentConsultation.getClientName());
        viewHolder.dateTextView.setText(currentConsultation.getDate());
        viewHolder.timeTextView.setText(currentConsultation.getTime());
        viewHolder.statusTextView.setText(currentConsultation.getStatus());

        return convertView;
    }

    // Method to update the consultation list and notify the adapter
    public void setConsultationList(ArrayList<Consultation> consultations) {
        this.consultationList.clear(); // Clear the old data
        this.consultationList.addAll(consultations); // Add the new data
        notifyDataSetChanged(); // Notify that the data has changed
    }

    static class ViewHolder {
        TextView consultationIdTextView;
        TextView nutritionistNameTextView;
        TextView clientNameTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView statusTextView;
    }
}