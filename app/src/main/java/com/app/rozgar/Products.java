package com.app.rozgar;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Products extends Fragment {
    View view;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ProductRecyclerAdapter adapter;
    TextView username;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseRef;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    private List<UploadClass> mUploadList;
    int lang;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.products_fragment,container,false);
        recyclerView = view.findViewById(R.id.productRecycler);
        progressBar = view.findViewById(R.id.progressCircle);
        username = view.findViewById(R.id.userName);
        checklang();
        layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        mUploadList = new ArrayList<>();
        mUploadList.add(null);
        adapter = new ProductRecyclerAdapter(mUploadList,getContext(),lang);
        recyclerView.setAdapter(adapter);
        mDatabaseRef = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("Products");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploadList.clear();
                progressBar.setVisibility(View.GONE);
                mUploadList.add(null);
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    UploadClass upload = postSnapshot.getValue(UploadClass.class);
                    upload.setmKey(postSnapshot.getKey());
                    mUploadList.add(upload);
                }
                adapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });




        final DatabaseReference dbNameReference = firebaseDatabase.getReference("Users/"+ firebaseAuth.getUid()+"/UserInfo");

        dbNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                String name = dataSnapshot.child("name").getValue().toString();

                ViewGroup parent=(ViewGroup) username.getParent();
                parent.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                if(lang==1)
                username.setText("स्वागत है, "+name);
                else if(lang==0)
                    username.setText("Welcome, "+name);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "database not found", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void checklang() {
        SharedPreferences sh =getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        if(sh!=null&&sh.getString("Lang", null)!=null) {
            if (sh.getString("Lang", null).equals("Hindi")) {
                username.setText("स्वागत है");
                lang=1;

            } else if (sh.getString("Lang", null).equals("Eng")) {
                username.setText("Welcome");
                lang=0;

            }
        }
    }
}
