package com.example.tokenviewer.Util;

import android.view.View;

import com.example.tokenviewer.models.HttpResponse;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;

public class Utils {
    public static String getQueryString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public static String getParamsString(Map<String, String> params){
        JSONObject obj = new JSONObject(params);
        return obj.toString();
    }

    public static String getValueFromJson(JSONObject json, String accessor) throws JSONException {
        String[] accessors = accessor.split("\\.", 5);
        JSONObject top = json;
        for (int i = 0; i < accessors.length -1; i++) {
            if(accessors[i].contains("]"))
            {
                String[] acc = accessors[i].split("\\[");
                int index = Integer.parseInt(acc[1].replace("]", ""));
                JSONArray arr = (JSONArray) top.get(acc[0]);
                top = (JSONObject) arr.get(index);
            } else {
                top = (JSONObject) top.get(accessors[i]);
            }
        }
        return top.getString(accessors[accessors.length - 1]);
    }

    public static HttpResponse getFullResponse(HttpURLConnection con) throws IOException {
        StringBuilder fullResponseBuilder = new StringBuilder();
        int status = con.getResponseCode();
        Reader streamReader = null;
        if (status > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }
        StringBuilder data = new StringBuilder();
        int i=0;
        while((i=streamReader.read())!=-1){
            data.append((char)i);
        }
        streamReader.close();
        return  new HttpResponse(status, data.toString());
    }

    public static void showSnackBar(View view, String msz){
        Snackbar.make(view, msz, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
