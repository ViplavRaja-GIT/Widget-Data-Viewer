package com.example.tokenviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tokenviewer.models.HttpResponse;
import com.example.tokenviewer.models.Token;
import com.example.tokenviewer.services.DataBaseHelper;
import com.example.tokenviewer.services.HttpBackendService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AddTokenConfig extends AppCompatActivity {

    DataBaseHelper dbInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_config);
        dbInstance = new DataBaseHelper(this);
        EditText name =  (EditText) findViewById(R.id.token_name);
        EditText accessToken =  (EditText) findViewById(R.id.access_token);
        EditText refreshToken =  (EditText) findViewById(R.id.refresh_token);
        EditText clientId =  (EditText) findViewById(R.id.client_id);
        EditText clientSecret =  (EditText) findViewById(R.id.client_secret);
        EditText refreshUrl =  (EditText) findViewById(R.id.refresh_api);
        Button addToken = (Button) findViewById(R.id.add_token);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        addToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Token token = new Token();
                token.Name = name.getText().toString();
                token.AccessToken = accessToken.getText().toString();
                token.RefreshToken = refreshToken.getText().toString();
                token.ClientId = clientId.getText().toString();
                token.ClientSecret = clientSecret.getText().toString();
                token.RefreshUrl = refreshUrl.getText().toString();
                if(ValidateToken(token, view)){
                    Snackbar.make(view, "Token Added Successfully.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    public boolean ValidateToken(Token token, View view){
        JSONObject jsonData = null;
        try{
            Map<String, String> data = new HashMap<String, String>();
            data.put("client_id", token.ClientId);
            data.put("client_secret", token.ClientSecret);
            data.put("refresh_token", token.RefreshToken);
            data.put("grant_type", "refresh_token");
            HttpBackendService service = new HttpBackendService(token.RefreshUrl, data);
            HttpResponse response = service.doPost(null);
            jsonData = new JSONObject(response.responseMsz);
            token.AccessToken = jsonData.getString("access_token");
            LocalDateTime dateTime = LocalDateTime.now();
            token.Validity = dateTime.plusSeconds(jsonData.getInt("expires_in"));;
            dbInstance.insertToken(token);
            return true;
        } catch (Exception ex){
            StringBuilder error = new StringBuilder(jsonData.toString());
            error.append(ex.getMessage());
            Snackbar.make(view, error.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }
    }
}