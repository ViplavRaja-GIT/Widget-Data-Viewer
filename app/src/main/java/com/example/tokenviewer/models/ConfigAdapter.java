package com.example.tokenviewer.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.tokenviewer.R;
import java.util.List;

public class ConfigAdapter extends ArrayAdapter<Config>{

    private List<Config> configs;

    public ConfigAdapter(Context context, int resource, List<Config> configs) {
        super(context, resource, configs);
        this.configs = configs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Config config = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
        }
        TextView configName = (TextView) convertView.findViewById(R.id.config_item_name);
        TextView configData = (TextView) convertView.findViewById(R.id.config_item_data);
        configName.setText(config.Name);
        configData.setText(config.data);
        return convertView;
    }

    @Override
    public int getCount() {
        return configs.size();
    }
}
