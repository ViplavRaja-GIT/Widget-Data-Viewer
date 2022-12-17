package com.example.tokenviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tokenviewer.Util.Utils;
import com.example.tokenviewer.services.DataBaseHelper;

import java.util.List;

public class ShowTokens extends AppCompatActivity {

    DataBaseHelper dbInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tokens);
        ListView tokenList = (ListView) findViewById(R.id.token_list);
        dbInstance = new DataBaseHelper(this);
        List<String> tokens = dbInstance.getAllTokenNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tokens);
        tokenList.setAdapter(adapter);
        tokenList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
                // TODO Auto-generated method stub
                try {
                    String str = tokens.get(index);
                    dbInstance.deleteConfig(str);
                    adapter.remove(adapter.getItem(index));
                    adapter.notifyDataSetChanged();
                    StringBuilder msz = new StringBuilder("Deleted Token. :- ");
                    msz.append(str);
                    Utils.showSnackBar(v, msz.toString());
                }
                catch (Exception e)
                {
                    Utils.showSnackBar(v, e.getMessage());
                }
                return true;
            }
        });
    }
}