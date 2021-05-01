package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button login;
    Button signup;
    public static ArrayList<Map<String, Object>> userList = null;
    Map<String, Object> map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(); //start from root
        username = findViewById(R.id.editTextUsername1);
        password = findViewById(R.id.editTextPassword1);
        login = findViewById(R.id.blogin);
        signup = findViewById(R.id.bsignup1);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList = new ArrayList<Map<String, Object>>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            //assign data to map (equivalent to a put)
                            map = (Map<String, Object>) child.getValue();
                            //grab individualized map values
                            String a = (String) map.get("username");
                            String b = (String) map.get("password");
                            String c = (String) map.get("group");
                            String d = (String) map.get("counter");

                            Log.d("username and password", "Username is " + a + " and password is " + b);
                            if (a.compareTo(username.getText().toString())== 0 && b.compareTo(password.getText().toString()) == 0){
                                String key = child.getKey();
                                if(c.compareTo("patient") == 0){
                                    Toast.makeText(MainActivity.this, "login was successfully", Toast.LENGTH_SHORT).show();
                                    Intent plogin = new Intent(MainActivity.this, DashboardActivity.class);
                                    startActivity(plogin);
                                    return;
                                }
                                else{
                                    if (d.compareTo("false") == 0){
                                        Toast.makeText(MainActivity.this, "login was successfully", Toast.LENGTH_SHORT).show();
                                        HashMap<String,Object> counterUpdate = new HashMap<>();
                                        counterUpdate.put("counter","true");
                                        myRef.child("users").child(key).updateChildren(counterUpdate);
                                        Intent dlogin1 = new Intent(MainActivity.this,ProfileCreatorActivity.class);
                                        startActivity(dlogin1);
                                        return;
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "login was successfully", Toast.LENGTH_SHORT).show();
                                        Intent dlogin2 = new Intent(MainActivity.this, DoctorDashboardActivity.class);
                                        startActivity(dlogin2);
                                        return;
                                    }
                                }
                            }
                        }
                        Toast.makeText(getApplicationContext(), "login was not successfully. Try again!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupPage = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(signupPage);
            }
        });
    }
}