package com.example.pdftest;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class EmailSender extends AppCompatActivity {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private final String SMTP_USERNAME;
    private final String SMTP_PASSWORD;
    private final Context context; // Add a Context field

    public EmailSender(Context context, String smtpUsername, String smtpPassword) {
        //this.SMTP_HOST = smtpHost;
        //this.SMTP_PORT = smtpPort;
        this.context = context;
        this.SMTP_USERNAME = smtpUsername;
        this.SMTP_PASSWORD = smtpPassword;
    }

    public void sendEmail(String recipientEmail) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Faktura");
        intent.putExtra(Intent.EXTRA_TEXT, "Dobrý den");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }
}
