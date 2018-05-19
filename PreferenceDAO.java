package com.worldnews.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public class PreferenceDAO extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "news_preference";
    private static final String DB_TABLE = "preferences";
    private static final String KEY_WORDS = "keywords";

    public PreferenceDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ARTICLE_TABLE = "CREATE TABLE " + DB_TABLE + "("+ KEY_WORDS + " TEXT PRIMARY KEY" + ")";
        db.execSQL(CREATE_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DB_TABLE);
        onCreate(db);
    }

    public void savePreferences(String[] list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues tuples = new ContentValues();
        for(String key:list) {
            tuples.put(KEY_WORDS, key.toLowerCase());
            if(db.insert(DB_TABLE, null, tuples)==-1)
            break;
        }
    }

    public List<String> getPreferences() {
        List<String> preferences = new LinkedList<>();
        String query = "select * from "+DB_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor it = db.rawQuery(query,null);

        if(it!=null && it.moveToFirst()) {
            do {
                preferences.add(it.getString(0));
            } while (it.moveToNext());
            it.close();
        }
        db.close();
        return preferences;
    }
}
