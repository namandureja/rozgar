package com.app.rozgar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.MyViewHolder> {

    private List<UploadClass> productList;
    private Context context;
    int lang;


    public ProductRecyclerAdapter(List<UploadClass> productList, Context context,int lang) {
        this.productList = productList;
        this.context = context;
        this.lang = lang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holders, int position) {
        final MyViewHolder holder = holders;
        if(position!=0) {
            UploadClass model = productList.get(position);
            holder.prodImg.setClipToOutline(true);
            holder.prodName.setText(model.getProdName());
            holder.prodPrice.setText(model.getProdPrice());
            Picasso.get().load(model.getImageURL()).fit().centerCrop().into(holder.prodImg, new Callback() {
                @Override
                public void onSuccess() {
                    holder.circle.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        else{
            holder.circle.setVisibility(View.INVISIBLE);
            holder.prodImg.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
            holder.add.setVisibility(View.VISIBLE);
            if(lang==0)
            holder.prodName.setText("Add New Product");
            else if(lang==1)
                holder.prodName.setText("नया उत्पाद जोड़ें");

            holder.prodName.setTextColor(context.getResources().getColor(R.color.primary));
            holder.prodPrice.setText("");
        }


    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView prodName,prodPrice;
        ImageView prodImg;
        ProgressBar circle;
        LinearLayout prodView;
        ImageButton add;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           prodName = itemView.findViewById(R.id.prodName);
           prodImg = itemView.findViewById(R.id.image);
           prodPrice = itemView.findViewById(R.id.prodPrice);
           add = itemView.findViewById(R.id.addButton);
           circle = itemView.findViewById(R.id.progress_circular);
           prodView = itemView.findViewById(R.id.parentProductView);
           itemView.setOnClickListener(this);
           prodView.setOnClickListener(this);
           add.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == add.getId()) {
                Intent intent = new Intent(context,AddProduct.class);
                context.startActivity(intent);
            }
            if(v.getId()== prodView.getId()){
                if(getAdapterPosition()!=0){
                    Intent i = new Intent(context,EditProd.class);
                    i.putExtra("Key",productList.get(getAdapterPosition()).getmKey());
                    context.startActivity(i);
                }
            }
        }
    }
}
