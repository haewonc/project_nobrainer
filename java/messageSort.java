package com.example.nobrainer;

import java.util.Comparator;

public class messageSort implements Comparable<messageSort> {
    private String name;
    private String text;
    Integer thumb;

    public messageSort (String text, String name, Integer thumb){
        this.text=text;
        this.name=name;
        this.thumb=thumb;
    }
    public String getText() {
        return text;
    }
    public  void setText(String text) {
        this.text=text;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name=name;
    }
    public Integer getThumb() { return thumb; }
    public void setThumb(Integer thumb) { this.thumb = thumb; }

    @Override
    public int compareTo(messageSort t1) {
        return getThumb().compareTo(t1.getThumb());
    }
}
