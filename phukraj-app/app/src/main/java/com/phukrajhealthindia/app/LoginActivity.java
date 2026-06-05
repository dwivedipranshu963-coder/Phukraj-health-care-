package com.phukrajhealthindia.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etCity;
    private TextInputLayout tilName, tilPhone, tilCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName  = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etCity  = findViewById(R.id.et_city);
        tilName  = findViewById(R.id.til_name);
        tilPhone = findViewById(R.id.til_phone);
        tilCity  = findViewById(R.id.til_city);

        Button btnStart = findViewById(R.id.btn_login);
        btnStart.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String name  = etName.getText() != null  ? etName.getText().toString().trim()  : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String city  = etCity.getText() != null  ? etCity.getText().toString().trim()  : "";

        tilName.setError(null);
        tilPhone.setError(null);
        tilCity.setError(null);

        if (TextUtils.isEmpty(name) || name.length() < 2) {
            tilName.setError("Enter your full name");
            return;
        }
        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            tilPhone.setError("Enter valid 10-digit phone number");
            return;
        }
        if (TextUtils.isEmpty(city)) {
            tilCity.setError("Enter your city");
            return;
        }

        SharedPreferences prefs = getSharedPreferences("phukraj_prefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("user_name", name)
                .putString("user_phone", phone)
                .putString("user_city", city)
                .apply();

        Toast.makeText(this, "Welcome, " + name + "! 🌿", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
