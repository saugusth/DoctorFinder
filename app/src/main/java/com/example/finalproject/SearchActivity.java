package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class SearchActivity extends AppCompatActivity{
    String insuranceType;
    EditText Esymptom;
    String symptom;
    Button Search;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch);
        Esymptom = findViewById(R.id.editTextSymptom);
        Spinner spinner = findViewById(R.id.insuranceType);
        Search = findViewById(R.id.bsearch2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.insurance_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                insuranceType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                symptom = Esymptom.getText().toString();
                Intent DoctorPreview = new Intent(getApplicationContext(), DoctorActivity.class);
                DoctorPreview.putExtra("Insurance",insuranceType);
                DoctorPreview.putExtra("Symptom", symptom);
                startActivity(DoctorPreview);
            }
        });
    }
}
