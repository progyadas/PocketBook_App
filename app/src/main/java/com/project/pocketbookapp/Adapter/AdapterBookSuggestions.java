package com.project.pocketbookapp.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.pocketbookapp.Activities.PdfEditActivity;
import com.project.pocketbookapp.Model.ModelBookSuggestions;
import com.project.pocketbookapp.MyApplication;
import com.project.pocketbookapp.R;
import com.project.pocketbookapp.databinding.RowBookSuggestionsBinding;

import java.util.ArrayList;

public class AdapterBookSuggestions extends RecyclerView.Adapter<AdapterBookSuggestions.HolderBookSuggestions>{

    private Context context;

    private ArrayList<ModelBookSuggestions> bookSuggestionsArrayList;

    private RowBookSuggestionsBinding binding;

    public AdapterBookSuggestions(Context context, ArrayList<ModelBookSuggestions> bookSuggestionsArrayList) {
        this.context = context;
        this.bookSuggestionsArrayList = bookSuggestionsArrayList;
    }

    @NonNull
    @Override
    public HolderBookSuggestions onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowBookSuggestionsBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderBookSuggestions(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderBookSuggestions holder, int position) {
        ModelBookSuggestions modelBookSuggestions = bookSuggestionsArrayList.get(position);


        String timestamp = modelBookSuggestions.getTimestamp();
        String bookName = modelBookSuggestions.getBookName();

        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

        holder.bookSuggestionTv.setText(bookName);
        holder.dateTv.setText(date);

        loadUserDetails(modelBookSuggestions,holder);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionsDialog(modelBookSuggestions,holder);
            }
        });
    }

    private void moreOptionsDialog(ModelBookSuggestions modelBookSuggestions, HolderBookSuggestions holder) {
        String[] options={"Delete"};

        String id=modelBookSuggestions.getId();
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Do you want to delete?")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            ProgressDialog progressDialog=new ProgressDialog(context);
                            progressDialog.setTitle("Please wait");
                            progressDialog.setMessage("Deleting "+modelBookSuggestions.getBookName());
                            progressDialog.show();
                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("BooksSuggestion");
                            ref.child(id)
                                    .removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(context,"Book Suggestion Deleted Successfully",Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(context,"Book Suggestion deletion failed due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .show();
    }

    private void loadUserDetails(ModelBookSuggestions modelBookSuggestions, HolderBookSuggestions holder) {
        String uid = modelBookSuggestions.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        holder.nameTv.setText(name);

                        try{
                            Glide.with(context)
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_person_gray)
                                    .into(holder.profileTv);
                        }
                        catch(Exception e){
                            holder.profileTv.setImageResource(R.drawable.ic_person_gray);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return bookSuggestionsArrayList.size();
    }

    class HolderBookSuggestions extends RecyclerView.ViewHolder{

        TextView bookSuggestionTv,dateTv,nameTv;
        ShapeableImageView profileTv;
        ImageButton moreBtn;

        public HolderBookSuggestions(@NonNull View itemView) {
            super(itemView);
            bookSuggestionTv = binding.bookSuggestionTv;
            dateTv = binding.dateTv;
            nameTv = binding.nameTv;
            profileTv = binding.profileTv;
            moreBtn = binding.moreBtn;
        }
    }

}
