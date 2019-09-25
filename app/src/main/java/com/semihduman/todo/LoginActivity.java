package com.semihduman.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import helpers.adapters.Helper;
import helpers.adapters.SessionSharedPreferences;

public class LoginActivity extends AppCompatActivity {
    EditText username,password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressBar mProgressBar;
   // private SessionSharedPreferences session;
    RelativeLayout rl ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.userNameLoginEdt);
        password = findViewById(R.id.passwordLoginEdt);
        rl = findViewById(R.id.loginRl);
        mProgressBar = findViewById(R.id.pbLogin);
        mProgressBar.setVisibility(View.GONE);
        //session =  new SessionSharedPreferences(getApplicationContext());
       // System.out.println("Session Id : "+ session.getUuid());
        setupFirebaseAuth();
        if (mAuth.getCurrentUser() != null  ){
            Intent i =  new Intent(this,MainActivity.class);
            startActivity(i);
        }
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        System.out.println("firebase User Current : " + mAuth);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    System.out.println("onAuthStateChanged:signed in" + user.getUid());
                    Intent i =  new Intent(LoginActivity.this,MainActivity.class);
                   // session.setUuid(user.getUid());
                    startActivity(i);
                } else {
                    System.out.println( "onAuthStateChanged: sign_out");
                }
            }
        };

    }
    public void login(View view){
        if ( username.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            Helper.showToast("Lütfen Tüm alanları doldurunuz",this);
        }else{
            mProgressBar.setVisibility(View.VISIBLE);
            //rl.setBackgroundResource(R.color.transparent);
            //mProgressBar.setBackgroundResource(R.color.transparent);
            mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())

                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mProgressBar.setVisibility(View.GONE);

                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "Giriş Başarılı.", Toast.LENGTH_LONG).show();
                                //session.setUuid(mAuth.getUid());
                                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(i);
                            } else {

                                Toast.makeText(LoginActivity.this, "Giriş Başarısız oldu", Toast.LENGTH_LONG).show();
                                mProgressBar.setVisibility(View.GONE);
                            }

                        }
                    });
        }
    }
    public  void register(View view){
        Intent i =  new Intent(this,Registerctivity.class);
        startActivity(i);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }
}
