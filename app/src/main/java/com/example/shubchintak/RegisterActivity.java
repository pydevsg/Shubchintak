package com.example.shubchintak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;
    private Button mCreateBtn2;


    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    private Toolbar mToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);
        mCreateBtn2 = (Button) findViewById(R.id.reg_create_btn2);

        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();


        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                register_user(display_name, email, password);
            }
        });
        mCreateBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                register_user2(display_name, email, password);
            }
        });
    }

    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    String user_id=mAuth.getCurrentUser().getUid();
                    final DatabaseReference current_user_db=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
//                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                    mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult getTokenResult) {
                            String token_id = FirebaseInstanceId.getInstance().getToken();
                            Map newPost=new HashMap();
//                    newPost.put("devide token",deviceToken);
                            newPost.put("name",display_name);
                            newPost.put("status","HI there...i am using Commute");
                            newPost.put("image","default");
                            newPost.put("thumb_image","default");
                            newPost.put("token_id",token_id);


                            current_user_db.setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
//                                mRegProgress.dismiss();
                                        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();

                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Cannot database.", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                        }
                    });

//                    Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
//                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(mainIntent);
//                    finish();


                } else {
                    Toast.makeText(RegisterActivity.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });

    }
    private void register_user2(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){


                    String user_id=mAuth.getCurrentUser().getUid();
                    final DatabaseReference current_user_db=FirebaseDatabase.getInstance().getReference().child("Admin").child(user_id);
//                    String deviceToken= FirebaseInstanceId.getInstance().getToken();

                    mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult getTokenResult) {
                            String token_id = FirebaseInstanceId.getInstance().getToken();
                            Map newPost=new HashMap();
//                    newPost.put("devide token",deviceToken);
                            newPost.put("name",display_name);
                            newPost.put("status","HI there...i am using Commute");
                            newPost.put("image","default");
                            newPost.put("thumb_image","default");
                            newPost.put("token_id",token_id);


                            current_user_db.setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
//                                mRegProgress.dismiss();
                                        Intent mainIntent=new Intent(RegisterActivity.this,AdminActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();

                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Cannot database.", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                        }
                    });

//                    Map newPost=new HashMap();
////                    newPost.put("devide token",deviceToken);
//                    newPost.put("name",display_name);
//                    newPost.put("status","HI there...i am using Commute");
//                    newPost.put("image","default");
//                    newPost.put("thumb_image","default");
//
//
//                    current_user_db.setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful())
//                            {
////                                mRegProgress.dismiss();
//                                Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
//                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(mainIntent);
//                                finish();
//
//                            }else{
//                                Toast.makeText(RegisterActivity.this, "Cannot database.", Toast.LENGTH_LONG).show();
//
//                            }
//                        }
//                    });
//                    Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
//                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(mainIntent);
//                    finish();


                } else {
                    Toast.makeText(RegisterActivity.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}
