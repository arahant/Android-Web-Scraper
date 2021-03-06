package com.worldnews.task;

import android.os.AsyncTask;
import android.util.Log;

import com.worldnews.activity.MainActivity;
import com.worldnews.article.Article;
import com.worldnews.article.ScrapeArticle;
import com.worldnews.source.NewsSource;
import com.worldnews.source.Source;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class CategoryTask extends AsyncTask<Integer,Article,Integer> {

    private String category;
    private List<Source> feeds;
    private static final String KEY_LINK = "link";

    public CategoryTask(String category) {
        this.category=category;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        NewsSource feedsRss = new NewsSource();
        if(feeds==null)
            feeds = feedsRss.getFeeds(category);
        Log.w(CategoryTask.class.getName(),"Crawling through the rss feeds for "+category);
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        int max = params[0];
        int c=0;
        try {
            for (Source source : feeds) {
                String url = source.getLink();
                if (verifyURL(url)) {
                    Elements items = Jsoup.connect(url).get().getElementsByTag("item");
                    int n = items.size();
                    if (n > max) {
                        for (int i = n - 1; i >= max; i--)
                            items.remove(i);
                    }
                    for (Element item : items) {
                        String link = item.getElementsByTag(KEY_LINK).get(0).text();
                        if(verifyURL(link)) {
                            Article article = ScrapeArticle.scrape(item);
                            if(article!=null) {
                                c++;publishProgress(article);
                            }
                        }
                    }
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    protected void onProgressUpdate(Article... articles) {
        super.onProgressUpdate(articles);
        MainActivity.getInstance().loadArticles(articles[0]);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        Log.w(CategoryTask.class.getName(),"Category task finished");
        MainActivity.getInstance().displayTotal(integer);
    }

    @Override
    protected void onCancelled(Integer integer) {
        super.onCancelled(integer);
        Log.e(CategoryTask.class.getName(),"Category task cancelled");
    }

    private boolean verifyURL(String url) {
        try {
            new URI(url);
            if (Jsoup.connect(url).ignoreHttpErrors(true).execute().statusCode() < 400)
                return true;
        } catch (URISyntaxException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            return false;
        }return false;
    }
}
