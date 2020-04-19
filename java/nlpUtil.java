package com.example.nobrainer;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

public class nlpUtil {
    public ArrayList<String> totalArr=new ArrayList<>();
    public ArrayList<Integer> totalIdx=new ArrayList<>();
    public ArrayList<wordTok> wordAll=new ArrayList<>();
    public ArrayList<String> words=new ArrayList<>();
    public nlpUtil(ArrayList<String> args, String chatCode) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        int docLen = args.size();
        for (int doc = 0; doc < docLen; doc++) {
            int[] inThis=new int[100000];
            String strToAnalyze = args.get(doc);
            KomoranResult analyzeResultList = komoran.analyze(strToAnalyze);
            List<Token> tokenList = analyzeResultList.getTokenList();
            for (Token token : tokenList) {
                String thisStr=token.getMorph();
                int putIdx=-1;
                if(token.getPos().equals("NNP"))
                {
                    int len=words.size();
                    int sameIdx=len;
                    int i=0;
                    for(i=0;i<len;i++)
                    {
                        if(thisStr.equals(words.get(i)))
                        {
                            sameIdx=i;
                            putIdx=sameIdx;
                            break;
                        }
                    }
                    if(i<len)
                    {
                        if(inThis[sameIdx]==0)
                        {
                            inThis[sameIdx]=1;
                            wordAll.get(sameIdx).docMany+=1;
                            wordAll.get(sameIdx).many+=1;
                        }
                        else
                        {
                            wordAll.get(sameIdx).many+=1;
                        }
                    }
                    else
                    {
                        inThis[len]=1;
                        putIdx=len;
                        words.add(thisStr);
                        wordTok n=new wordTok(thisStr,1,1,
                                token.getEndIndex()-token.getBeginIndex());
                        wordAll.add(0,n);
                    }
                    totalArr.add(thisStr);
                    totalIdx.add(putIdx);
                }
            }
        }
        ArrayList<ArrayList<Integer>> conList=new ArrayList<>();
        ArrayList<Integer> contTot=new ArrayList<>();
        int lenFor=totalArr.size();
        final int conLen=3;
        for(int i=0;i<lenFor;i++)
        {
            int s=Math.max(i-conLen,0);
            int e=Math.min(lenFor-1,i+conLen);
            for(int j=s;j<=e;j++)
            {
                if(j==i||totalIdx.get(j).equals(totalIdx.get(i)))
                {
                    continue;
                }
                else
                {
                    int my=totalIdx.get(i);
                    int con=totalIdx.get(j);
                    wordAll.get(my).conList.add(con);
                    wordAll.get(my).conTot+=1;
                }
            }
        }
        int lenWords=wordAll.size();
        final int converge=10;
        for(int ep=0;ep<converge;ep++)
        {
            for(int i=0;i<lenWords;i++)
            {
                Double tot=0.0;
                for(int j=0;j<wordAll.get(i).conTot;j++)
                {
                    tot+=wordAll.get(wordAll.get(i).conList.get(j)).val/wordAll.get(wordAll.get(i).conList.get(j)).conTot;
                    Log.d(this.getClass().getName(),"Value plus: "+String.valueOf(tot));
                }
                wordAll.get(i).val=tot;
            }
        }
        Collections.sort(wordAll);
        FirebaseDatabase database;
        DatabaseReference mRef;
        database=FirebaseDatabase.getInstance();
        mRef=database.getReference("CHATS").child(chatCode);
        for(int i=0;i<5;i++)
        {
            Log.d(this.getClass().getName(),"Value "+String.valueOf(wordAll.get(i).val));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            Map<String, Object> dat = new HashMap<>();
            dat.put("Text", wordAll.get(i).word);
            dat.put("Name","AI Helper");
            dat.put("Photo","thisIsAI");
            dat.put("Time", currentTime);
            dat.put("Thumbs", 0);
            dat.put("ImageURL","noImage");
            mRef.child("MESS").child(currentTime+String.valueOf(i)).setValue(dat);
        }
    }
}
