package com.app.rozgar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class RegActivity extends AppCompatActivity {

    TabLayout tabLayout;
    Toolbar appBarlayout;
    ImageButton help;
    ViewPager viewPager;
    private FirebaseAuth firebaseAuth;
    SharedPreferences sh;
    SharedPreferences.Editor editor;
    int lang ;
    private TextToSpeech tts;
    Button speech;
    Dialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        sh = getSharedPreferences("Language",MODE_PRIVATE);
        editor = sh.edit();


        tabLayout = findViewById(R.id.tabLayout);
        help = findViewById(R.id.helpBt);

        appBarlayout = findViewById(R.id.appBarId);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setTabRippleColor(null);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(RegActivity.this, homePage.class));
        }



        ViewPagerTabsAdapter adapter = new ViewPagerTabsAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        if(sh!=null){
        if(sh.getString("Lang","aloo").equals("Hindi")) {
            adapter.AddFragment(new SignUpFragment(), " साइन अप ");
            adapter.AddFragment(new SignInFragment(), " साइन इन ");
            lang=1;
        }
        else {
            adapter.AddFragment(new SignUpFragment(), " Sign Up ");
            adapter.AddFragment(new SignInFragment(), " Sign In ");
            lang=0;
        }
        }
        else{
            adapter.AddFragment(new SignUpFragment(), " Sign Up ");
            adapter.AddFragment(new SignInFragment(), " Sign In ");
            lang=0;
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    //InputMethodManager is used to hide the virtual keyboard from the user after finishing the user input
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isAcceptingText()) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } catch (NullPointerException e) {
                    Log.e("Exception", e.getMessage() + ">>");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               d = new Dialog(RegActivity.this);
                d.setContentView(R.layout.translate_dialg);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button translate = d.findViewById(R.id.translateBt);
                speech = d.findViewById(R.id.speakOut);
                translate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeLang();
                    }
                });
                speech.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                        if(tts!=null)
                            tts.speak(getString(R.string.reg_page_hindi),TextToSpeech.QUEUE_FLUSH,null,null);

                    }
                });


                d.show();

            }
        });

        inititaliseTTS();
    }

    private void inititaliseTTS() {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.forLanguageTag("hin"));
                }
            }
        });

    }

    private void changeLang() {
        if(sh.getString("Lang","aloo").equals("aloo")||sh.getString("Lang","aloo").equals("Eng")) {
            editor.putString("Lang", "Hindi");
            editor.commit();
            recreate();
        }
        else
        {
            editor.putString("Lang", "Eng");
            editor.commit();
            recreate();

        }
    }


    @Override
    protected void onPause() {

        if(tts!=null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        if(tts!=null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        inititaliseTTS();
    }
}
