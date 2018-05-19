package com.worldnews.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.worldnews.article.Article;

import java.util.LinkedList;
import java.util.List;

public class ArticleDAO extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "news_article";
    private static final String DB_TABLE = "article";

    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "link";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DESCR = "description";
    private static final String KEY_WORDS = "keywords";

    public ArticleDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ARTICLE_TABLE = "CREATE TABLE " + DB_TABLE + "("+
                KEY_TITLE + " TEXT," + KEY_LINK + " TEXT PRIMARY KEY," + KEY_DATE + " TEXT,"+ KEY_TIME+" TEXT,"+
                KEY_SOURCE + " TEXT," + KEY_IMAGE + " TEXT," + KEY_DESCR + " TEXT" + ")";
        Log.d(ArticleDAO.class.getName(),CREATE_ARTICLE_TABLE);
        db.execSQL(CREATE_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DB_TABLE);
        onCreate(db);
    }

    public long saveArticle(Article a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues tuples = new ContentValues();

        tuples.put(KEY_TITLE, a.getTitle());
        tuples.put(KEY_LINK, a.getLink());
        tuples.put(KEY_DATE, a.getDate());
        tuples.put(KEY_TIME, a.getTime());
        tuples.put(KEY_SOURCE, a.getSource());
        tuples.put(KEY_IMAGE, a.getImage());
        tuples.put(KEY_DESCR, a.getDescription());
        /*String keywords = "";
        for(String key:a.getKeywords())
            keywords+=(key+",");
        tuples.put(KEY_WORDS, keywords);*/
        long rowid = db.insert(DB_TABLE, null, tuples);
        db.close();
        return rowid;
    }

    public int removeArticle(Article a) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(DB_TABLE,KEY_LINK+" = ?",new String[] {a.getLink()});
        db.close();
        return rows;
    }

    public List<Article> getSavedArticles() {
        List<Article> article_list = new LinkedList<>();
        String query = "select * from "+DB_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor it = db.rawQuery(query,null);

        if(it!=null) {
            if(it.moveToFirst()) {
                do {
                    int c = 0;
                    Article a = new Article(it.getString(c++), it.getString(c++), it.getString(c++));
                    a.setTime(Float.parseFloat(it.getString(c++)));a.setSource(it.getString(c++));a.setImage(it.getString(c++));
                    a.setDescription(it.getString(c));//a.setKeywords(it.getString(c++).split(","));
                    article_list.add(a);
                } while (it.moveToNext());
                it.close();
            }
        }
        db.close();
        return article_list;
    }
}
