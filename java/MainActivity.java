package com.example.nobrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    public ArrayList<chatInfo> list = new ArrayList<chatInfo>();
    public chatAdapter adapter = new chatAdapter(this);
    FirebaseDatabase database;
    DatabaseReference mRef;
    SwipeRefreshLayout mSwipe;
    public SharedPreferences prefs;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        database = FirebaseDatabase.getInstance();
        /*
        ArrayList<String> input=new ArrayList<>();
        input.add("그는 양말을 신고 텀블러를 잡은 다음 기숙사 호실을 나왔다. 기숙사 호실에는 아직 그의 친구가 잠을 자고 있다. 아침 6시에 따듯하고 안락한 호실을 나오는 것은 쉽지 않다.");
        input.add("급하게 나오면서 신은 양말이라서 그는 양말이 젖어있었다는 사실을 모르고 있었다. 다시 호실에 들어갈까 생각도 했지만 그러지 않았다. 아침부터 왔던 길을 또 가기는 너무 귀찮았다.");
        nlpUtil nlp=new nlpUtil(input);
        ArrayList<wordTok> getArr=new ArrayList<>();
        getArr=nlp.wordAll;
        for(int i=0;i<getArr.size();i++)
        {
            Log.d(this.getClass().getName(),"Printing: "+getArr.get(i).word);
            Log.d(this.getClass().getName(),"Printing: "+String.valueOf(getArr.get(i).val));
        }*/

        String UserId;
        String mPhotoUrl = new String();
        String mUsername = new String();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
        UserId = mFirebaseUser.getUid();
        boolean firstRun = prefs.getBoolean("firstRun", true);
        if (firstRun) {
            database.getReference("USERS").child(UserId).setValue(UserId);
            prefs.edit().putString("UserId", UserId).apply();
            prefs.edit().putString("UserName", mUsername).apply();
            prefs.edit().putString("UserPhoto", mPhotoUrl).apply();
            prefs.edit().putBoolean("firstRun", false).apply();
        }

        mSwipe.setOnRefreshListener(this);
        onRefresh();
        mRef = database.getReference("USERS").child(UserId).child("Chats");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot == null) return;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    chatInfo single = new chatInfo("new name", "AAAAA1", "#22222", "0","dd");
                    if (userSnapshot.child("NAME").getValue() != null)
                        single.Name = userSnapshot.child("NAME").getValue().toString();
                    else continue;
                    if (userSnapshot.child("CODE").getValue() != null)
                        single.Code = userSnapshot.child("CODE").getValue().toString();
                    else continue;
                    if (userSnapshot.child("COLOR").getValue() != null)
                        single.Color = userSnapshot.child("COLOR").getValue().toString();
                    else continue;
                    if (userSnapshot.child("TIME").getValue() != null)
                        single.Time = userSnapshot.child("TIME").getValue().toString();
                    else continue;
                    if(userSnapshot.child("KEY").getValue()!=null)
                    {
                        single.Key=userSnapshot.child("KEY").getValue().toString();
                    }
                    else continue;
                    list.add(0, single);
                }
                adapter.setItems(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_LONG).show();
            }
        });

        FloatingActionButton floatbut = (FloatingActionButton) findViewById(R.id.newchat);
        floatbut.setOnClickListener(
                new FloatingActionButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, newChat.class);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public void onRefresh() {
        mSwipe.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        if (dataSnapshot == null) return;
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            chatInfo single = new chatInfo("new name", "AAAAA1", "#22222", "0","EE");
                            if (userSnapshot.child("NAME").getValue() != null)
                                single.Name = userSnapshot.child("NAME").getValue().toString();
                            else continue;
                            if (userSnapshot.child("CODE").getValue() != null)
                                single.Code = userSnapshot.child("CODE").getValue().toString();
                            else continue;
                            if (userSnapshot.child("COLOR").getValue() != null)
                                single.Color = userSnapshot.child("COLOR").getValue().toString();
                            else continue;
                            if (userSnapshot.child("TIME").getValue() != null)
                                single.Time = userSnapshot.child("TIME").getValue().toString();
                            else continue;
                            if(userSnapshot.child("KEY").getValue()!=null)
                            {
                                single.Key=userSnapshot.child("KEY").getValue().toString();
                            }
                            else continue;
                            list.add(0, single);
                        }
                        adapter.setItems(list);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                });
                mSwipe.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}