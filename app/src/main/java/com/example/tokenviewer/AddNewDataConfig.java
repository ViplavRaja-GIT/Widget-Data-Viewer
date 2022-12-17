package com.example.tokenviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.tokenviewer.Util.Utils;
import com.example.tokenviewer.models.Config;
import com.example.tokenviewer.models.HttpResponse;
import com.example.tokenviewer.models.Token;
import com.example.tokenviewer.services.DataBaseHelper;
import com.example.tokenviewer.services.DataHelper;
import com.example.tokenviewer.services.HttpBackendService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AddNewDataConfig extends AppCompatActivity {

    DataBaseHelper dbInstance;
    DataHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_data_config);
        dbInstance = new DataBaseHelper(this);
        helper = new DataHelper(dbInstance);
        Spinner tokens = (Spinner) findViewById(R.id.tokens);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dbInstance.getAllTokenNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tokens.setAdapter(adapter);
        EditText configName = (EditText) findViewById(R.id.config_name);
        EditText fetchUrl = (EditText) findViewById(R.id.data_api);
        EditText accessor = (EditText) findViewById(R.id.data_accessor);
        Button saveConfig = (Button) findViewById(R.id.add_config);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        saveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Config config = new Config(
                        configName.getText().toString(),
                        tokens.getSelectedItem() == null ? null : tokens.getSelectedItem().toString(),
                        fetchUrl.getText().toString(),
                        accessor.getText().toString()
                    );
                    String data = helper.getConfigData(config);
                    if(data != null){
                        dbInstance.insertConfig(config);
                        StringBuilder msz = new StringBuilder("Config added successfully. Data :- ");
                        msz.append(data);
                        Snackbar.make(view, msz, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "No data in given accessor.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }catch (Exception ex) {
                    StringBuilder error = new StringBuilder("Invalid Config :- ");
                    error.append(ex.getMessage());
                    Snackbar.make(view, error.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }
}