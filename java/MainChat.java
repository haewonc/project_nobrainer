package com.example.nobrainer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.weights.WeightInit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;


import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import java.util.Timer;
import java.util.TimerTask;

public class MainChat extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        GoogleApiClient.OnConnectionFailedListener {


    MyTimer myTimer;
    TimerTask timerTask;
    Timer timer = new Timer();
    TextView textView;

    class MyTimer extends CountDownTimer {
        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }


        @Override
        public void onFinish() {
        }
    }

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    FirebaseDatabase database;
    DatabaseReference mRef, cRef;
    SwipeRefreshLayout mSwipe;
    public messageAdapter adapter;
    private GoogleApiClient mGoogleApiClient;

    private String mUsername;
    private String mPhotoUrl;
    Integer chatTime;

    private EditText mMessageEditText;
    public String chatCode, chatKey;
    ArrayList<wordTok> getArr=new ArrayList<>();

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    ArrayList<ChatMessage> list = new ArrayList<>();
    ArrayList<messageSort> myList = new ArrayList<>();
    ArrayList<messageSort> allList = new ArrayList<>();
    TextView codeview,keyview;

    RecyclerView MyRecyclerView;
    LinearLayoutManager MyLayoutManager;
    RecyclerView allRecyclerView;
    LinearLayoutManager allLayoutManager;

    public myAdapter MyAdapter;
    public sortAdapter AllAdapter;
    public String uid, url;
    final int NUM_SAMPLES=5;

    String keyword_for_text = "대회";
    String keyword_for_image = "고양이";
    String url_image = "https://search.naver.com/search.naver?where=image&sm=tab_jum&query=" + keyword_for_image;
    String text_2 = "https://news.sbs.co.kr/news/search/main.do?query=" + keyword_for_text + "&pageIdx=1&searchOption=1&collection=";
    String text_3 = "http://news.kbs.co.kr/search/search.do?query=" + keyword_for_text+"#1";
    ArrayList<String> htmlresult= new ArrayList<>();
    String imgresult = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        DrawerLayout mDrawerLayout;
        ActionBarDrawerToggle toggle;
        NavigationView navView;
        textView = findViewById(R.id.textView2);
        mRecyclerView = findViewById(R.id.message_recycler_view);
        MyRecyclerView = findViewById(R.id.recyclerMy);
        allRecyclerView = findViewById(R.id.recyclerAll);
        codeview=findViewById(R.id.codeView);
        keyview=findViewById(R.id.keyView);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        MyLayoutManager = new LinearLayoutManager(this);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        allLayoutManager = new LinearLayoutManager(this);
        allLayoutManager.setOrientation(RecyclerView.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_chat);
        mSwipe.setOnRefreshListener(this);
        Intent intent = getIntent();
        chatCode = intent.getStringExtra("chatCode");
        chatKey = intent.getStringExtra("chatKey");

        url_image = "https://search.naver.com/search.naver?where=image&sm=tab_jum&query=" + chatKey;
        text_2 = "https://news.sbs.co.kr/news/search/main.do?query=" +chatKey + "&pageIdx=1&searchOption=1&collection=";
        text_3 = "http://news.kbs.co.kr/search/search.do?query=" + chatKey+"#1";


        chatTime = Integer.parseInt(intent.getStringExtra("chatTime"));

        final String chatName = intent.getStringExtra("chatName");
        final String chatColor = intent.getStringExtra("chatColor");
        TextView textName;
        textName = findViewById(R.id.mainChatName);
        textName.setText(chatName);
        codeview.setText(chatCode);
        keyview.setText(chatKey);
        codeview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("ID",chatCode);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(),"코드가 복사되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        textView.setText(chatTime * 60 + " 초");
        mMessageEditText = findViewById(R.id.message_edit);
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("CHATS").child(chatCode);
        myTimer = new MyTimer(chatTime * 1000, 1000);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mRecyclerView = findViewById(R.id.message_recycler_view);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        uid = mFirebaseUser.getUid();
        adapter = new messageAdapter(this, chatCode, uid);
        mRecyclerView.setAdapter(adapter);
        mUsername = mFirebaseUser.getDisplayName();
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }

        onRefresh();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                DatabaseReference tRef = database.getReference("USERS").child(uid).child("Chats").child(chatCode).child("MY");
                tRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myList.clear();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            messageSort single = new messageSort("a", "a", 0);
                            if (userSnapshot.child("Text").getValue() != null)
                                single.setText(userSnapshot.child("Text").getValue().toString());
                            else continue;
                            if (userSnapshot.child("Name").getValue() != null)
                                single.setName(userSnapshot.child("Name").getValue().toString());
                            else continue;
                            if (userSnapshot.child("Thumbs").getValue() != null)
                                single.setThumb(Integer.parseInt(userSnapshot.child("Thumbs").getValue().toString()));
                            else continue;
                            myList.add(single);
                        }
                        MyAdapter.setItems(myList);
                        MyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });


        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        DatabaseReference chatRef = database.getReference("CHATS").child(chatCode).child("MEM");
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String newId = dataSnapshot.getKey();
                if (newId.equals(uid)) return;
                DatabaseReference newRef = database.getReference("USERS").child(newId).child("Chats");
                newRef.child(chatCode).child("CODE").setValue(chatCode);
                newRef.child(chatCode).child("NAME").setValue(chatName);
                newRef.child(chatCode).child("TIME").setValue(String.valueOf(chatTime));
                newRef.child(chatCode).child("COLOR").setValue(chatColor);
                newRef.child(chatCode).child("KEY").setValue(chatKey);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        MyRecyclerView.setLayoutManager(MyLayoutManager);
        allRecyclerView.setLayoutManager(allLayoutManager);
        MyAdapter = new myAdapter(this);
        AllAdapter = new sortAdapter(this);
        MyRecyclerView.setAdapter(MyAdapter);
        allRecyclerView.setAdapter(AllAdapter);

        DatabaseReference tRef = database.getReference("USERS").child(uid).child("Chats").child(chatCode).child("MY");
        tRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    messageSort single = new messageSort("a", "a", 0);
                    if (userSnapshot.child("Text").getValue() != null)
                        single.setText(userSnapshot.child("Text").getValue().toString());
                    else continue;
                    if (userSnapshot.child("Name").getValue() != null)
                        single.setName(userSnapshot.child("Name").getValue().toString());
                    else continue;
                    if (userSnapshot.child("Thumbs").getValue() != null)
                        single.setThumb(Integer.parseInt(userSnapshot.child("Thumbs").getValue().toString()));
                    else continue;
                    myList.add(single);
                }
                MyAdapter.setItems(myList);
                MyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentTime = sdf.format(new Date());
                Map<String, Object> data = new HashMap<>();
                data.put("Text", mMessageEditText.getText().toString());
                data.put("Name", mUsername);
                data.put("Photo", mPhotoUrl);
                data.put("Time", currentTime);
                data.put("Thumbs", 0);
                mRef.child("MESS").child(currentTime).setValue(data);
                SharedPreferences prefs = getSharedPreferences("Pref", MODE_PRIVATE);
                boolean firstMess = prefs.getBoolean(chatCode, true);
                if (firstMess) {
                    prefs.edit().putBoolean("firstMess", false).apply();
                    cRef = mRef.child("MESS");
                    cRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot == null) return;
                            list.clear();
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                ChatMessage single = new ChatMessage("tt", "tt", "tt", "tt", "tt", 0);
                                if (userSnapshot.child("Text").getValue() != null)
                                    single.setText(userSnapshot.child("Text").getValue().toString());
                                else continue;
                                if (userSnapshot.child("Name").getValue() != null)
                                    single.setName(userSnapshot.child("Name").getValue().toString());
                                else continue;
                                if (userSnapshot.child("Photo").getValue() != null)
                                    single.setPhotoUrl(userSnapshot.child("Photo").getValue().toString());
                                else continue;
                                if (userSnapshot.child("Time").getValue() != null)
                                    single.setTime(userSnapshot.child("Time").getValue().toString());
                                else continue;
                                if (userSnapshot.child("ImageURL").getValue() != null)
                                    single.setImageUrl(userSnapshot.child("ImageURL").getValue().toString());
                                else single.setImageUrl("noImage");
                                if (userSnapshot.child("Thumbs").getValue() != null)
                                    single.setThumb(Integer.parseInt(userSnapshot.child("Thumbs").getValue().toString()));
                                else continue;
                                list.add(single);
                            }
                            adapter.setItems(list);
                            adapter.notifyDataSetChanged();

                            int len = list.size();
                            allList.clear();
                            for (int i = 0; i < len; i++) {
                                messageSort single = new messageSort(list.get(i).getText(), list.get(i).getName(), list.get(i).getThumb());
                                if (single.getThumb() != 0) allList.add(0, single);
                            }
                            Collections.sort(allList);
                            if(allList.size()>0)
                            {
                                chatKey=allList.get(0).getText();
                                url_image = "https://search.naver.com/search.naver?where=image&sm=tab_jum&query=" + chatKey;
                                text_2 = "https://news.sbs.co.kr/news/search/main.do?query=" +chatKey + "&pageIdx=1&searchOption=1&collection=";
                                text_3 = "http://news.kbs.co.kr/search/search.do?query=" + chatKey+"#1";
                            }
                            AllAdapter.setItems(allList);
                            AllAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                mMessageEditText.setText("");
            }
        });
    }

