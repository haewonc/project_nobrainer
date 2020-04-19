package com.example.nobrainer;

public class chatInfo {
    public String Name;
    public String Code;
    public String Color;
    public String Time;
    public String Key;

    public String getName() { return Name; }
    public String getCode() {return Code; }
    public String getColor() {return Color; }
    public String getTime() { return Time; }
    public String getKey() { return Key; }
    public void setKey(String key) { Key = key; }

    public chatInfo(String Name, String Code,String Color, String Time,String Key)
    {
        this.Name=Name;
        this.Code=Code;
        this.Color=Color;
        this.Time=Time;
        this.Key=Key;
    }
}
