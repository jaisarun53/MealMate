package com.example.mealmate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SendMessageActivity extends AppCompatActivity {

    Button send;
    TextView editTextPhone, editTextMessage;
    String userMessage;
    String userNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarSendMessage);
        setSupportActionBar(toolbar);

        // Enable the back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Send Message");
        }

        // Handle back arrow click
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        send = findViewById(R.id.send_button);
        editTextPhone = findViewById(R.id.phone_number_edit_text);
        editTextMessage = findViewById(R.id.message_edit_text);

        // Get the ingredient message passed from ShopListActivity
        String ingredientMessage = getIntent().getStringExtra("ingredient_message");
        editTextMessage.setText(ingredientMessage != null ? ingredientMessage : "No ingredients found.");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMessage = editTextMessage.getText().toString();
                userNumber = editTextPhone.getText().toString();
                if (userNumber.isEmpty()) {
                    Toast.makeText(SendMessageActivity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                } else if (userMessage.isEmpty()) {
                    Toast.makeText(SendMessageActivity.this, "Message field is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    sendSMS(userMessage, userNumber);
                }
            }
        });
    }

    public void sendSMS(String userMessage, String userNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(userNumber, null, userMessage, null, null);
            Toast.makeText(this, "Message sent successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to the home page after sending the message
            navigateToHome();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(userNumber, null, userMessage, null, null);
            Toast.makeText(this, "Message sent successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to the home page after sending the message
            navigateToHome();
        } else {
            Toast.makeText(this, "Permission denied to send SMS!", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(SendMessageActivity.this, MainActivity.class); // Navigate to MainActivity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to it
    }
}