/*
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.timer:

                //Toast.makeText(MainActivity.this, "Time end",Toast.LENGTH_SHORT).show();
                //timer.schedule(timerTask,0 ,100);
                //stopTimerTask();
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    public void clickHandler(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                startTimerTask();
                break;
            case R.id.btnReset:
                stopTimerTask();
                break;
        }
    }

    private void startTimerTask() {
        stopTimerTask();

        final SharedPreferences prefs;
        prefs=getSharedPreferences("Pref",MODE_PRIVATE);
        if(prefs.getBoolean(chatCode+"Timer",false)) return;
        prefs.edit().putBoolean(chatCode+"Timer",true).apply();
        database.getReference("CHATS").child(chatCode).child("CONTIME").setValue(String.valueOf(chatTime*60));

        timerTask = new TimerTask() {
            int count = chatTime * 60;

            @Override
            public void run() {
                if (count == 1) {

                    prefs.edit().putBoolean(chatCode + "Timer", false).apply();
                    database.getReference("CHATS").child(chatCode).child("CONTIME").removeValue();
                    //ToDo: Firebase에 push
                    stopTimerTask();

                    // start crawling

                    JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                    jsoupAsyncTask.execute();
                    //AsyncTaskRunner asyncTask = new AsyncTaskRunner(getArr);
                    //asyncTask.execute();
                }
                count--;
                database.getReference("CHATS").child(chatCode).child("CONTIME").setValue(String.valueOf(count));

                /*textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(count + " 초");
                    }
                });*/
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void stopTimerTask() {
        if (timerTask != null) {
            textView.setText(chatTime * 60 + " 초");
            SharedPreferences prefs=getSharedPreferences("Pref",MODE_PRIVATE);
            prefs.edit().putBoolean(chatCode+"Timer",false).apply();
            database.getReference("CHATS").child(chatCode).child("CONTIME").removeValue();
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void onRefresh() {
        mSwipe.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(allList.size()>0)
                {
                    chatKey=allList.get(0).getText();
                    url_image = "https://search.naver.com/search.naver?where=image&sm=tab_jum&query=" + chatKey;
                    text_2 = "https://news.sbs.co.kr/news/search/main.do?query=" +chatKey + "&pageIdx=1&searchOption=1&collection=";
                    text_3 = "http://news.kbs.co.kr/search/search.do?query=" + chatKey+"#1";
                }
                cRef = mRef.child("MESS");
                cRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null) return;
                        list.clear();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            ChatMessage single = new ChatMessage("tt", "tt", "tt", "tt", "tt", 0);
                            if (userSnapshot.child("Text").getValue() != null)
                                single.setText(userSnapshot.child("Text").getValue().toString());
                            else continue;
                            if (userSnapshot.child("Name").getValue() != null)
                                single.setName(userSnapshot.child("Name").getValue().toString());
                            else continue;
                            if (userSnapshot.child("Photo").getValue() != null)
                                single.setPhotoUrl(userSnapshot.child("Photo").getValue().toString());
                            else continue;
                            if (userSnapshot.child("Time").getValue() != null)
                                single.setTime(userSnapshot.child("Time").getValue().toString());
                            else continue;
                            if (userSnapshot.child("ImageURL").getValue() != null)
                                single.setImageUrl(userSnapshot.child("ImageURL").getValue().toString());
                            else single.setImageUrl("noImage");
                            if (userSnapshot.child("Thumbs").getValue() != null)
                                single.setThumb(Integer.parseInt(userSnapshot.child("Thumbs").getValue().toString()));
                            else continue;
                            list.add(single);
                        }
                        adapter.setItems(list);
                        adapter.notifyDataSetChanged();
                        int len = list.size();
                        allList.clear();
                        for (int i = 0; i < len; i++) {
                            messageSort single = new messageSort(list.get(i).getText(), list.get(i).getName(), list.get(i).getThumb());
                            if (single.getThumb() != 0) allList.add(0, single);
                        }
                        Arrays.sort(new ArrayList[]{allList});
                        AllAdapter.setItems(allList);
                        AllAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                });
                mSwipe.setRefreshing(false);
            }
        }, 1000);

        DatabaseReference ctRef= database.getReference("CHATS").child(chatCode).child("CONTIME");
        ctRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    String update=dataSnapshot.getValue().toString();
                    textView.setText(update+" 초");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            htmlresult.clear();
            try {
                Document doc ;
                Elements titles;

                doc = Jsoup.connect(text_2).get();
                titles = doc.select("a");
                for (Element k : titles) {
                    String href = k.attr("abs:href");
                    if (href.contains("https://news.sbs.co.kr/news/endPage.do?news_id=N")) {
                        doc = Jsoup.connect(href).get();
                        titles = doc.select("div.text_area");
                        for (Element j : titles) {
                            System.out.println("title sbs: " + j.text());
                            String a = j.text().trim();
                            htmlresult.add(a);
                        }
                    }
                }

                doc = Jsoup.connect(text_3).get();
                titles = doc.select("a");
                for (Element k : titles) {
                    String href = k.attr("abs:href");

                    if (href.contains("http://news.kbs.co.kr/news/view.do?ncd=")) {
                        doc = Jsoup.connect(href).get();
                        titles = doc.select("div#cont_newstext.detail-body.font-size");
                        for (Element j : titles) {
                            System.out.println("title kbs: " + j.text());
                            String a = j.text().trim();
                            htmlresult.add(a);
                        }
                    }
                }

                doc = Jsoup.connect(url_image).get();
                Elements image = doc.select("div.img_area._item");
                System.out.println(image.select("img").attr("src"));
                for (Element e : image) {
                    for (Element k : e.children()) {
                        for (Element y : k.children()) {
                            System.out.println(y);
                            Elements i = y.getElementsByTag("img");
                            for (Element j : i) {
                                Elements l = j.getElementsByTag("data-source");

                                System.out.println("data : " + j.data() + "\n tag : " + j.tag() + "\n attibutes : " + j.attributes() + "\nsrc : " + j.attr("src"));
                                System.out.println(j.attr("src"));
                                String data = "";
                                data = data + j.attributes();
                                int start = data.indexOf("data-source") + 13;
                                int last = data.indexOf("data-width") - 2;
                                data = data.substring(start, last);
                                imgresult=data;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //a: text result, imgresult: img result
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            Map<String, Object> data = new HashMap<>();
            data.put("Text", "이미지 추천");
            data.put("Name","AI Helper");
            data.put("Photo","thisIsAI");
            data.put("Time", currentTime);
            data.put("ImageURL",imgresult);
            data.put("Thumbs", 0);
            mRef.child("MESS").child(currentTime).setValue(data);
            nlpUtil nlp=new nlpUtil(htmlresult,chatCode);
            getArr=nlp.wordAll;
        }
    }

    private class AsyncTaskRunner extends AsyncTask<Double, Void, String>
    {
        ArrayList<wordTok> words;
        AsyncTaskRunner(ArrayList<wordTok> n)
        {
            words=n;
        }
        INDArray trainingInputs;
        INDArray trainingOutputs;
        MultiLayerNetwork myNetwork;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Double... doubles) {
            DenseLayer inputLayer= new DenseLayer.Builder()
                    .nIn(4)
                    .nOut(8)
                    .name("INPUT")
                    .build();
            DenseLayer hiddenLayer1 = new DenseLayer.Builder()
                    .nIn(8)
                    .nOut(8)
                    .name("Hidden1")
                    .build();

            DenseLayer hiddenLayer2 = new DenseLayer.Builder()
                    .nIn(8)
                    .nOut(8)
                    .name("Hidden2")
                    .build();

            OutputLayer outputLayer = new OutputLayer.Builder()
                    .nIn(8)
                    .nOut(1)
                    .name("Output")
                    .build();
            long seed = 6;
            NeuralNetConfiguration.Builder nncBuilder = new NeuralNetConfiguration.Builder()
                    .seed(seed)
                    .activation(Activation.TANH)
                    .weightInit(WeightInit.XAVIER);

            NeuralNetConfiguration.ListBuilder listBuilder = nncBuilder.list();
            listBuilder.layer(0, inputLayer);
            listBuilder.layer(1, hiddenLayer1);
            listBuilder.layer(2, hiddenLayer2);
            listBuilder.layer(3, outputLayer);

            //listBuilder.backpropType(BackpropType.Standard);

            myNetwork = new MultiLayerNetwork(listBuilder.build());
            myNetwork.init();
            INDArray trainingInputs = Nd4j.zeros(NUM_SAMPLES, 4);
            INDArray trainingOutputs = Nd4j.zeros(NUM_SAMPLES, 1);
            return "";

        }

        @Override
        protected void onPostExecute(String result)
        {
            INDArray out=myNetwork.output(trainingInputs);
            ArrayList<Double> rt=new ArrayList<>();
            for(int i=0;i<NUM_SAMPLES;i++) {
                rt.add(out.getDouble(i, 0));
                if(rt.get(i)>0.5)
                {
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String currentTime = sdf.format(new Date());
                    Map<String, Object> data = new HashMap<>();
                    data.put("Text", words.get(i).word);
                    data.put("Name","AI Helper");
                    data.put("Photo","thisIsAI");
                    data.put("Time", currentTime);
                    data.put("Thumbs", 0);
                    data.put("ImageURL","noImage");
                    mRef.child("MESS").child(currentTime).setValue(data);
                }
            }
        }
    }
}
