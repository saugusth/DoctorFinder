package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button login;
    public static ArrayList<Map<String, Object>> userList = null;
    Map<String, Object> map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(); //start from root
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        login = findViewById(R.id.blogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList = new ArrayList<Map<String, Object>>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            //assign data to map (equivalent to a put)
                            map = (Map<String, Object>) child.getValue();

                            //grab individualized map values
                            String a = (String) map.get("username");
                            String b = (String) map.get("password");
                            if (a.compareTo(username.getText().toString())== 0 && b.compareTo(password.getText().toString()) == 0){
                                Toast.makeText(MainActivity.this, "login was successfully", Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(MainActivity.this,Dashboard.class);
                                startActivity(login);
                            }
                        }
                        Toast.makeText(MainActivity.this, "login was not successfully. Try again!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}