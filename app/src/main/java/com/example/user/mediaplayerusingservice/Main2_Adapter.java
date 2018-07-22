package com.example.user.mediaplayerusingservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Main2_Adapter extends RecyclerView.Adapter {

    Context mContext;
    public Main2_Adapter(Context context){
        mContext = context;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.songlist_piece,parent,false);
        return new ItemHolder(view);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemHolder)holder).textView.setText("Song "+position);
    }

    public int getItemCount() {
        return 4;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        public ItemHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_Song);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            Intent intent = new Intent(mContext,MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("Song",getAdapterPosition());
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }
    }
}

