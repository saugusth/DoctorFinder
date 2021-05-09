package com.example.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoctorActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;
    StorageReference storageReference;
    FirebaseStorage storage;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Doctors");
    ImageView image1;
    Map<String, Object> map = null;
    TextView name;
    TextView specialization;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        name = findViewById(R.id.textName);
        specialization = findViewById(R.id.texts);


        // [START storage_field_initialization]
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageReference = storage.getReference();
        Uri uri = Uri.parse(myRef.child("imageUrI").get().toString());

        Intent intent = getIntent();
        String doctorType = intent.getStringExtra("Symptom");

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore
                    .Images
                    .Media
                    .getBitmap (
                            getContentResolver(),
                            uri);
        } catch (IOException e) {
            e.printStackTrace();
        }


        image1.setImageBitmap(bitmap);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    map = (Map<String, Object>) child.getValue();
                    String a = (String) map.get("specialization");
                    String b = (String) map.get("fname");
                    String c = (String) map.get("lname");

                    if (doctorType.compareTo("tummy trouble") == 0 && a.compareTo("gastroenterologist") == 0){
                        specialization.setText(a);
                        name.setText("Dr. " + b + " " + c);
                        return;
                    }
                    else if (doctorType.compareTo("physical") == 0 && a.compareTo("family") == 0){
                        specialization.setText(a);
                        name.setText("Dr. " + b + " " + c);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
}
