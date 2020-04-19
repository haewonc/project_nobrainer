package com.example.nobrainer;

public class ChatMessage {
    private String name;
    private String text;
    private String photoUrl;
    private String imageUrl;
    private String time;
    Integer thumb;

    public ChatMessage (String text, String name, String photoUrl, String imageUrl, String time, Integer thumb){
        this.text=text;
        this.name=name;
        this.photoUrl=photoUrl;
        this.imageUrl=imageUrl;
        this.time=time;
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
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl=photoUrl;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl=imageUrl;
    }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public Integer getThumb() { return thumb; }
    public void setThumb(Integer thumb) { this.thumb = thumb; }
}
