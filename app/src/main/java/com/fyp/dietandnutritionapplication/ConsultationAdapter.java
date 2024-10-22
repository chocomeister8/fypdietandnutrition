package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    FirebaseUser currentUser = auth.getCurrentUser();

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
        Consultation currentConsultation = consultationList.get(position);

        // Set data to the views
        viewHolder.consultationIdTextView.setText(currentConsultation.getConsultationId());
        viewHolder.nutritionistNameTextView.setText(currentConsultation.getNutritionistName());
        viewHolder.clientNameTextView.setText(currentConsultation.getClientName());
        viewHolder.dateTextView.setText(currentConsultation.getDate());
        viewHolder.timeTextView.setText(currentConsultation.getTime());
        viewHolder.statusTextView.setText(currentConsultation.getStatus());

        viewHolder.bookButton.setOnClickListener(v -> {
            // Ensure user is logged in
            if (currentUser != null) {
                String userId = currentUser.getUid();
                String clientName = currentUser.getDisplayName(); // Get logged-in user's name

                // Get consultation ID from current consultation object
                String consultationId = currentConsultation.getConsultationId();

                // Fetch the consultation slot details from Firestore based on the consultationId
                firestore.collection("Consultation_slots").document(consultationId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Fetch necessary details from the consultation slot
                                String nutritionistName = documentSnapshot.getString("nutritionistName");
                                String date = documentSnapshot.getString("date");
                                String time = documentSnapshot.getString("time");
                                String status = "pending"; // You can set the default status to "pending"

                                // Create a new Consultation object for booking
                                Consultation booking = new Consultation(consultationId, nutritionistName, clientName, date, time, status);

                                // Save the booking to the "Bookings" collection in Firestore
                                firestore.collection("Bookings")
                                        .document(consultationId) // Using consultationId as the document ID
                                        .set(booking) // Saving the Booking object
                                        .addOnSuccessListener(aVoid -> {
                                            // Show success message
                                            Toast.makeText(context, "Consultation booked with " + nutritionistName, Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Show error message
                                            Toast.makeText(context, "Failed to book consultation: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            } else {
                                // Consultation slot does not exist
                                Toast.makeText(context, "Consultation slot not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle error in fetching consultation slot
                            Toast.makeText(context, "Error fetching consultation slot: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
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