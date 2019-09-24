package com.semihduman.todo;

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

public class Registerctivity extends AppCompatActivity {

    EditText password,email;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerctivity);


        password = findViewById(R.id.passwordRegisterEdt);
        email = findViewById(R.id.emailRegisterEdt);

        mAuth = FirebaseAuth.getInstance();
    }

    public  void  save(View view){

        if (email.getText().toString().equals(" ") || password.getText().toString().equals(" ")){
            Helper.showToast("Lütfen e-mail ve şifre alanlarını boş bırakmayınız",this);
        }else if(password.getText().toString().length() < 6){
            Helper.showToast("Şifreniz 6 karakterden fazla olmadılıdır",this);
        }else{

            mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(Registerctivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent i = new Intent(Registerctivity.this,MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                Helper.showToast(task.getException().getMessage(),Registerctivity.this);
                            }
                        }
                    });

        }
    }
}
