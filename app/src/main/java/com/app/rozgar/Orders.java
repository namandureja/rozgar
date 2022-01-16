package com.app.rozgar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Orders extends Fragment {

    View view;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    OrderRecyclerAdapter adapter;
    List<ProductData> productDataList;
    int lang;
    TextView orderHeader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.orders_fragment,container,false);
        recyclerView = view.findViewById(R.id.orderRecycler);
        orderHeader = view.findViewById(R.id.orderHeader);
        Paper.init(getContext());
        SharedPreferences sh1 = getActivity().getSharedPreferences("DataRecord",Context.MODE_PRIVATE);



        checklang();
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        productDataList = new ArrayList<>();

        if(!sh1.getBoolean("DataAdded",false)) {
            if (lang == 1)
                initialiseArrayHindi();
            else
                initialiseArrayEng();
        }
        else {
            OrderRecyclerAdapter adapter = new OrderRecyclerAdapter(Paper.book().read("Orders",new ArrayList<ProductData>()),getContext());
            recyclerView.setAdapter(adapter);
        }





        return view;
    }

    private void checklang() {
        SharedPreferences sh =getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        if(sh!=null&&sh.getString("Lang", null)!=null) {
            if (sh.getString("Lang", null).equals("Hindi")) {
                orderHeader.setText("आदेश");
                lang=1;

            } else if (sh.getString("Lang", null).equals("Eng")) {
                lang=0;

            }
        }
    }

    private void initialiseArrayEng(){
        productDataList.add(new ProductData("Women's T Shirts", "52 Units", "Due in 5 days", R.drawable.tshirts));
        productDataList.add(new ProductData("Colourful Bangles", "31 Units", "Due in 3 days", R.drawable.bangles));
        productDataList.add(new ProductData("Carpet", "25 Units", "Due in 8 days", R.drawable.carpet));
        productDataList.add(new ProductData("Slippers", "13 Units", "Due in 4 days", R.drawable.womenchappal));
        OrderRecyclerAdapter adapter = new OrderRecyclerAdapter(productDataList,getContext());
        recyclerView.setAdapter(adapter);
    }

    private void initialiseArrayHindi(){
        productDataList.add(new ProductData("महिला टी शर्ट्स", "52 इकाइयाँ", "5 दिनों में पूरा", R.drawable.tshirts));
        productDataList.add(new ProductData("रंगीन चूड़ियाँ", "31 इकाइयाँ", "3 दिनों में पूरा", R.drawable.bangles));
        productDataList.add(new ProductData("गलीचा", "25 इकाइयाँ", "8 दिनों में पूरा", R.drawable.carpet));
        productDataList.add(new ProductData("चप्पलें", "13 इकाइयाँ", "4 दिनों में पूरा", R.drawable.womenchappal));
        OrderRecyclerAdapter adapter = new OrderRecyclerAdapter(productDataList,getContext());
        recyclerView.setAdapter(adapter);
    }


}
