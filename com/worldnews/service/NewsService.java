package com.worldnews.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.worldnews.R;
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
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewsService extends Service {

    private Timer timer;

    public NewsService() {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        Log.w(NewsService.class.getName(),"News Service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        long delay = 1000*60*60*6;
        TimerTask task = new TimerTask() {
            public void run() {
                int max=1;
                Runnable r = new NewsUpdate();
                Thread t = new Thread(r,max+"");
                t.start();
            }
        };
        timer.schedule(task,new Date(),delay);
        Log.w(NewsService.class.getName(),"Timer started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private class NewsUpdate implements Runnable {
        private List<Source> feeds;
        private static final String KEY_LINK = "link";

        private void initiate() {
            NewsSource feedsRss = new NewsSource();
            if(feeds==null)
                feeds = feedsRss.getAllFeeds();
            Cache.initiate(getApplicationContext());
            Log.w(NewsUpdate.class.getName(),"Crawling through the rss feeds");
        }

        @Override
        public void run() {
            initiate();
            boolean flag = false;
            int c=0;
            int max = Integer.parseInt(Thread.currentThread().getName());
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
                            Article article = new Article(link);
                            if(verifyURL(link) && !Cache.hasValue(article)) {
                                article = ScrapeArticle.scrape(item);
                                if(article!=null) {
                                    Cache.storeCache(article);
                                    if(c++<10)
                                        publishProgress(article);
                                    flag = true;
                                }
                            }
                        }
                    }
                }
            }catch(IOException e) {
                e.printStackTrace();
            }
            finishTask(flag);
        }

        private void publishProgress(final Article article) {
            new Thread(new Runnable() {
                public void run() {
                    int id = (int)System.currentTimeMillis();
                    Intent nIntent = new Intent(Intent.ACTION_VIEW);
                    nIntent.setData(Uri.parse(article.getLink()));
                    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),id,nIntent,0);

                    NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(getApplicationContext());
                    nBuilder.setContentTitle(article.getTitle());
                    nBuilder.setContentText(article.getDescription()).setSmallIcon(R.mipmap.ic_launcher);
                    nBuilder.setContentIntent(pIntent);

                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nManager.notify(id,nBuilder.build());
                }
            }).start();
        }

        private void finishTask(final boolean flag) {
            Log.w(NewsUpdate.class.getName(),"News service task finished");

            new Thread(new Runnable() {
                public void run() {
                    int id = (int)System.currentTimeMillis();

                    NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(getApplicationContext());
                    nBuilder.setContentTitle(getString(R.string.app_name));
                    nBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    nBuilder.setContentText(flag?getString(R.string.new_article_notify):getString(R.string.no_new_article_notify));
                    if(flag) {
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        nBuilder.setSound(alarmSound);
                    }

                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nManager.notify(id,nBuilder.build());
                }
            }).start();
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

}
