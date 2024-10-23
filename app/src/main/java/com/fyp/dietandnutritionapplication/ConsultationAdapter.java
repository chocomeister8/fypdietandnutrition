package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ConsultationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Consultation> consultationList;
    private LayoutInflater inflater;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    // Ensure currentUser is checked inside onClick method instead of being cached here
    // FirebaseUser currentUser = auth.getCurrentUser();

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
            convertView = inflater.inflate(R.layout.consultation_view_item, parent, false);
            viewHolder = new ViewHolder();
            // Initialize the views
            viewHolder.consultationIdTextView = convertView.findViewById(R.id.consultation_id);
            viewHolder.nutritionistNameTextView = convertView.findViewById(R.id.nutritionist_name);
            viewHolder.clientNameTextView = convertView.findViewById(R.id.client_name);
            viewHolder.dateTextView = convertView.findViewById(R.id.date);
            viewHolder.timeTextView = convertView.findViewById(R.id.time);
            viewHolder.statusTextView = convertView.findViewById(R.id.status);
            viewHolder.bookButton = convertView.findViewById(R.id.book_button); // Add the button

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the Consultation object for the current position
        Consultation currentConsultation = (Consultation) getItem(position);

        // Set data to the views
        viewHolder.consultationIdTextView.setText(currentConsultation.getConsultationId());
        viewHolder.nutritionistNameTextView.setText(currentConsultation.getNutritionistName());
        viewHolder.clientNameTextView.setText(currentConsultation.getClientName());
        viewHolder.dateTextView.setText(currentConsultation.getDate());
        viewHolder.timeTextView.setText(currentConsultation.getTime());
        viewHolder.statusTextView.setText(currentConsultation.getStatus());

        viewHolder.bookButton.setOnClickListener(v -> {
            // Fetch current user inside the onClick method
            FirebaseUser currentUser = auth.getCurrentUser();

            // Ensure user is logged in
            if (currentUser != null) {
                String userId = currentUser.getUid();
                String clientName = currentUser.getDisplayName(); // Get logged-in user's name

                // Get consultation ID from the current consultation object
                String consultationId = currentConsultation.getConsultationId();

                // Create a dialog to show confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Booking");

                // Add consultation cost and duration information to the message
                builder.setMessage("This consultation costs $150 for 50 minutes.\n\nAre you sure you want to book this consultation?");

                // Set up the Confirm button
                builder.setPositiveButton("Confirm Booking", (dialog, which) -> {
                    // Perform the booking action here
                    // Ensure the bookConsultation method is implemented
                    bookConsultation(userId, consultationId, clientName)
                            .addOnSuccessListener(aVoid -> {
                                // Handle successful booking
                                Toast.makeText(context, "Consultation booked successfully!", Toast.LENGTH_LONG).show();
                            })
                            .addOnFailureListener(e -> {
                                // Handle error in booking
                                Toast.makeText(context, "Error booking consultation: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                });

                // Set up the Cancel button
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                // User is not logged in
                Toast.makeText(context, "You need to log in to book a consultation.", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    // Method to update the consultation list and notify the adapter
    public void setConsultationList(ArrayList<Consultation> consultations) {
        this.consultationList.clear(); // Clear the old data
        this.consultationList.addAll(consultations); // Add the new data
        notifyDataSetChanged(); // Notify that the data has changed
    }

    // This is a placeholder for the actual booking method
    private Task<Void> bookConsultation(String userId, String consultationId, String clientName) {
        // Implement Firebase Firestore or booking logic here
        // Return Task<Void> or similar from Firebase or any other service
        return firestore.collection("consultations")
                .document(consultationId)
                .update("status", "booked", "clientName", clientName, "userId", userId);
    }

    static class ViewHolder {
        TextView consultationIdTextView;
        TextView nutritionistNameTextView;
        TextView clientNameTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView statusTextView;
        Button bookButton;
    }
}
