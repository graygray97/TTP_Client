package com.ttp.ttp_client;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.app.INotificationSideChannel;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.myViewHolder> {

    List<InputLocation> itemList;

    public Adapter(List<InputLocation> passedListItem){
        this.itemList = passedListItem;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_recycle, parent, false);

        return new myViewHolder(itemView);
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView itemTextView;
        Button delButton;

        public myViewHolder(View view){
            super(view);
            itemTextView = view.findViewById(R.id.location_Name);
            itemTextView.setBreakStrategy(Layout.BREAK_STRATEGY_BALANCED);
            delButton = view.findViewById(R.id.delete_Loc);

        }
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {
        holder.itemTextView.setText(itemList.get(position).getLocation());
        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}