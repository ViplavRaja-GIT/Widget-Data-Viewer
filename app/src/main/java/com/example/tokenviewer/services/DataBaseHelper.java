package com.example.tokenviewer.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tokenviewer.models.Config;
import com.example.tokenviewer.models.Token;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data.db";
    public static final String TOKEN_TABLE_NAME = "Tokens";
    public static final String CONFIG_TABLE_NAME = "Configs";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder table = new StringBuilder("create table " + TOKEN_TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,");
        table.append("Name TEXT,");
        table.append("AccessToken TEXT,");
        table.append("RefreshToken TEXT,");
        table.append("ClientId TEXT,");
        table.append("ClientSecret TEXT,");
        table.append("RefreshUrl TEXT,");
        table.append("Validity TEXT)");
        sqLiteDatabase.execSQL(table.toString());

        StringBuilder configTable = new StringBuilder("create table " + CONFIG_TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,");
        configTable.append("Name TEXT,");
        configTable.append("TokenName TEXT,");
        configTable.append("FetchUrl TEXT,");
        configTable.append("Accessor TEXT)");
        sqLiteDatabase.execSQL(configTable.toString());
    }

    public void createTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder configTable = new StringBuilder("create table " + CONFIG_TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,");
        configTable.append("Name TEXT,");
        configTable.append("TokenName TEXT,");
        configTable.append("FetchUrl TEXT,");
        configTable.append("Accessor TEXT)");
        db.execSQL(configTable.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TOKEN_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+CONFIG_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertToken(Token token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", token.Name);
        contentValues.put("AccessToken", token.AccessToken);
        contentValues.put("RefreshToken", token.RefreshToken);
        contentValues.put("ClientId", token.ClientId);
        contentValues.put("ClientSecret", token.ClientSecret);
        contentValues.put("RefreshUrl", token.RefreshUrl);
        contentValues.put("Validity", token.Validity.toString());
        long result = db.insert(TOKEN_TABLE_NAME, null, contentValues);
        db.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    public void updateToken(Token token){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("AccessToken", token.AccessToken);
        contentValues.put("RefreshToken", token.RefreshToken);
        contentValues.put("Validity", token.Validity.toString());
        db.update(TOKEN_TABLE_NAME, contentValues,"Id=?", new String[]{token.Id});
        db.close();
    }

    public void deleteTokensData(String name)    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TOKEN_TABLE_NAME,"Name='" + name+"'", null);
        db.close();
    }

    public Token getToken(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TOKEN_TABLE_NAME+" WHERE NAME = '"+name+"'",null);
        res.moveToNext();
        Token token = new Token();
        token.Id = res.getString(0);
        token.Name = res.getString(1);
        token.AccessToken = res.getString(2);
        token.RefreshToken = res.getString(3);
        token.ClientId = res.getString(4);
        token.ClientSecret = res.getString(5);
        token.RefreshUrl = res.getString(6);
        token.Validity = LocalDateTime.parse(res.getString(7));
        return token;
    }

    public List<String> getAllTokenNames()
    {
        try {
            List<String> names = new ArrayList<String>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select DISTINCT Name from " + TOKEN_TABLE_NAME, null);
            while (res.moveToNext()) {
                names.add(res.getString(0));
            }
            return names;
        }
        catch(java.lang.NullPointerException e){
            return null;
        }
    }

    public boolean insertConfig(Config config) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", config.Name);
        contentValues.put("TokenName", config.TokenName);
        contentValues.put("FetchUrl", config.Url);
        contentValues.put("Accessor", config.Accessor);
        long result = db.insert(CONFIG_TABLE_NAME, null, contentValues);
        db.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    public List<Config> getAllConfigs() {
        try {
            List<Config> configs = new ArrayList<Config>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select DISTINCT * from " + CONFIG_TABLE_NAME, null);
            while (res.moveToNext()) {
                Config c = new Config(
                    res.getInt(0),
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4)
                );
                configs.add(c);
            }
            return configs;
        }
        catch(Exception e){
            return null;
        }
    }

    public void deleteConfig(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CONFIG_TABLE_NAME,"Name='" + name+"'", null);
        db.close();
    }
}
