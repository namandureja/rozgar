package com.app.rozgar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class AddProduct extends AppCompatActivity {
    ImageView backBt,edit,gallery,camera,image;
    EditText prodName,prodQty,prodPrice;
    Button addBt;
    RelativeLayout editImg;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    LinearLayout linearLayout;
    StorageReference storageReference;
    TextView galleryTxt,camTxt;
    DatabaseReference databaseReference,databaseReferenceRoot;
    ProgressDialog progressDialog ;
    private StorageTask mUploadTask;
    String category;
    FirebaseAuth firebaseAuth;


    String cameraPermission[];
    String storagePermission[];
    Uri image_uri,resultUri;
    SharedPreferences sh;
    String photoStringLink,qty,price,TempImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        sh = getSharedPreferences("Language",MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Products");
        databaseReferenceRoot = FirebaseDatabase.getInstance().getReference("Users").child("AllProducts");
        backBt = findViewById(R.id.backBt);
        edit = findViewById(R.id.editImg);
        galleryTxt = findViewById(R.id.galleryText);
        camTxt = findViewById(R.id.cameraText);
        gallery = findViewById(R.id.Gallery);
        camera = findViewById(R.id.camera);
        image = findViewById(R.id.prodImage);
        prodName = findViewById(R.id.prodName);
        prodQty = findViewById(R.id.prodQty);
        prodPrice = findViewById(R.id.prodPrice);
        linearLayout = findViewById(R.id.selectLayout);
        addBt = findViewById(R.id.addBt);
        editImg = findViewById(R.id.editBg);
        progressDialog = new ProgressDialog(this);
        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        MaterialSpinner spinner =findViewById(R.id.spinner);
        spinner.setItems("Essentials", "Stationary", "Footwear", "Clothing");
        category="Essentials";
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                category = item;
            }
        });

        if(sh!=null){
            if(sh.getString("Lang","aloo").equals("Hindi")) {
                galleryTxt.setText("गेलरी");
                camTxt.setText("कैमरा");
                prodName.setHint("उत्पाद का नाम");
                prodQty.setHint("प्रति माह अधिकतम मात्रा");
                prodPrice.setHint("कीमत");
                addBt.setText("उत्पाद जोड़ें");
                spinner.setHint("कोई श्रेणी चुनें");
            }
        }

        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkStoragePermission()){
                    requestStoragePermission();
                }else {
                    pickGallery();
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editImg.setVisibility(View.GONE);
                image.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkCameraPermission()){
                    requestCameraPermission();
                }else {
                    pickCamera();
                }
            }
        });
        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUploadTask !=null && mUploadTask.isInProgress()){
                    Toast.makeText(AddProduct.this,"Upload in progress..",Toast.LENGTH_SHORT).show();
                }else {
                    uploadImage();
                }
            }
        });



    }

    private void uploadImage() {

        if(fieldsCheck()) {
            if (resultUri != null) {

                progressDialog.setTitle("Data is being uploaded...");
                progressDialog.show();
                StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(resultUri));
                mUploadTask = storageReference2.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                TempImageName = prodName.getText().toString().trim();
                                price = "Rs. " + prodPrice.getText().toString().trim();
                                 qty = prodQty.getText().toString().trim() +" Units" ;
                                @SuppressWarnings("VisibleForTests")
                                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        photoStringLink = uri.toString();
                                        progressDialog.dismiss();
                                        UploadClass uploadinfo = new UploadClass(TempImageName, photoStringLink, qty, price,category);
                                        String ImageUploadId = databaseReference.push().getKey();
                                        databaseReference.child(ImageUploadId).setValue(uploadinfo);
                                        databaseReferenceRoot.child(ImageUploadId).setValue(uploadinfo);
                                        finish();
                                        startActivity(new Intent(AddProduct.this, ProductAddSuccess.class));
                                    }
                                });


                            }
                        });
            } else {
                Toast.makeText(AddProduct.this, "Please Select an image.", Toast.LENGTH_LONG).show();

            }
        }else{
            Toast.makeText(AddProduct.this, "Please fill up all the details.", Toast.LENGTH_LONG).show();

        }



    }

    private boolean fieldsCheck() {
        Boolean result = true;
        if(prodName.getText().toString().isEmpty()){
            prodName.setError("Please fill this field.");
            result=false;
        }
        if(prodQty.getText().toString().isEmpty()){
            prodQty.setError("Please fill this field.");
            result = false;
        }
        if(prodPrice.getText().toString().isEmpty()){
            prodPrice.setError("Please fill this field.");
            result = false;
        }
        return result;
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }


    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);


    }


    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==  (PackageManager.PERMISSION_GRANTED);


        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result  = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                ==  (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==  (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);

    }

    public void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAcceptor = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAcceptor)
                    {
                        pickCamera();
                    }
                    else {
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0) {
                    boolean writeStorageAcceptor = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAcceptor)
                    {
                        pickGallery();
                    }
                    else {
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);


            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    image.setImageBitmap(bitmap);
                    image.setVisibility(View.VISIBLE);
                    editImg.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }


