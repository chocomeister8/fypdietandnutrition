package com.fyp.dietandnutritionapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BMIRecordAdapter extends RecyclerView.Adapter<BMIRecordAdapter.BMIViewHolder> {

    private List<BMIDetail> bmiList;

    public BMIRecordAdapter(List<BMIDetail> bmiList) {
        this.bmiList = bmiList;
    }

    @NonNull
    @Override
    public BMIViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bmi_past_record_item, parent, false);
        return new BMIViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BMIViewHolder holder, int position) {
        BMIDetail bmiDetail = bmiList.get(position);
        holder.bmiTextView.setText(String.format("BMI: %.1f", bmiDetail.getBmi()));
        holder.timestampTextView.setText(formatDate(bmiDetail.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return bmiList.size();
    }

    static class BMIViewHolder extends RecyclerView.ViewHolder {
        TextView bmiTextView;
        TextView timestampTextView;

        public BMIViewHolder(@NonNull View itemView) {
            super(itemView);
            bmiTextView = itemView.findViewById(R.id.bmi_value);
            timestampTextView = itemView.findViewById(R.id.timeStamp);
        }
    }

    private String formatDate(long timestamp) {
        // Create a Date object from the timestamp
        Date date = new Date(timestamp);
        // Define a date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Return the formatted date string
        return dateFormat.format(date);
    }
}
