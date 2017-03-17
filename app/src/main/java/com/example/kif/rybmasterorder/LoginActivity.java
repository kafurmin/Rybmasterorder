package com.example.kif.rybmasterorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String ZIPCODE = "1111";//TODO перенести в firebase
    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextZipCode;

    private Button buttonSignin;
    private Button buttonNewRegister;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null){
            // profile activity
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextZipCode = (EditText) findViewById(R.id.editTextZipCode);

        buttonSignin = (Button) findViewById(R.id.buttonSignin);
        buttonNewRegister = (Button) findViewById(R.id.buttonNewRegister);

        progressDialog = new ProgressDialog(this);
        buttonSignin.setOnClickListener(this);
        buttonNewRegister.setOnClickListener(this);
    }

    private void registerUser(){

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        final String zipcode  = editTextZipCode.getText().toString().trim();
        //checking if email and passwords are empty

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(zipcode)) {
            Toast.makeText(this, "Please enter zip code", Toast.LENGTH_LONG).show();
            return;
        }

            //if the email and password are not empty
            //displaying a progress dialog

         progressDialog.setMessage("Registering Please Wait...");
         progressDialog.show();

         //creating a new user
         firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //checking if success
                            if(task.isSuccessful()){
                                if(zipcode.equals(ZIPCODE)) {
                                    Toast.makeText(LoginActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                                else {Toast.makeText(LoginActivity.this, "Zip Code Error", Toast.LENGTH_LONG).show();   }
                            }else{
                                //display some message here
                                Toast.makeText(LoginActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
    }

    public void userLogin(){
       //getting email and password from edit texts
       String email = editTextEmail.getText().toString().trim();
       String password  = editTextPassword.getText().toString().trim();
       final String zipcode  = editTextZipCode.getText().toString().trim();

       //checking if email and passwords are empty
       if(TextUtils.isEmpty(email)){
           Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
           return;
       }

       if(TextUtils.isEmpty(password)) {
           Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
           return;
       }
       if(TextUtils.isEmpty(zipcode)) {
           Toast.makeText(this, "Please enter zip code", Toast.LENGTH_LONG).show();
           return;
       }

       //if the email and password are not empty
       //displaying a progress dialog

       progressDialog.setMessage("Registering Please Wait...");
       progressDialog.show();



       //signIn
       firebaseAuth.signInWithEmailAndPassword(email, password)
               .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       progressDialog.dismiss();
                       //checking if success
                       if(task.isSuccessful()){
                           if(zipcode.equals(ZIPCODE)){
                           Toast.makeText(LoginActivity.this,"Successfully sign in",Toast.LENGTH_LONG).show();
                           finish();
                           startActivity(new Intent(getApplicationContext(), MainActivity.class));
                           }else{
                               Toast.makeText(LoginActivity.this, "Zip Code Error", Toast.LENGTH_LONG).show();
                           }
                       }else{
                           //display some message here
                           Toast.makeText(LoginActivity.this,"Sign in Error",Toast.LENGTH_LONG).show();
                       }
                   }
               });
    }
    @Override
    public void onClick(View view) {
        //calling register method on click
       switch (view.getId()){

           case R.id.buttonSignin:
               if(!isNetworkConnected()){
                   Toast.makeText(this, "Нет интернета. Вход невозможен", Toast.LENGTH_LONG).show();
               }else  userLogin();
               break;
           case R.id.buttonNewRegister:
               if(!isNetworkConnected()){
                   Toast.makeText(this, "Нет интернета. Регистрация невозможна", Toast.LENGTH_LONG).show();
               }else registerUser();
               break;
       }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
