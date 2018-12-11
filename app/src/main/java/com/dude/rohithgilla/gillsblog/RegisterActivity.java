package com.dude.rohithgilla.gillsblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText regEmail;
    private EditText regPass;
    private EditText regConfirmPass;
    private ProgressBar regProgressBar;
    private Button regBtn;
    private Button loginBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regEmail = findViewById(R.id.reg_email);
        regPass = findViewById(R.id.reg_password);
        regConfirmPass = findViewById(R.id.regConfirmPass);
        regBtn = findViewById(R.id.reg_btn);
        regProgressBar = findViewById(R.id.regProgress);
        loginBtn = findViewById(R.id.login_reg_btn);

        regProgressBar.setVisibility(View.INVISIBLE);


//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regEmailText = regEmail.getText().toString();
                String regPasswordText = regPass.getText().toString();
                String regConfirmPasswordText = regConfirmPass.getText().toString();

                if(!TextUtils.isEmpty(regEmailText) && !TextUtils.isEmpty(regConfirmPasswordText) && !TextUtils.isEmpty(regPasswordText)){
                    if(regConfirmPasswordText.equals(regPasswordText)){
                        regProgressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(regEmailText,regPasswordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();
                                }

                                else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                                }
                                regProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"Passwords doesn't match",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
