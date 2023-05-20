package com.project.pocketbookapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.pocketbookapp.Model.CategoryModel;
import com.project.pocketbookapp.databinding.ActivityPdfAddBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {

    private ActivityPdfAddBinding binding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;

    private  String title="",description="";

    //tag for debugging
    private static final String tag="ADD_PDF_TAG";

    private static final int PDF_PICK_CODE=1000;

    //uri of picked pdf
    private Uri pdfUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        loadPdfCategories();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryPickDialog();
            }
        });

        binding.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        Log.d(tag,"validateData: validating data");

        title=binding.titleEt.getText().toString().trim();
        description=binding.descriptionEt.getText().toString().trim();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this,"Enter Title",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this,"Enter Description",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(chosenCategoryTitle)){
            Toast.makeText(this,"Pick Category",Toast.LENGTH_SHORT).show();
        }
        else if(pdfUri==null){
            Toast.makeText(this,"Pick Pdf",Toast.LENGTH_SHORT).show();
        }
        else{
            uploadPdf();
        }
    }

    private void uploadPdf() {
        Log.d(tag,"uploadPdf: uploading pdf");

        progressDialog.setMessage("Uploading Pdf...");
        progressDialog.show();

        long timestamp=System.currentTimeMillis();

        String filePathAndName="Books/"+timestamp;

        StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(tag,"onSuccess: Pdf uploaded...getting it's URL");
                        //getting pdf url
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedPdfUrl=""+uriTask.getResult();

                        //upload to database
                        uploadPdfInfo(uploadedPdfUrl,timestamp);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(tag,"onFailure: PDF upload failed due to"+e.getMessage());
                        Toast.makeText(PdfAddActivity.this,"Upload failed because of"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfInfo(String uploadedPdfUrl, long timestamp) {
        Log.d(tag,"uploadPdfInfo: uploading pdf info into database");
        progressDialog.setMessage("Uploading pdf info...");
        String uid=firebaseAuth.getUid();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+chosenCategoryId);
        hashMap.put("url",""+uploadedPdfUrl);
        hashMap.put("timestamp",timestamp);
        hashMap.put("viewsCount",0);
        hashMap.put("downloadsCount",0);


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Log.d(tag,"onSuccess: Successfully uploaded");
                        Toast.makeText(PdfAddActivity.this,"Successfully uploaded pdf",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(tag,"onFailure: Failed to upload to database due to"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PdfAddActivity.this,"Uploading pdf to database failed due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String chosenCategoryId , chosenCategoryTitle;
    private void categoryPickDialog() {
        Log.d(tag,"categoryPickDialog: showing category pick dialog");

        //get string array of categories from arraylist
        String[] categoriesArray=new String[categoryTitleArrayList.size()];
        for(int i = 0; i< categoryTitleArrayList.size(); i++){
            categoriesArray[i]= categoryTitleArrayList.get(i);
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosenCategoryTitle=categoryTitleArrayList.get(which);
                        chosenCategoryId=categoryIdArrayList.get(which);
                        binding.categoryTv.setText(chosenCategoryTitle);

                        Log.d(tag,"onClick: Selected Category "+chosenCategoryId+" "+chosenCategoryTitle);
                    }
                })
                .show();
    }

    private void loadPdfCategories() {
        Log.d(tag,"LoadPdfCategories: Loading pdf categories...");
        //categoryArrayList = new ArrayList<>();
        categoryTitleArrayList=new ArrayList<>();
        categoryIdArrayList=new ArrayList<>();


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear(); //clear before adding data
                categoryIdArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    //get id and title of category
                    String categoryId=""+ds.child("id").getValue();
                    String categoryTitle=""+ds.child("category").getValue();


                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void pdfPickIntent() {
        Log.d(tag,"pdfPickIntent:starting pdf pick intent");

        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Pdf"),PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==PDF_PICK_CODE){
                Log.d(tag,"onActivityResult: Pdf picked");

                pdfUri=data.getData();

                Log.d(tag,"onActivityResult: URI: "+pdfUri);
            }
        }
        else{
            Log.d(tag,"onActivityResult: cancelled picking pdf");
            Toast.makeText(this, "cancelled picking pdf", Toast.LENGTH_SHORT).show();
        }
    }
}