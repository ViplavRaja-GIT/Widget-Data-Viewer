package com.example.tokenviewer.models;

public class HttpResponse {

    public int statusCode;
    public String responseMsz;

    public HttpResponse(int statusCode, String responseMsz){
        this.statusCode = statusCode;
        this.responseMsz = responseMsz;
    }
}
