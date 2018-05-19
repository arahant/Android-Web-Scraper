package com.worldnews.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.worldnews.activity.MainActivity;
import com.worldnews.article.Article;
import com.worldnews.store.ArticleDAO;

import java.util.List;

public class SavedTask extends AsyncTask<Context,Article,Integer> {

    @Override
    protected Integer doInBackground(Context... params) {
        Log.w(SavedTask.class.getName(),"Bookmark task started");
        int c=0;
        ArticleDAO articleDAO = new ArticleDAO(params[0]);
        List<Article> article_list = articleDAO.getSavedArticles();
        if(!article_list.isEmpty()) {
            try {
                for (Article a : article_list) {
                    c++;
                    Thread.sleep(250);
                    publishProgress(a);
                }
            } catch(InterruptedException e) {
                Log.e(SavedTask.class.getName(),e.getMessage());
            }
        }
        return c;
    }

    @Override
    protected void onProgressUpdate(Article... values) {
        super.onProgressUpdate(values);
        MainActivity.getInstance().loadArticles(values[0]);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        Log.w(SavedTask.class.getName(),"Bookmark task finished");
        MainActivity.getInstance().displayTotal(integer);
    }

    @Override
    protected void onCancelled(Integer integer) {
        super.onCancelled(integer);
        Log.e(SavedTask.class.getName(),"Bookmark task cancelled");
    }
}
