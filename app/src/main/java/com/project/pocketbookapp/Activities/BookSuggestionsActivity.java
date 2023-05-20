package com.project.pocketbookapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.pocketbookapp.Adapter.AdapterBookSuggestions;
import com.project.pocketbookapp.Model.ModelBookSuggestions;
import com.project.pocketbookapp.databinding.ActivityBookSuggestionsBinding;

import java.util.ArrayList;

public class BookSuggestionsActivity extends AppCompatActivity {

    private ActivityBookSuggestionsBinding binding;
    private AdapterBookSuggestions adapterBookSuggestions;


    private ArrayList<ModelBookSuggestions> modelBookSuggestionsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookSuggestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadBookSuggestions();
    }

    private void loadBookSuggestions() {
        modelBookSuggestionsArrayList = new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("BooksSuggestion");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelBookSuggestionsArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelBookSuggestions model=ds.getValue(ModelBookSuggestions.class);

                    modelBookSuggestionsArrayList.add(model);
                }
                //setup adapter
                adapterBookSuggestions=new AdapterBookSuggestions(BookSuggestionsActivity.this,modelBookSuggestionsArrayList);
                //set adapter to recyclerview
                binding.bookSuggestionsRv.setAdapter(adapterBookSuggestions);
                // recyclerView.setLayoutManager(new LinearLayoutManager(DashboardAdminActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}