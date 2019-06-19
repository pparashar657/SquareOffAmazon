package com.example.myapplication;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private List<String> list;

    public RecyclerAdapter(List<String> list){
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.textlayoutsummary,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String temp = list.get(position);
        holder.packageId.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position){
        UploadActivity.trId.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(String id) {
        UploadActivity.trId.add(id);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        TextView packageId;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            packageId = (TextView) itemView.findViewById(R.id.textlayout);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(),121,0,"Remove this item");
        }
    }
}
