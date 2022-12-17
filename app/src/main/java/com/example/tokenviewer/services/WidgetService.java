package com.example.tokenviewer.services;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.example.tokenviewer.R;
import com.example.tokenviewer.models.Config;

import java.util.List;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory(getApplicationContext());
    }

    class RemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private final Context context;
        private DataBaseHelper dbInstance;
        private List<Config> configs;
        private DataHelper helper;

        public RemoteViewsFactory(Context context) {
            this.context = context;
            this.dbInstance = new DataBaseHelper(context);
            this.helper = new DataHelper(dbInstance);
        }

        @Override
        public void onCreate() {
            configs = dbInstance.getAllConfigs();
            fetchAllConfigData(configs);
        }

        @Override
        public void onDataSetChanged() {
            configs = dbInstance.getAllConfigs();
            fetchAllConfigData(configs);
        }

        @Override
        public void onDestroy() {
            configs.clear();
        }

        @Override
        public int getCount() {
            return configs.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Config config = configs.get(position);
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.activity_listview);
            remoteView.setTextViewText(R.id.config_item_name, config.Name);
            remoteView.setTextViewText(R.id.config_item_data, config.data);
            // Removed code that sets the other fields as I tried it with and without and it didn't matter. So removed for brevity

            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return configs.get(position).Id;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public void fetchAllConfigData(List<Config> configs) {
            for (int i = 0; i < configs.size(); i++) {
                Config c = configs.get(i);
                try {
                    c.data = helper.getConfigData(c);
                } catch (Exception ex) {
                    StringBuilder error = new StringBuilder(c.Name);
                    error.append(" ");
                    error.append(ex.getMessage());
                    c.data = error.toString();
                }
            }
        }
    }
}
