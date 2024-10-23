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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ConsultationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Consultation> consultationList;
    private LayoutInflater inflater;
    private FirebaseAuth auth;

    public ConsultationAdapter(Context context, ArrayList<Consultation> consultationList) {
        super();
        this.context = context;
        this.consultationList = consultationList;
        this.inflater = LayoutInflater.from(context);
        this.auth = FirebaseAuth.getInstance();
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

        // Reuse the convertView if possible
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.consultation_view_item, parent, false);
            viewHolder = new ViewHolder();
            // Initialize the views
            viewHolder.consultationIdTextView = convertView.findViewById(R.id.consultation_id);
            viewHolder.nutritionistNameTextView = convertView.findViewById(R.id.nutritionist_name);
            viewHolder.clientNameTextView = convertView.findViewById(R.id.client_name);
            viewHolder.dateTextView = convertView.findViewById(R.id.date);
            viewHolder.timeTextView = convertView.findViewById(R.id.time);
            viewHolder.priceTextView = convertView.findViewById(R.id.price);
            viewHolder.statusTextView = convertView.findViewById(R.id.status);
            viewHolder.bookButton = convertView.findViewById(R.id.book_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the Consultation object for the current position
        Consultation currentConsultation = (Consultation) getItem(position);

        // Check for null values and set data to the views
        if (currentConsultation != null) {
            viewHolder.consultationIdTextView.setText(currentConsultation.getConsultationId());
            viewHolder.nutritionistNameTextView.setText(currentConsultation.getNutritionistName());
            viewHolder.clientNameTextView.setText(currentConsultation.getClientName());
            viewHolder.dateTextView.setText(currentConsultation.getDate());
            viewHolder.timeTextView.setText(currentConsultation.getTime());
            viewHolder.priceTextView.setText("$150");
            viewHolder.statusTextView.setText(currentConsultation.getStatus());
        } else {
            Toast.makeText(context, "Consultation data is not available.", Toast.LENGTH_SHORT).show();
        }

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
                showConfirmationDialog(userId, consultationId, clientName);
            } else {
                // User is not logged in
                Toast.makeText(context, "You need to log in to book a consultation.", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    // Method to show confirmation dialog
    private void showConfirmationDialog(String userId, String consultationId, String clientName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Booking");
        builder.setMessage("This consultation costs $150 for 50 minutes.\n\nProceed to payment?");

        builder.setPositiveButton("Confirm Booking", (dialog, which) -> {
            // Redirect to the PaymentConsultationFragment without storing in Firestore
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                PaymentConsultationFragment paymentFragment = PaymentConsultationFragment.newInstance(consultationId);
                mainActivity.replaceFragment(paymentFragment);
                Toast.makeText(context, "Redirecting to payment...", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
        TextView priceTextView;
        Button bookButton;
    }
}
