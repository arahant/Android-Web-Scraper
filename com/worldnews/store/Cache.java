package com.worldnews.store;

import android.content.Context;
import android.util.Log;

import com.worldnews.activity.MainActivity;
import com.worldnews.article.Article;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public class Cache {

    private static HashSet<Article> cache;
    private static File cacheFile;
    private static ObjectOutputStream oos;

    private Cache() {}

    public static void initiate(Context context) {
        initializeCache();
        initializeCacheFile(context);
        initializeOutputStream();
        retrieveStoredCache();
    }

    private static void initializeCache() {
        if(cache==null)
            cache = new HashSet<>();
    }

    private static void initializeCacheFile(Context context) {
        if(cacheFile==null)
            cacheFile = new File(context.getCacheDir(), "article_cache");
    }

    private static void initializeOutputStream() {
        if(oos==null) {
            try {
                oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
            } catch (IOException e) {
                Log.e(Cache.class.getName(), "IOException error");
                e.printStackTrace();
            }
        }
    }

    private static void retrieveStoredCache() {
        int c=0;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile));
            Article a;
            while((a=(Article)ois.readObject())!=null) {
                cache.add(a);
                c++;
            }
            ois.close();
        } catch (IOException e) {
            Log.e(Cache.class.getName(),"IOException error 1");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e(Cache.class.getName(),e.getMessage());
            e.printStackTrace();
        }
        refillCache(c);
    }

    private static void refillCache(int c) {
        Log.w(Cache.class.getName(),c+" articles in stored cache");
        if(c==0 && !cache.isEmpty()) {
            for(Article a:cache)
                storeCache(a);
        }
    }

    public static void storeCache(Article a) {
        try {
            oos.writeObject(a);
            //oos.close();fos.close();
        } catch (IOException e) {
            Log.e(Cache.class.getName(),e.getMessage());
        }
    }

    public static boolean hasValue(Article a) {
        return cache.contains(a);
    }

    public static boolean addValue(Article a) {
        storeCache(a);
        return cache.add(a);
    }

    public static boolean removeValue(Article a) {
        return cache.remove(a);
    }

    public static HashSet<Article> getCache() {
        return cache;
    }

    public static void clearCache() {
        cache.clear();
    }

}
