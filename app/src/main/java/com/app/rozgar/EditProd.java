package com.app.rozgar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class EditProd extends AppCompatActivity {

    EditText prodName,prodPrice,prodQty;
    Button editBt,deleteBt;
    ImageView backBt,prodImage;
    FirebaseDatabase firebaseDatabase;
    String prodKey;
    String link;
    ProgressDialog progressDialog;
    DatabaseReference mDatabaseRef;
    FirebaseStorage mStorage;
    SharedPreferences sh;
    TextView edittext;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prod);
        sh = getSharedPreferences("Language",MODE_PRIVATE);
        backBt = findViewById(R.id.backBt);
        edittext = findViewById(R.id.editText);
        prodName = findViewById(R.id.prodName);
        prodPrice = findViewById(R.id.prodPrice);
        prodImage = findViewById(R.id.prodImage);
        prodQty = findViewById(R.id.prodQty);
        deleteBt = findViewById(R.id.deleteBt);
        editBt = findViewById(R.id.editBt);
        progressDialog = new ProgressDialog(this);
        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        prodKey = getIntent().getStringExtra("Key");
        mStorage = FirebaseStorage.getInstance();

        if(sh!=null){
            if(sh.getString("Lang","aloo").equals("Hindi")) {
                edittext.setText("उत्पाद संपादित करें");
                deleteBt.setText("हटाएं");
                editBt.setText("संपादित करें");
            }
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
         mDatabaseRef = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Products").child(prodKey);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null) {
                    UploadClass upload = snapshot.getValue(UploadClass.class);
                    if (upload != null) {
                        link = upload.getImageURL();
                        prodName.setText(upload.getProdName());
                        prodPrice.setText(upload.getProdPrice());
                        prodQty.setText(upload.getProdQty());
                        category = upload.getCategory();
                        Picasso.get().load(upload.getImageURL()).fit().centerCrop().into(prodImage, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProd.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    //    Toast.makeText(this, getIntent().getStringExtra("Key"),Toast.LENGTH_SHORT).show();

        editBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editChanges();
            }
        });

        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProd();
            }
        });
    }

    private void deleteProd() {
        progressDialog.setTitle("Deleting product...");
        progressDialog.show();
        StorageReference imageRef = mStorage.getReferenceFromUrl(link);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.removeValue();
                progressDialog.dismiss();
                finish();
            }
        });
    }


    private void editChanges() {
        UploadClass uploadinfo = new UploadClass(prodName.getText().toString(), link, prodQty.getText().toString(),prodPrice.getText().toString(),category);
        mDatabaseRef.setValue(uploadinfo);
        Toast.makeText(this,"Changes Saved!",Toast.LENGTH_SHORT).show();
    }
}
