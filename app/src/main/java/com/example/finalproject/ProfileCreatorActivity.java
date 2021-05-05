package com.example.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;


public class ProfileCreatorActivity extends AppCompatActivity {
    EditText fName;
    EditText lname;
    EditText mline;
    Button upload;
    Button chooser;
    final static int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView image1;
    // view for image view


    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Doctors"); //start from root
    StorageReference storageReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creator);
        fName = findViewById(R.id.editTextFirstName);
        lname = findViewById(R.id.editTextLastName);
        mline = findViewById(R.id.editTextMultiLine);
        upload = findViewById(R.id.bupload);
        chooser = findViewById(R.id.bchooser);
        image1 = findViewById(R.id.imageView1);

        String fName1 = fName.getText().toString();
        String lName1 = lname.getText().toString();
        String mline1 = lname.getText().toString();
        chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fName1.isEmpty() && lName1.isEmpty() && mline1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "First Name, LastName, and specialization cannot be empty", Toast.LENGTH_SHORT);
                } else if (fName1.isEmpty() && lName1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "First Name, and LastName cannot be empty", Toast.LENGTH_SHORT);
                } else if (fName1.isEmpty() && mline1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "First Name, and specialization cannot be empty", Toast.LENGTH_SHORT);
                } else if (lName1.isEmpty() && mline1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LastName, and specialization cannot be empty", Toast.LENGTH_SHORT);
                } else if (fName1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "First Name cannot be empty", Toast.LENGTH_SHORT);
                } else if (lName1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), " LastName cannot be empty", Toast.LENGTH_SHORT);
                } else if (mline1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "specialization cannot be empty", Toast.LENGTH_SHORT);
                } else {
                    HashMap<String, String> doctors = new HashMap<>();
                    doctors.put("fname", fName1);
                    doctors.put("lname", lName1);
                    doctors.put("specialization", mline1);
                    myRef.push().setValue(doctors);
                    uploadImage();
                    Intent docDashboard = new Intent(getApplicationContext(),DoctorDashboardActivity.class);
                    startActivity(docDashboard);
                }
            }
        });
    }

    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                image1.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + "file1"); //UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(getApplicationContext(),
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getApplicationContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }

}
