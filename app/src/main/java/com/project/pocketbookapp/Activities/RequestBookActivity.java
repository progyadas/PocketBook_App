package com.project.pocketbookapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.pocketbookapp.databinding.ActivityRequestBookBinding;

import java.util.HashMap;

public class RequestBookActivity extends AppCompatActivity {

    private ActivityRequestBookBinding binding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private String bookName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser()==null){
                    Toast.makeText(RequestBookActivity.this, "You are not logged in...", Toast.LENGTH_SHORT).show();
                }
                else{
                    validateData();
                }
            }
        });
    }

    private void validateData() {
        bookName = binding.chatEt.getText().toString().trim();
        if(TextUtils.isEmpty(bookName)){
            Toast.makeText(RequestBookActivity.this, "Add Book Name...", Toast.LENGTH_SHORT).show();
        }
        else{
            addBookSuggestion();
        }
    }

    private void addBookSuggestion() {
        progressDialog.setMessage("Adding comment...");
        progressDialog.show();

        String timestamp = ""+System.currentTimeMillis();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("bookName",""+bookName);
        hashMap.put("uid",""+firebaseAuth.getUid());

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("BooksSuggestion");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(RequestBookActivity.this,"Successfully requested pdf of the book",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RequestBookActivity.this,"Requesting pdf of the book failed due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}