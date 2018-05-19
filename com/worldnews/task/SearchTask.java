package com.worldnews.task;

import android.os.AsyncTask;
import android.util.Log;

import com.worldnews.activity.MainActivity;
import com.worldnews.article.Article;
import com.worldnews.article.ScrapeArticle;
import com.worldnews.source.NewsSource;
import com.worldnews.source.Source;
import com.worldnews.store.Cache;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public class SearchTask extends AsyncTask<Integer,Article,Integer> {

    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "link";

    private List<Source> feeds;
    private String key;

    public SearchTask(String key) {
        this.key=key;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        NewsSource feedsRss = new NewsSource();
        if(feeds==null)
            feeds = feedsRss.getAllFeeds();
        Log.w(SearchTask.class.getName(),"Crawling for articles matching - "+key);
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        int max = params[0];
        int c=0;
        key=normalizeKey();
        String regexp = ".*\\b"+key+"\\b.*";
        if(!Cache.getCache().isEmpty()) {
            try {
                for(Article article:Cache.getCache()) {
                    if (article.getTitle().toLowerCase().matches(regexp)) {
                        c++;Thread.sleep(250);
                        publishProgress(article);
                    }
                }
            } catch(InterruptedException e) {
                Log.e(SearchTask.class.getName(),e.getMessage());
            }
        }
        if(MainActivity.getInstance().isNetworkAvailable()) {
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
                            String title = item.getElementsByTag(KEY_TITLE).get(0).text();
                            Article article = new Article(link);
                            if (verifyURL(link) && !Cache.hasValue(article)) {
                                if (title.toLowerCase().matches(regexp)) {
                                    article = ScrapeArticle.scrape(item);
                                    if(article!=null) {
                                        Cache.addValue(article);
                                        c++;publishProgress(article);
                                        rankFeeds(article);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return c;
    }

    @Override
    protected void onProgressUpdate(Article... articles) {
        super.onProgressUpdate(articles);
        MainActivity.getInstance().loadArticles(articles[0]);
    }

    @Override
    protected void onPostExecute(Integer total) {
        super.onPostExecute(total);
        Log.w(SearchTask.class.getName(),"Search task finished");
        Log.w(SearchTask.class.getName(),"Cache size "+Cache.getCache().size());
        MainActivity.getInstance().displayTotal(total);
    }

    @Override
    protected void onCancelled(Integer integer) {
        super.onCancelled(integer);
        Log.e(SearchTask.class.getName(),"Search task cancelled");
        Log.w(SearchTask.class.getName(),"Cache size "+Cache.getCache().size());
    }

    private String normalizeKey() {
        return key.toLowerCase();
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

    private void rankFeeds(Article article) {
        try {
            float unitRank = 0.01f;
            for(Source s:feeds) {
                String source = s.getSource();
                String domain = new URI(article.getLink()).getAuthority();
                if(source.equals(domain))
                    s.setRank(unitRank);
            }
            Collections.sort(feeds);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
