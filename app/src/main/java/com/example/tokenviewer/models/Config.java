package com.example.tokenviewer.models;

public class Config {

    public Config(String name, String tokenName, String url, String accessor){
        this.Name = name;
        this.TokenName = tokenName;
        this.Url = url;
        this.Accessor = accessor;
    }

    public Config(int id, String name, String tokenName, String url, String accessor){
        this.Id = id;
        this.Name = name;
        this.TokenName = tokenName;
        this.Url = url;
        this.Accessor = accessor;
    }

    public int Id;
    public String Name;
    public String TokenName;
    public String Url;
    public String Accessor;
    public String data;
}
