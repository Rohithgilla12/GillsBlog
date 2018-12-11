package com.dude.rohithgilla.gillsblog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri acoountImageUri = null;

    private EditText setupNameText;
    private EditText setupDobText;
    private Button setupBtn;
    private ProgressBar setupBar;
    private String user_id;
    private Boolean isChanged = false;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = firebaseAuth.getCurrentUser().getUid();

        setupImage = findViewById(R.id.activityImage);
        setupNameText = findViewById(R.id.setupName);
        setupDobText = findViewById(R.id.setupDob);
        setupBtn = findViewById(R.id.setupButton);
        setupBar = findViewById(R.id.accountProgress);

        setupBtn.setEnabled(false);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    if(task.getResult().exists()){
                     Toast.makeText(SetupActivity.this,"Data exists",Toast.LENGTH_LONG).show();
                     String name = task.getResult().getString("Name");
                     String image = task.getResult().getString("Image");
                     String dob = task.getResult().getString("DOB");

                     RequestOptions placeholdereq = new RequestOptions();
                     placeholdereq.placeholder(R.drawable.default_image);

                     setupNameText.setText(name);
                     Glide.with(SetupActivity.this).load(image).into(setupImage);
                     setupDobText.setText(dob);
                     acoountImageUri = Uri.parse(image);

                    }
                    else {
                    Toast.makeText(SetupActivity.this,"Data not exists",Toast.LENGTH_LONG).show();
                    }

                }
                else {
                    String errorMesage = task.getException().toString();
                    Toast.makeText(SetupActivity.this, errorMesage,Toast.LENGTH_LONG).show();
                }
                setupBtn.setEnabled(true);
            }
        });


        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChanged) {
                    final String userName = setupNameText.getText().toString();
                    final String dob = setupDobText.getText().toString();
                    if (!TextUtils.isEmpty(userName) && acoountImageUri != null) {
                        setupBar.setVisibility(View.VISIBLE);
                        StorageReference image_path = storageReference.child("profilePictures").child(user_id + ".jpg");
                        image_path.putFile(acoountImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {
                                    storeFireStore(task, userName, dob);
                                } else {
                                    String errorMesage = task.getException().toString();
                                    Toast.makeText(SetupActivity.this, errorMesage, Toast.LENGTH_LONG).show();
                                    setupBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });

                    }
                }
                else{
                    final String userName = setupNameText.getText().toString();
                    final String dob = setupDobText.getText().toString();
                    storeFireStore(null,userName,dob);
                }
            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(SetupActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else{
                        Toast.makeText(SetupActivity.this,"Permission Granted",Toast.LENGTH_LONG).show();
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(SetupActivity.this);
                    }
                }

            }
        });

    }

    private void storeFireStore(Task<UploadTask.TaskSnapshot> task, String userName,String dob) {
        Uri download_uri;
        if(task !=null){
            download_uri = task.getResult().getDownloadUrl();
        }
        else {
            download_uri=acoountImageUri;
        }
        Map <String,String> userMap = new HashMap<>();
        userMap.put("Name",userName);
        userMap.put("DOB",dob);
        userMap.put("Image",download_uri.toString());
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SetupActivity.this, "Details are updated!",Toast.LENGTH_LONG).show();
                    Intent mainActivity = new Intent(SetupActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                }
                else {
                    String errorMesage = task.getException().toString();
                    Toast.makeText(SetupActivity.this, errorMesage,Toast.LENGTH_LONG).show();

                }

            }
        });
        Toast.makeText(SetupActivity.this, "Uploaded Dude!",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                acoountImageUri = result.getUri();
                setupImage.setImageURI(acoountImageUri);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SetupActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }
}
