package com.example.tokenviewer.services;
import com.example.tokenviewer.Util.Utils;
import com.example.tokenviewer.models.HttpResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpBackendService {
    private String uri;
    private Map<String, String> data;

    public HttpBackendService(String uri, Map<String, String> data){
        this.uri = uri;
        this.data = data;
    }

    public HttpResponse doGet(String token) throws Exception{
        HttpURLConnection con = null;
        try {
            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setInstanceFollowRedirects(false);
            if(token != null){
                StringBuilder auth = new StringBuilder("Bearer ");
                auth.append(token);
                con.setRequestProperty("Authorization", auth.toString());
            }
            return Utils.getFullResponse(con);
        } catch (Exception ex){
            throw ex;
        } finally {
            if(con != null){
                con.disconnect();
            }
        }
    }

    public HttpResponse doPost(String token) throws Exception{
        HttpURLConnection con = null;
        try {
            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setInstanceFollowRedirects(false);
            if(token != null){
                StringBuilder auth = new StringBuilder("Bearer ");
                auth.append(token);
                con.setRequestProperty("Authorization", auth.toString());
            }
            if(data != null) {
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(Utils.getParamsString(data));
                out.flush();
                out.close();
            }
            return Utils.getFullResponse(con);
        } catch (Exception ex){
            throw ex;
        } finally {
            if(con != null){
                con.disconnect();
            }
        }
    }
}
