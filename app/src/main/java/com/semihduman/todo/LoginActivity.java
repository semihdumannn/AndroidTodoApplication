package com.semihduman.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import helpers.adapters.Helper;

public class LoginActivity extends AppCompatActivity {
    EditText username,password;
   private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.userNameLoginEdt);
        password = findViewById(R.id.passwordLoginEdt);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            Intent i =  new Intent(this,MainActivity.class);
            startActivity(i);
        }
    }

    public void login(View view){
        if (username.getText().toString().equals(" ") || password.getText().toString().equals(" ")){
            Helper.showToast("Lütfen Tüm alanları doldurunuz",this);
        }else{
            mAuth.signInWithEmailAndPassword(username.getText().toString(),password.getText().toString()).addOnCompleteListener(LoginActivity.this,
                    new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(i);

                            }
                            else{
                                // hata
                                Helper.showToast(task.getException().getMessage(), LoginActivity.this);
                            }
                        }
                    });
        }
    }

    public  void register(View view){
        Intent i =  new Intent(this,Registerctivity.class);
        startActivity(i);
    }
}
