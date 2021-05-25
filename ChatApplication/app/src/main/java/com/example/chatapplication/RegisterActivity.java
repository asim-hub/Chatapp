package com.example.chatapplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.DataTruncation;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText userDate, passDate, emailDate;
    Button registerBut;
    // Firebase
    FirebaseAuth aut;
    DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initializare
        userDate = findViewById(R.id.userEditText);
        passDate = findViewById(R.id.editTextTextPassword);
        emailDate = findViewById(R.id.emailEditText);
        registerBut = findViewById(R.id.button);
        aut = FirebaseAuth.getInstance();

        // adding event to button register
        registerBut.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String username_text = userDate.getText().toString();
                String email_text  = emailDate.getText().toString();
                String pass_text  = passDate.getText().toString();

                // empty data register
                if (TextUtils.isEmpty(username_text) || TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text)) {
                    Toast.makeText(RegisterActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    RegisterNow(username_text, email_text, pass_text);
                }
            }
        });
    }

    private void RegisterNow(final String name, String email, String password) {
        aut.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = aut.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            ref = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
                            //HashMaps
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", name);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");

                            // Opening the Main Activity after Success Regist.
                            ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Invalid Username or Password or Email",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}