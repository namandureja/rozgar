package com.app.rozgar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class homePage extends AppCompatActivity implements View.OnClickListener{

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    TextView signOut,editProfile;
    private TabLayout tabLayout;
    private HeightWrappingViewPager viewPager;
    private FirebaseAuth firebaseAuth;
    SharedPreferences sh;
    SharedPreferences.Editor editor;
    TextToSpeech tts;
    Dialog d;
    int lang ;

    private int[] navIcons = {
            R.drawable.basket_unselected,
            R.drawable.cart_unselected
    };
    private int[] navLabels = {
            R.string.nav_home,
            R.string.nav_order
    };
    private int[] navLabelsHindi = {
            R.string.nav_home_hindi,
            R.string.nav_order_hindi
    };
    private int[] navIconsActive = {
            R.drawable.basket_selected,
            R.drawable.cart_selected
    };

    ImageButton help;

    @Override
    protected void onResume() {
        if (dl.isDrawerOpen(GravityCompat.START)) {

            dl.closeDrawer(GravityCompat.START);

        }
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        sh = getSharedPreferences("Language",MODE_PRIVATE);
        editor = sh.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        help = findViewById(R.id.helpBt);
        Toolbar toolbar = findViewById(R.id.appBarId);
        dl = findViewById(R.id.drawerLayout);
        tabLayout = findViewById(R.id.tabLayout);
        nv = findViewById(R.id.nv);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setTabRippleColor(null);
        editProfile = findViewById(R.id.editProfile);
        signOut = findViewById(R.id.signOutBt);


        setSupportActionBar(toolbar);
        t = new ActionBarDrawerToggle(this, dl, toolbar, R.string.open, R.string.close);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setTitle("");

        ViewPagerTabsAdapter adapter = new ViewPagerTabsAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        if(sh!=null){
            if(sh.getString("Lang","aloo").equals("Hindi")) {
                adapter.AddFragment(new Products(), " मेरे उत्पाद ");
                adapter.AddFragment(new Orders(), " आदेश ");
                signOut.setText("साइन आउट");
                editProfile.setText("प्रोफ़ाइल संपादित करें");
                lang=1;

            }
            else {
                adapter.AddFragment(new Products()," My Products ");
                adapter.AddFragment(new Orders()," Orders ");
                lang=0;
            }
        }
        else{
            adapter.AddFragment(new Products()," My Products ");
            adapter.AddFragment(new Orders()," Orders ");
            lang=0;
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        setTabIcons();

        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);

                        View tabView = tab.getCustomView();

                        TextView tab_label =  tabView.findViewById(R.id.nav_label);
                        ImageView tab_icon = tabView.findViewById(R.id.nav_icon);

                        // change the label color, by getting the color resource value
                        tab_label.setTextColor(getResources().getColor(R.color.primary));
                        tab_icon.setImageResource(navIconsActive[tab.getPosition()]);
                    }

                    // do as the above the opposite way to reset tab when state is changed
                    // as it not the active one any more
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        View tabView = tab.getCustomView();
                        TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                        ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);

                        tab_label.setTextColor(getResources().getColor(R.color.dark_grey));
                        tab_icon.setImageResource(navIcons[tab.getPosition()]);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                }
        );

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d = new Dialog(homePage.this);
                d.setContentView(R.layout.translate_dialg);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button translate = d.findViewById(R.id.translateBt);
                translate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeLang();
                    }
                });

                Button speech = d.findViewById(R.id.speakOut);
                speech.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                        if(tts!=null)
                        tts.speak(getString(R.string.home_page_hindi), TextToSpeech.QUEUE_FLUSH,null,null);

                    }
                });

                d.show();
            }
        });
    }

    private void setTabIcons() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.nav_tab, null);

            TextView tab_label = tab.findViewById(R.id.nav_label);
            ImageView tab_icon =  tab.findViewById(R.id.nav_icon);

            if(lang==0)
            tab_label.setText(getResources().getString(navLabels[i]));
            else if(lang==1)
                tab_label.setText(getResources().getString(navLabelsHindi[i]));

            if(i == 0) {
                tab_label.setTextColor(getResources().getColor(R.color.primary));
                tab_icon.setImageResource(navIconsActive[i]);
            } else {
                tab_icon.setImageResource(navIcons[i]);
            }

            tabLayout.getTabAt(i).setCustomView(tab);
        }

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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.item1:
                logoutUser();
                break;

            case R.id.item2:
                openEditor();
                break;

        }

    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(homePage.this, RegActivity.class));
    }

    private void openEditor() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    protected void onPostResume() {
        super.onPostResume();
        inititaliseTTS();
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

}
