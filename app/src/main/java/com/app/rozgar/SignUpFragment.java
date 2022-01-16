package com.app.rozgar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment {
    View view;
    ImageView viewPass;
    EditText pass,email,name;
    String mail,password,Bname;
    int h=0;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    Button signup;
    String uid;
    TextView caution;
    CheckBox checkBox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_frag,container,false);
        viewPass = view.findViewById(R.id.viewPass);
        pass = view.findViewById(R.id.pass);
        progressDialog = new ProgressDialog(getContext());
        email = view.findViewById(R.id.email);
        caution = view.findViewById(R.id.caution);
        checkBox = view.findViewById(R.id.checkBox);
        name = view.findViewById(R.id.name);
        signup = view.findViewById(R.id.signUpbt);
        checkLang();

        viewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(h==0) {
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pass.setSelection(pass.getText().length());
                    viewPass.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
                    h=1;
                }
                else
                {
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pass.setSelection(pass.getText().length());
                    viewPass.setBackgroundResource(R.drawable.ic_visibility_black_24dp);
                    h=0;
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    //Upload data to the database
                    String user_email = email.getText().toString().trim();
                    String user_password = pass.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendEmailVerification();
                            }else{
                                Toast.makeText(getContext(), "Email already exists.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });





        return view;
    }

    private void checkLang() {
        SharedPreferences sh =getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        if(sh!=null&&sh.getString("Lang", null)!=null) {
            if (sh.getString("Lang", null).equals("Hindi")) {
                name.setHint("नाम / व्यवसाय का नाम");
                email.setHint("ईमेल पता");
                pass.setHint("पासवर्ड");
                signup.setText("साइन अप");
                checkBox.setText("पासवर्ड सेव करे");
                caution.setVisibility(View.INVISIBLE);

            }
        }
    }

    private Boolean validate(){
        Boolean result = false;

        mail = email.getText().toString();
        password= pass.getText().toString();
        Bname = name.getText().toString();



        if(password.isEmpty() || mail.isEmpty() || Bname.isEmpty()){
            Toast.makeText(getContext(), "Please enter all the details.", Toast.LENGTH_SHORT).show();
            if(mail.isEmpty())
                email.setError("Can't be empty");
            if(password.isEmpty())
                pass.setError("Can't be empty");
            if(Bname.isEmpty())
                name.setError("Can't be empty");
        }
        else
        {
            result = true;
        }

        return result;
    }
    private void sendEmailVerification(){
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser!=null){
            uid = firebaseUser.getUid();
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        sendUserData();
                      //  Toast.makeText(getContext(), "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        Dialog d = new Dialog(getContext());
                        d.setContentView(R.layout.custom_dialog_error);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        d.show();
                        name.setText("");
                        email.setText("");
                        pass.setText("");
                    }else{
                        Toast.makeText(getContext(), "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(uid).child("UserInfo");
        UserProfile userProfile = new UserProfile(Bname,mail);
        myRef.setValue(userProfile);
    }

}
