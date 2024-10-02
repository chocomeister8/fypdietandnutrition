package com.example.dietandnutritionapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
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
        holder.bmiValue.setText(String.format("BMI: %.1f", bmiDetail.getBmi()));

        // Format timestamp to readable date
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(bmiDetail.getTimestamp());
        holder.timeStamp.setText(date);
    }

    @Override
    public int getItemCount() {
        return bmiList.size();
    }

    static class BMIViewHolder extends RecyclerView.ViewHolder {
        TextView bmiValue;
        TextView timeStamp;

        BMIViewHolder(View itemView) {
            super(itemView);
            bmiValue = itemView.findViewById(R.id.bmi_value);
            timeStamp = itemView.findViewById(R.id.timeStamp);
        }
    }
}
