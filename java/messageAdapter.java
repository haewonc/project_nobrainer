package com.example.nobrainer;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.ItemViewHolder> {

    Context mContext;
    String chatCode;
    String uid;
    public ArrayList<ChatMessage> items = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference mRef;

    public messageAdapter(Context context, String ChatCode,String Uid) {
        mContext = context;
        chatCode = ChatCode;
        uid=Uid;
    }

    @Override
    public messageAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final messageAdapter.ItemViewHolder holder, int position) {
        final ChatMessage item = items.get(position);
        holder.whoSend.setText(item.getName());
        holder.whatSend.setText(item.getText());
        if(item.getPhotoUrl() == null) {
            holder.circleImageView.setImageDrawable(ContextCompat.getDrawable(
                    mContext,R.drawable.ic_account_circle_black_24dp
            ));
        }
        else if(item.getPhotoUrl().equals("thisIsAI"))
        {
            holder.circleImageView.setImageDrawable(ContextCompat.getDrawable(
                    mContext,R.drawable.ic_account_circle_ai_24dp
            ));
        }
        else
        {
            Glide.with(mContext)
                    .load(item.getPhotoUrl())
                    .into(holder.circleImageView);
        }
        Log.d(this.getClass().getName(),item.getImageUrl());
        Glide.with(mContext).load(item.getImageUrl()).into(holder.Crawl);
        holder.ThumbsNum.setText(String.valueOf(item.getThumb()));
        holder.Thumbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer n=item.getThumb();
                item.setThumb(n+1);
                holder.ThumbsNum.setText(String.valueOf(item.getThumb()));
                String url=item.getPhotoUrl();
                database = FirebaseDatabase.getInstance();
                mRef = database.getReference("CHATS").child(chatCode).child("MESS").child(item.getTime());
                mRef.child("Thumbs").setValue(String.valueOf(item.getThumb()));

                DatabaseReference tRef=database.getReference("USERS").child(uid).child("Chats").child(chatCode).child("MY").child(item.getTime());
                tRef.child("Name").setValue(item.getName());
                tRef.child("Thumbs").setValue(String.valueOf(item.getThumb()));
                tRef.child("Text").setValue(item.getText());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<ChatMessage> items) {
        this.items = items;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView whoSend;
        TextView whatSend;
        ImageView Thumbs;
        TextView ThumbsNum;
        ImageView Crawl;

        ItemViewHolder(View itemView) {
            super(itemView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.profile);
            whoSend = (TextView) itemView.findViewById(R.id.whoSend);
            whatSend = (TextView) itemView.findViewById(R.id.whatSend);
            Thumbs = (ImageView) itemView.findViewById(R.id.thumbs);
            ThumbsNum = (TextView) itemView.findViewById(R.id.thumbsNum);
            Crawl=(ImageView) itemView.findViewById(R.id.crawl_image);
        }
    }
}