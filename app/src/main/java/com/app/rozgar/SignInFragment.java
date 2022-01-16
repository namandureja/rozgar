package com.app.rozgar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignInFragment extends Fragment {
    View view;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    EditText email,pass;
    Button signIn;
    String mail,password;
    ProgressDialog progressDialog;
    ImageView viewPass;
    CheckBox checkBox;
    int h=0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_frag,container,false);
        email = view.findViewById(R.id.email);
        pass = view.findViewById(R.id.pass);
        signIn = view.findViewById(R.id.loginBt);
        viewPass = view.findViewById(R.id.viewPass);
        checkBox = view.findViewById(R.id.checkBox);
        checklang();

        progressDialog = new ProgressDialog(getContext());

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

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    validate(email.getText().toString(),pass.getText().toString());
                }
            }
        });


        return view;
    }

    private void validate(String userName, String userPassword) {

        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    //Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    checkEmailVerification();
                }else{
                    Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });


    }

    private void checkEmailVerification(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            Boolean emailflag = firebaseUser.isEmailVerified();
            if (emailflag) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), homePage.class));
            } else {
                Dialog d = new Dialog(getContext());
                d.setContentView(R.layout.custom_dialog_error_login);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();
              //  Toast.makeText(getContext(), "Verify your email please.", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }
        }
    }

    private Boolean validate(){
        Boolean result = false;
        password = pass.getText().toString();
        mail = email.getText().toString();


        if( password.isEmpty() || mail.isEmpty() ){
            Toast.makeText(getContext(), "Please enter all the details.", Toast.LENGTH_SHORT).show();
            if(password.isEmpty())
                pass.setError("Can't be empty.");
            if(mail.isEmpty())
                email.setError("Can't be empty.");
        }else{

            result = true;
        }

        return result;
    }
    private void checklang() {
        SharedPreferences sh =getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        if(sh!=null&&sh.getString("Lang", null)!=null) {
            if (sh.getString("Lang", null).equals("Hindi")) {
                email.setHint("ईमेल पता");
                pass.setHint("पासवर्ड");
                signIn.setText("साइन इन");
                checkBox.setText("पासवर्ड सेव करे");

            }

        }
    }
}
