package com.anshutiwari.baatchit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout mTilSignName;
    TextInputLayout mTilSignMobile;
    TextInputLayout mTilSignEmail;
    TextInputLayout mTilSignPass;
    TextInputLayout mTilAbout;
    Button mBtnSignUp;

    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mTilSignEmail = findViewById(R.id.til_signup_email);
        mTilSignMobile = findViewById(R.id.til_signup_phone);
        mTilSignName = findViewById(R.id.til_signup_name);
        mTilSignPass = findViewById(R.id.til_signup_password);
        mTilAbout = findViewById(R.id.til_about);
        mBtnSignUp = findViewById(R.id.btn_signup);

        mAuth = FirebaseAuth.getInstance();

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateFullName() | !validateEmail() | !validatePhoneNumber() | !validatePassword() | !validateAbout()){
                    return;
                }

                String email = mTilSignEmail.getEditText().getText().toString();
                String signUsername = mTilSignName.getEditText().getText().toString();
                String signPhone = mTilSignMobile.getEditText().getText().toString();
                String password = mTilSignPass.getEditText().getText().toString();
                String about = mTilAbout.getEditText().getText().toString();

                register(email,signPhone,password,signUsername,about);
            }
        });
    }

    private void register(String email, String mobile, String password, String username, String about) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        mAuth.createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                            long timestamp = (-1*System.currentTimeMillis());
                                            assert firebaseUser != null;
                                            String userId = firebaseUser.getUid();

                                            reference = database.getReference("User").child(userId);

                                            HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put("id",userId);
                                            hashMap.put("phone",mobile);
                                            hashMap.put("email",email);
                                            hashMap.put("username",username);
                                            hashMap.put("imageURL","default");
                                            hashMap.put("status","online");
                                            hashMap.put("about",about);
                                            hashMap.put("token",token);


                                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(SignUpActivity.this, "Database failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }else {
                                            Toast.makeText(SignUpActivity.this, "You can't register with this email or password" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });


    }
    public void moveToLogin(View view){
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Validation of fields
    private boolean validateFullName() {
        String val = mTilSignName.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            mTilSignName.setError("Field can not be empty");
            return false;
        } else {
            mTilSignName.setError(null);
            mTilSignName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAbout() {
        String val = mTilAbout.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            mTilAbout.setError("Field can not be empty");
            return false;
        } else {
            mTilAbout.setError(null);
            mTilAbout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = mTilSignEmail.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (val.isEmpty()) {
            mTilSignEmail.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            mTilSignEmail.setError("Invalid Email!");
            return false;
        } else {
            mTilSignEmail.setError(null);
            mTilSignEmail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = mTilSignPass.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[0-9])" +           //at least 1 digit
                "(?=.*[a-z])" +           //at least 1 lower case letter
                "(?=.*[A-Z])" +           //at least 1 upper case letter
//                "(?=.*[a-zA-Z])" +        //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
//                "(?=S+$)" +               //no white spaces
                ".{4,}" +                 //at least 4 characters
                "$";

        if (val.isEmpty()) {
            mTilSignPass.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkPassword)) {
            mTilSignPass.setError("*Password must contain min. 4 character. 1 uppercase and 1 lowercase,1 number at least");
            return false;
        } else {
            mTilSignPass.setError(null);
            mTilSignPass.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        String val = mTilSignMobile.getEditText().getText().toString().trim();
//        String checkspaces = "Aw{1,15}z";
        if (val.isEmpty()) {
            mTilSignMobile.setError("Enter valid phone number");
            return false;
        }
//        else if (!val.matches(checkspaces)) {
//            mTilPhone.setError("No White spaces are allowed!");
//            return false;
//        }
        else {
            mTilSignMobile.setError(null);
            mTilSignMobile.setErrorEnabled(false);
            return true;
        }
    }
}