package com.example.myapplication;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SummaryRecyclerAdapter extends RecyclerView.Adapter<SummaryRecyclerAdapter.MyViewHolder> {


    private List<String> list;

    public SummaryRecyclerAdapter(List<String> list){
        this.list = list;
    }

    @NonNull
    @Override
    public SummaryRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.textlayoutsummary,parent,false);
        SummaryRecyclerAdapter.MyViewHolder holder = new SummaryRecyclerAdapter.MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(SummaryRecyclerAdapter.MyViewHolder holder, int position) {
        final String temp = list.get(position);
        holder.packageId.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
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

        }
    }

}
