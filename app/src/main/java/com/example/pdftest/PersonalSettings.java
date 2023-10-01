package com.example.pdftest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class PersonalSettings extends AppCompatActivity {

    String customerName;
    TextInputEditText txtUserName;
    TextInputEditText txtStreet;
    TextInputEditText txtCity;
    TextInputEditText txtCountry;
    TextInputEditText txtICO;
    TextInputEditText txtDIC;
    TextInputEditText txtIBAN;
    TextInputEditText txtBankAcc;
    TextInputEditText txtSWIFT;
    EditText txtEmail;
    EditText txtPassword;
    String userName;
    String iban;
    String bankAcc;
    String swift;
    String street;
    String city;
    String country;
    String ico;
    String dic;
    String email;
    String password;
    Button btnSaveInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        String retrievedUser = sharedPreferences.getString("User", "Not set yet");
        txtUserName = findViewById(R.id.edit_user_name);
        txtUserName.setHint(retrievedUser);
        String retrievedStreet = sharedPreferences.getString("Street", "Not set yet");
        txtStreet = findViewById(R.id.edit_street_name);
        txtStreet.setHint(retrievedStreet);
        String retrievedCity = sharedPreferences.getString("City", "Not set yet");
        txtCity = findViewById(R.id.edit_city);
        txtCity.setHint(retrievedCity);
        String retrievedCountry = sharedPreferences.getString("Country", "Not set yet");
        txtCountry = findViewById(R.id.edit_country);
        txtCountry.setHint(retrievedCountry);
        String retrievedICO = sharedPreferences.getString("ICO", "Not set yet");
        txtICO = findViewById(R.id.edit_ico);
        txtICO.setHint(retrievedICO);
        String retrievedDIC = sharedPreferences.getString("DIC", "Not set yet");
        txtDIC = findViewById(R.id.edit_dic);
        txtDIC.setHint(retrievedDIC);
        String retrievedBankAcc = sharedPreferences.getString("BankAcc", "Not set yet");
        txtBankAcc = findViewById(R.id.edit_bank_acc);
        txtBankAcc.setHint(retrievedBankAcc);
        String retrievedIBAN = sharedPreferences.getString("IBAN", "Not set yet");
        txtIBAN = findViewById(R.id.edit_iban);
        txtIBAN.setHint(retrievedIBAN);
        String retrievedSWIFT = sharedPreferences.getString("SWIFT", "Not set yet");
        txtSWIFT = findViewById(R.id.edit_swift);
        txtSWIFT.setHint(retrievedSWIFT);


        btnSaveInfo = findViewById(R.id.btn_save_info);
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
                leaveLayout(v);
            }
        });
    }

    // Leaving current layout
    public void leaveLayout(View v) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    // Saving user personal information
    private void saveUserInfo() {
        userName = txtUserName.getText().toString();
        street = txtStreet.getText().toString();
        city = txtCity.getText().toString();
        country = txtCountry.getText().toString();
        ico = txtICO.getText().toString();
        dic = txtDIC.getText().toString();
        bankAcc = txtBankAcc.getText().toString();
        iban = txtIBAN.getText().toString();
        swift = txtSWIFT.getText().toString();
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (userName != null && !userName.isEmpty()) {
            editor.putString("User", userName);
        }
        if (street != null && !street.isEmpty()) {
            editor.putString("Street", street);
        }
        if (city != null && !city.isEmpty()) {
            editor.putString("City", city);
        }
        if (country != null && !country.isEmpty()) {
            editor.putString("Country", country);
        }
        if (ico != null && !ico.isEmpty()) {
            editor.putString("ICO", ico);
        }
        if (dic != null && !dic.isEmpty()) {
            editor.putString("DIC", dic);
        }
        if (bankAcc != null && !bankAcc.isEmpty()) {
            editor.putString("BankAcc", bankAcc);
        }
        if (iban != null && !iban.isEmpty()) {
            editor.putString("IBAN", iban);
        }
        if (swift != null && !swift.isEmpty()) {
            editor.putString("SWIFT", swift);
        }
        if (email != null && !email.isEmpty()) {
            editor.putString("SenderEmail", email);
        }
        if (password != null && !password.isEmpty()) {
            editor.putString("Password", password);
        }

        editor.apply();
    }
}