package com.app.rozgar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProductAddSuccess extends AppCompatActivity {

    Button goback;
    SharedPreferences sh;
    TextView prod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add_success);
        sh = getSharedPreferences("Language",MODE_PRIVATE);
        goback = findViewById(R.id.goBack);
        prod = findViewById(R.id.productAdded);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(sh!=null){
            if(sh.getString("Lang","aloo").equals("Hindi")) {
               goback.setText("वापस");
               prod.setText("उत्पाद जोड़ा गया");
            }
        }
    }
}
