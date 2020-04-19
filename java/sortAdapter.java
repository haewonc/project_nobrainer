package com.example.nobrainer;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class sortAdapter extends RecyclerView.Adapter<sortAdapter.ItemViewHolder> {

    Context mContext;
    public ArrayList<messageSort> items = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference mRef;

    public sortAdapter(Context context) {
        mContext = context;
    }

    @Override
    public sortAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sort_message, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final sortAdapter.ItemViewHolder holder, int position) {
        final messageSort item = items.get(position);
        holder.whoSend.setText(item.getName());
        holder.whatSend.setText(item.getText());
        holder.ThumbsNum.setText(String.valueOf(item.getThumb()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<messageSort> items) {
        this.items = items;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView whatSend;
        TextView whoSend;
        ImageView Thumbs;
        TextView ThumbsNum;

        ItemViewHolder(View itemView) {
            super(itemView);
            whatSend = (TextView) itemView.findViewById(R.id.whatSend2);
            whoSend = (TextView) itemView.findViewById(R.id.whoSend2);
            Thumbs = (ImageView) itemView.findViewById(R.id.thumbs2);
            ThumbsNum = (TextView) itemView.findViewById(R.id.thumbsNum2);
        }
    }
}