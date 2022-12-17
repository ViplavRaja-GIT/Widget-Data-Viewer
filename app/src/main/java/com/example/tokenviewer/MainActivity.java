package com.example.tokenviewer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.tokenviewer.Util.Utils;
import com.example.tokenviewer.databinding.ActivityMainBinding;
import com.example.tokenviewer.models.Config;
import com.example.tokenviewer.models.ConfigAdapter;
import com.example.tokenviewer.services.DataBaseHelper;
import com.example.tokenviewer.services.DataHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DataBaseHelper dbInstance;
    private DataHelper helper;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Config> configs = new ArrayList<>();
    private ConfigAdapter configAdapter;
    private ListView configList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        dbInstance = new DataBaseHelper(this);
        helper = new DataHelper(dbInstance);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateAdapter();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        binding.fabToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddTokenConfig.class));
            }
        });
        binding.fabData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNewDataConfig.class));
            }
        });
        configList = (ListView) findViewById(R.id.config_list);
        configList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
                deleteConfig(v, index);
                return true;
            }
        });
        updateAdapter();
    }

    private void updateAdapter(){
        if(configAdapter == null){
            configAdapter = new ConfigAdapter(this,  android.R.layout.simple_list_item_1, configs);
            configList.setAdapter(configAdapter);
        }
        new AsyncDataHandler().execute(dbInstance.getAllConfigs().toArray(new Config[0]));
    }

    private void deleteConfig(View v, int index){
        try {
            String str = configs.get(index).Name;
            dbInstance.deleteConfig(str);
            configAdapter.remove(configAdapter.getItem(index));
            configAdapter.notifyDataSetChanged();
            StringBuilder msz = new StringBuilder("Deleted Config. :- ");
            msz.append(str);
            Utils.showSnackBar(v, msz.toString());
        }
        catch (Exception e)
        {
            Utils.showSnackBar(v, e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, ShowTokens.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncDataHandler extends AsyncTask<Config, Void, List<Config>> {
        @Override
        protected List<Config> doInBackground(Config... configs) {
            for (int i = 0; i < configs.length; i++) {
                Config c = configs[i];
                try{
                    c.data = helper.getConfigData(c);
                } catch (Exception ex){
                    StringBuilder error = new StringBuilder(c.Name);
                    error.append(" ");
                    error.append(ex.getMessage());
                    c.data = error.toString();
                }
            }
            return Arrays.asList(configs);
        }

        @Override
        protected void onPostExecute(List<Config> result) {
            configs = result;
            configAdapter.clear();
            configAdapter.addAll(configs);
            configAdapter.notifyDataSetChanged();
        }
    }
}