package com.example.tokenviewer.services;

import com.example.tokenviewer.Util.Utils;
import com.example.tokenviewer.models.Config;
import com.example.tokenviewer.models.HttpResponse;
import com.example.tokenviewer.models.Token;

import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class DataHelper {
    DataBaseHelper dbInstance;
    public DataHelper(DataBaseHelper dbInstance){
        this.dbInstance = dbInstance;
    }
    public String getConfigData(Config config) throws Exception{
        HttpBackendService service = new HttpBackendService(config.Url, null);
        HttpResponse response;
        if(config.TokenName != null){
            Token token = dbInstance.getToken(config.TokenName);
            ValidateToken(token);
            response = service.doGet(token.AccessToken);
        } else {
            response = service.doGet(null);
        }
        JSONObject json = new JSONObject(response.responseMsz);
        String data = Utils.getValueFromJson(json, config.Accessor);
        return data;
    }

    public void ValidateToken(Token token) throws Exception{
        Duration duration = Duration.between(LocalDateTime.now(), token.Validity);
        long diff = duration.getSeconds();
        if(diff < 20) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("client_id", token.ClientId);
            data.put("client_secret", token.ClientSecret);
            data.put("refresh_token", token.RefreshToken);
            data.put("grant_type", "refresh_token");
            HttpBackendService service = new HttpBackendService(token.RefreshUrl, data);
            HttpResponse response = service.doPost(null);
            JSONObject jsonData = new JSONObject(response.responseMsz);
            token.AccessToken = jsonData.getString("access_token");
            LocalDateTime dateTime = LocalDateTime.now();
            token.Validity = dateTime.plusSeconds(jsonData.getInt("expires_in"));;
            dbInstance.updateToken(token);
        }
    }
}
