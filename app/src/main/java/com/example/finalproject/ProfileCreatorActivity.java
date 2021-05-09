package com.example.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ProfileCreatorActivity extends AppCompatActivity {
    EditText fName;
    EditText lname;
    EditText mline;
    Button upload;
    Button chooser;
    final static int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView image1;
    String filename;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // private FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Doctors"); //start from root

    StorageReference storageReference;
    FirebaseStorage storage;
    FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creator);

        fName = findViewById(R.id.editTextFirstName);
        lname = findViewById(R.id.editTextLastName);
        mline = findViewById(R.id.editTextMultiLine);
        upload = findViewById(R.id.bupload);
        chooser = findViewById(R.id.bchooser);
        image1 = findViewById(R.id.imageView1);

        // [START storage_field_initialization]
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageReference = storage.getReference();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GET CURRENT USER INPUT
                String fName1 = fName.getText().toString();
                String lName1 = lname.getText().toString();
                String mline1 = mline.getText().toString();

                Log.d("fields ", fName1 + " " + lName1 + " " + mline1);

                Toast.makeText(getApplicationContext(), "specialization cannot be empty", Toast.LENGTH_LONG).show();

                  if (fName1.isEmpty() && lName1.isEmpty() && mline1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "First Name, LastName, and specialization cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (fName1.isEmpty() && lName1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "First Name, and LastName cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (fName1.isEmpty() && mline1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "First Name, and specialization cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (lName1.isEmpty() && mline1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LastName, and specialization cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (fName1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "First Name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (lName1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), " LastName cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (mline1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "specialization cannot be empty", Toast.LENGTH_SHORT).show();
                } else {


                HashMap<String, String> doctors = new HashMap<>();
                doctors.put("fname", fName1);
                doctors.put("lname", lName1);
                doctors.put("specialization", mline1);
                //myRef.push().setValue(doctors);
                myRef.push().setValue(doctors).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Toast.makeText(RegistrationActivity.this, "user has been added successfully", Toast.LENGTH_SHORT).show();
                        Log.d("recinsert ", "rec insrt");
                        uploadImage();
                        Intent docDashboard = new Intent(getApplicationContext(),DoctorDashboardActivity.class);
                        startActivity(docDashboard);

                    }
                });
                }  //end else
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
        Log.d("onActivity ", "onActivityResult fired");
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
            File file =new File(String.valueOf(filePath));
            filename=file.getName();
            Log.d("filename ", filename  );
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
        if(filePath!=null)     {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            /****** SAVE TO FB STORAGE ACCOUNT **************/
            storageReference = storageReference.child("images/" + UUID.randomUUID().toString());

            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                    Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_LONG) .show();
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progres = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progres+"%");
                        }
                    }) ;
            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                    Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT) .show();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //You will get donwload URL in uri - use that URL for your image
                            Log.d("dlurl", "Download URL = "+ uri.toString());
                            //Adding that URL to Realtime database
                            // myRef = database.getReference().child("image"); //start from root alternative?
                            myRef.child("imageUrl").setValue(uri.toString()); //insert into FB DB
                        }
                    });
                }
            });
        }
    }
    /*check authenticity of FB user -- ALLOW ANONYMOUS ENTRY*/
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

    }
    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
                Log.e("Success", "signInAnonymously:success");
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("Failure", "signInAnonymously:FAILURE", exception);
                    }
                });
    }
}
