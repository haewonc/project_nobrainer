package com.example.nobrainer;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ItemViewHolder> {

    Context mContext;
    public ArrayList<chatInfo> items=new ArrayList<>();

    public chatAdapter (Context context)
    {
        mContext=context;
    }
    @Override
    public chatAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlist_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(chatAdapter.ItemViewHolder holder, int position) {
        final chatInfo item=items.get(position);
        holder.chatname.setVisibility(View.VISIBLE);
        holder.image.setVisibility(View.VISIBLE);
        holder.chatname.setText(item.getName());
        holder.chatId.setText(item.getCode());
        holder.chatTimeSave.setText(item.getTime());
        int color = Color.parseColor(item.getColor()); //The color u want
        holder.image.setColorFilter(color);

        final FirebaseDatabase database;
        database=FirebaseDatabase.getInstance();
        FirebaseAuth mFirebaseAuth;
        FirebaseUser mFirebaseUser;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        final String uid=mFirebaseUser.getUid();

        DatabaseReference chatRef=database.getReference("CHATS").child(item.getCode()).child("MEM");
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String newId=dataSnapshot.getKey();
                String chatCode=item.getCode();
                if(newId.equals(uid)) return;
                DatabaseReference newRef=database.getReference("USERS").child(newId).child("Chats");
                newRef.child(chatCode).child("CODE").setValue(chatCode);
                newRef.child(chatCode).child("NAME").setValue(item.getName());
                newRef.child(chatCode).child("TIME").setValue(item.getTime());
                newRef.child(chatCode).child("COLOR").setValue(item.getColor());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public void setItems(ArrayList<chatInfo> items)
    {
        this.items=items;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView chatname;
        CircleImageView image;
        TextView chatId;
        TextView chatTimeSave;

        ItemViewHolder(View itemView) {
            super(itemView);
            chatname = (TextView) itemView.findViewById(R.id.chatname);
            image=(CircleImageView)itemView.findViewById(R.id.chatColor);
            chatId=(TextView)itemView.findViewById(R.id.chatId);
            chatTimeSave=(TextView)itemView.findViewById(R.id.chatTimeSave);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Context context=v.getContext();
                    Intent intent=new Intent(context,MainChat.class);
                    intent.putExtra("chatCode",items.get(getAdapterPosition()).getCode());
                    intent.putExtra("chatTime",items.get(getAdapterPosition()).getTime());
                    intent.putExtra("chatColor",items.get(getAdapterPosition()).getColor());
                    intent.putExtra("chatName",items.get(getAdapterPosition()).getName());
                    intent.putExtra("chatKey",items.get(getAdapterPosition()).getKey());
                    context.startActivity(intent);
                }
            });
        }
    }

}



