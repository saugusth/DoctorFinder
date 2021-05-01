package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    EditText Eusername;
    EditText Epassword;
    Button signup;
    CheckBox doctorCheckbox;
    CheckBox patientCheckbox;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("users");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_signup);
        Eusername = findViewById(R.id.editTextUsername);
        Epassword = findViewById(R.id.editTextPassword);
        doctorCheckbox = findViewById(R.id.doctorCheckBox);
        patientCheckbox = findViewById(R.id.patientCheckBox);
        signup = findViewById(R.id.bsignup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Eusername.getText().toString();
                String password = Epassword.getText().toString();
                String group;


                if(username.isEmpty() && password.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "username and password cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if (username.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "username cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if (password.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "password cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<String, String> loginMap = new HashMap<>();
                    if(doctorCheckbox.isChecked()){
                        group = "doctor";
                        loginMap.put("counter", "false");
                    }
                    else{
                        group = "patient";
                    }
                    loginMap.put("username", username);
                    loginMap.put("password", password);
                    loginMap.put("group", group);

                    myRef.push().setValue(loginMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RegistrationActivity.this, "user has been added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent login = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(login);
                }

            }
        });
    }
}
