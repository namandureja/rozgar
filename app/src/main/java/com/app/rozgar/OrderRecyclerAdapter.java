package com.app.rozgar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.paperdb.Paper;

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.MyViewHolder> {

    private List<ProductData> productList;
    private Context context;
    SharedPreferences sh;
    SharedPreferences.Editor editor;


    public OrderRecyclerAdapter(List<ProductData> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holders, int position) {
        final MyViewHolder holder = holders;

            ProductData model = productList.get(position);

            holder.prodName.setText(model.getProdName());
            holder.prodQty.setText(model.getQty());
            holder.dueDate.setText(model.getDueDate());
            holder.img.setImageResource(model.getImageResId());
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView prodName,prodQty,dueDate;
        Button clear;
        ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           prodName = itemView.findViewById(R.id.prodName);
           prodQty = itemView.findViewById(R.id.qty);
           dueDate = itemView.findViewById(R.id.dueDate);
           clear = itemView.findViewById(R.id.clearBt);
           img = itemView.findViewById(R.id.image);
           itemView.setOnClickListener(this);
           clear.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == clear.getId()) {
                productList.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(),productList.size());
                updateSharedPref();

            }
        }
    }

    public List<ProductData> returnList(){

        return productList;
    }

    private void updateSharedPref(){
        Paper.init(context);
        Paper.book().write("Orders",productList);
        sh = context.getSharedPreferences("DataRecord",Context.MODE_PRIVATE);
        editor = sh.edit();
        editor.putBoolean("DataAdded",true);
        editor.commit();
    }
}
