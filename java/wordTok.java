package com.example.nobrainer;

import java.util.ArrayList;

public class wordTok implements Comparable<wordTok> {
    public String word;
    public Integer many;
    public Integer docMany;
    public Integer wordLen;
    public ArrayList<Integer> conList=new ArrayList<>();
    public Integer conTot;
    public Double val;
    public wordTok(String iword,Integer imany, Integer idocMany, Integer iwordLen)
    {
        this.word=iword;
        this.many=imany;
        this.docMany=idocMany;
        this.wordLen=iwordLen;
        this.conTot=0;
        this.val=20.0;
    }

    @Override
    public int compareTo(wordTok t1) {
        return val.compareTo(t1.val);
    }
}
