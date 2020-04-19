package com.example.nobrainer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class newChat extends AppCompatActivity {

    private String makeRand()
    {
        Random rnd = new Random();
        String randomStr = String.valueOf((char) ((int) (rnd.nextInt(26)) + 65));
        return randomStr;
    }
    private String makeStr()
    {
        String rt=new String();
        for(int i=0;i<6;i++)
        {
            rt+=makeRand();
        }
        return rt;
    }
    private String makeCol()
    {
        Random rnd=new Random();
        ArrayList<String> arr=new ArrayList<>();
        arr.add(0,"FFB5E8");
        arr.add(0,"C5A3FF");
        arr.add(0,"85E3FF");
        arr.add(0,"FFC9DE");
        arr.add(0,"AFF8DB");
        arr.add(0,"ACE7FF");
        arr.add(0,"E7FFAC");
        arr.add(0,"FCC2FF");
        int r=rnd.nextInt(arr.size());
        return "#"+arr.get(r);
    }
    EditText prevCode,chatName,chatKey,chatTime;
    Button but1, but2;
    SharedPreferences prefs;
    FirebaseDatabase database;
    DatabaseReference userRef,chatRef,mRef;
    String uid;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    String mUsername,mPhotoUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        database=FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            return;
        }
        else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
        prefs=getSharedPreferences("Pref",MODE_PRIVATE);
        uid=mFirebaseUser.getUid();
        prefs.edit().putString("UserId", uid).apply();
        prefs.edit().putString("UserName", mUsername).apply();
        prefs.edit().putString("UserPhoto",mPhotoUrl).apply();
        mRef=database.getReference("USERS").child(uid);

        chatRef=database.getReference("CHATS");
        userRef=database.getReference("USERS").child(uid).child("Chats");
        prevCode=(EditText)findViewById(R.id.chatCode);
        but1 = (Button) findViewById(R.id.prevChatSend);
        chatName=(EditText)findViewById(R.id.chatName);
        chatKey=(EditText)findViewById(R.id.chatKey);
        chatTime=(EditText)findViewById(R.id.chatTime);
        but2 = (Button) findViewById(R.id.newChatSend);
        but1.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String code=prevCode.getText().toString();
                chatRef.child(code).child("MEM").child(uid).setValue(uid);
                finish();
            }
        });
        but2.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String ChatCode=makeStr();
                String RandCol=makeCol();
                userRef.child(ChatCode).child("CODE").setValue(ChatCode);
                userRef.child(ChatCode).child("NAME").setValue(chatName.getText().toString());
                userRef.child(ChatCode).child("COLOR").setValue(RandCol);
                userRef.child(ChatCode).child("TIME").setValue(chatTime.getText().toString());
                userRef.child(ChatCode).child("KEY").setValue(chatKey.getText().toString());
                chatRef.child(ChatCode).child("MEM").child(uid).setValue(uid);

                chatRef.child(ChatCode).child("TIME").setValue(chatTime.getText().toString());
                chatRef.child(ChatCode).child("KEY").setValue(chatKey.getText().toString());
                Intent intent=new Intent(newChat.this,ChatCheck.class);
                intent.putExtra("chatCode",ChatCode);
                startActivity(intent);
                finish();
            }
        });
    }


}
