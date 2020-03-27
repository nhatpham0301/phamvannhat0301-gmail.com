package com.example.serverside.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.serverside.Common.Common;
import com.example.serverside.Model.User;
import com.example.serverside.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {

    Button btnSignIn;
    MaterialEditText editTextPhone, editTextPassword;
    ProgressDialog progress;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        addControl();
        addEvent();
    }

    private void addEvent() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setMessage("Please waiting...");
                progress.show();
                final String phone = editTextPhone.getText().toString();
                final String password = editTextPassword.getText().toString();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(phone).exists()) {
                            User user = dataSnapshot.child(phone).getValue(User.class);
                            user.setPhone(phone);
                            if (Boolean.parseBoolean(user.getIsStaff())) {
                                if (user.getPassword().equals(password)) {
                                    progress.dismiss();
                                    Common.currentUser = user;
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progress.dismiss();
                                    Toast.makeText(SignIn.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progress.dismiss();
                                Toast.makeText(SignIn.this, "Please login with Staff account", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progress.dismiss();
                            Toast.makeText(SignIn.this, "User not exist in database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void addControl() {
        btnSignIn = findViewById(R.id.btnSignIn_SignIn);
        editTextPhone = findViewById(R.id.editTextPhoneNumberSignIn);
        editTextPassword = findViewById(R.id.editTextPasswordSignIn);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        progress = new ProgressDialog(SignIn.this);
    }
}
