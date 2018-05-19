package com.worldnews.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.worldnews.R;
import com.worldnews.article.Article;
import com.worldnews.service.GPSHandler;
import com.worldnews.service.NewsService;
import com.worldnews.source.NewsSource;
import com.worldnews.source.NewsSourceGeneral;
import com.worldnews.source.Source;
import com.worldnews.store.ArticleDAO;
import com.worldnews.store.Cache;
import com.worldnews.store.PreferenceDAO;
import com.worldnews.task.CategoryTask;
import com.worldnews.task.MyFeedTask;
import com.worldnews.task.SavedTask;
import com.worldnews.task.SearchTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Dialog dialog;
    private ProgressBar mProgressView;
    private Context context;
    private NewsAdapter newsAdapter;
    private RecyclerView news_list;
    private FloatingActionButton undo;

    private static MainActivity instance;
    private Intent newsService;
    private GPSHandler gpsHandler;
    //private Thread feedT, searchT, categoryT, bookmarkT;
    private MyFeedTask feedTask;
    private CategoryTask categoryTask;
    private SearchTask searchTask;
    private SavedTask saveTask;

    private List<Article> article_list;
    private Article article;
    private int position = -1;
    private boolean removed = false;
    private static String search_key="",category="",title;

    private enum news_state {myfeed, search, category, bookmark}
    private news_state state = news_state.myfeed;

    public static MainActivity getInstance() { return instance;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        instance = this;
        gpsHandler = new GPSHandler(context);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final SwipeRefreshLayout myfeed_refresh = (SwipeRefreshLayout) findViewById(R.id.myfeed_refresh);
        myfeed_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //Cache.clearCache();
                initiate();
                myfeed_refresh.setRefreshing(false);
                switch (state) {
                    case myfeed:
                        startMyFeedTask();
                        break;
                    case search:
                        if(search_key!=null && search_key.length()>1)
                            startSearchTask(search_key);
                        break;
                    case category:
                        if(category!=null)
                            startCategoryTask(category);
                        break;
                    case bookmark:
                        startBookmarkTask();
                        break;
                }
            }
        });

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layer_progress);
        dialog.setCancelable(true);
        mProgressView = dialog.findViewById(R.id.progressBar);

        undo = (FloatingActionButton) findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(removed) {
                    removed = false;
                    v.setVisibility(View.GONE);
                    article_list.add(position,article);
                    newsAdapter.notifyItemInserted(position);
                    if(state==news_state.bookmark) {
                        ArticleDAO articleDAO = new ArticleDAO(getApplicationContext());
                        articleDAO.saveArticle(article);
                    }
                }
            }
        });

        ItemTouchHelper.SimpleCallback itemSwipeHandler = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                position = viewHolder.getAdapterPosition();
                article = article_list.get(position);
                article_list.remove(position);
                newsAdapter.notifyItemRemoved(position);
                removed = true;
                undo.setVisibility(View.VISIBLE);
                if(state==news_state.bookmark) {
                    ArticleDAO articleDAO = new ArticleDAO(context);
                    articleDAO.removeArticle(article);
                }
            }
        };
        news_list = (RecyclerView) findViewById(R.id.news_list);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemSwipeHandler);
        itemTouchHelper.attachToRecyclerView(news_list);

        newsService = new Intent(context, NewsService.class);
        if(isMyServiceRunning(NewsService.class)) {
            stopService(newsService);
        }
        initiate();
        startMyFeedTask();
        //retrieveSources();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
        cancelMyFeedTask();
        cancelSearchTask();
        cancelCategoryTask();
        if(!isMyServiceRunning(NewsService.class)) {
            startService(newsService);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isMyServiceRunning(Class serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void selectTask(View v) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        initiate();
        int id=v.getId();
        switch(id) {
            case R.id.search_button:
                EditText search = (EditText)findViewById(R.id.search_field);
                search_key = search.getText().toString();
                search.setText(null);
                if(search_key.length()>1) {
                    startSearchTask(search_key);
                }
                break;
            case R.id.menu_myfeeds:
                startMyFeedTask();
                break;
            case R.id.menu_world:
                title = getString(R.string.title_international);
                toolbar.setTitle(title);
                category = NewsSource.KEY_INTERNATIONAL;
                startCategoryTask(category);
                break;
            case R.id.menu_national:
                title = getString(R.string.title_national);
                toolbar.setTitle(title);
                category = NewsSource.KEY_NATIONAL;
                startCategoryTask(category);
                break;
            case R.id.menu_local:
                title = getString(R.string.title_local);
                toolbar.setTitle(title);
                category = NewsSource.KEY_LOCAL;
                isGpsEnabled();
                startCategoryTask(category);
                break;
            case R.id.menu_politics:
                title = getString(R.string.title_politics);
                toolbar.setTitle(title);
                category = NewsSource.KEY_POLITICS;
                startCategoryTask(category);
                break;

            case R.id.menu_economy:
                toolbar.setTitle(getString(R.string.title_economy));
                category = NewsSource.KEY_ECONOMY;
                startCategoryTask(category);
                break;
            case R.id.menu_market:
                toolbar.setTitle(getString(R.string.title_market));
                category = NewsSource.KEY_MARKET;
                startCategoryTask(category);
                break;
            case R.id.menu_auto:
                toolbar.setTitle(getString(R.string.title_auto));
                category = NewsSource.KEY_AUTO;
                startCategoryTask(category);
                break;

            case R.id.menu_education:
                toolbar.setTitle(getString(R.string.title_education));
                category = NewsSource.KEY_EDUCATION;
                startCategoryTask(category);
                break;
            case R.id.menu_science:
                title = getString(R.string.title_science);
                toolbar.setTitle(title);
                category = NewsSource.KEY_TECHNOLOGY;
                startCategoryTask(category);
                break;

            case R.id.menu_sport:
                title = getString(R.string.title_sports);
                toolbar.setTitle(title);
                category = NewsSource.KEY_SPORTS;
                startCategoryTask(category);
                break;
            case R.id.menu_entertainment:
                title = getString(R.string.title_entertainment);
                toolbar.setTitle(title);
                category = NewsSource.KEY_ENTERTAINMENT;
                startCategoryTask(category);
                break;

            case R.id.menu_bookmark:
                title = getString(R.string.title_bookmark);
                toolbar.setTitle(title);
                startBookmarkTask();
                break;
        }
    }

    private void retrieveSources() {
        NewsSourceGeneral feedRss = new NewsSourceGeneral();
        List<Source> feeds = feedRss.getAllFeeds();
        Log.w(MainActivity.class.getName(),feeds.size()+" sources");
        for(Source source: feeds)
            Log.w(source.getSource()+"",source.getLink());
    }

    private void initiate() {
        undo.setVisibility(View.GONE);
        article_list = new LinkedList<>();
        newsAdapter = new NewsAdapter(article_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        news_list.setLayoutManager(layoutManager);
        news_list.setAdapter(newsAdapter);
        news_list.setItemAnimator(new DefaultItemAnimator());
    }

    private void startMyFeedTask() {
        title = getString(R.string.title_myfeed);
        toolbar.setTitle(title);
        cancelSearchTask();
        cancelCategoryTask();
        cancelBookmarkTask();
        showProgress(true);
        state = news_state.myfeed;
        Log.w(MainActivity.class.getName(), "Extracting my feeds");
        final int max = 1;
        Cache.initiate(context);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                feedTask = new MyFeedTask();
                feedTask.execute(max);
                Looper.loop();
            }
        }).start();
        /*MyFeedExtract feedExtract = new MyFeedExtract();
        feedT = new Thread(feedExtract,max+"");
        feedT.start();*/
    }

    private void cancelMyFeedTask() {
        if(feedTask!=null)
            feedTask.cancel(true);
        /*if(feedT!=null && feedT.isAlive()) {
            Thread temp = feedT;
            feedT = null;
            temp.interrupt();
        }*/
    }

    private void startSearchTask(final String key) {
        title = search_key;
        toolbar.setTitle(search_key);
        cancelMyFeedTask();
        cancelCategoryTask();
        cancelBookmarkTask();
        showProgress(true);
        state = news_state.search;
        Log.w(MainActivity.class.getName(), "Searching for articles matching " + key);
        final int max = 50;
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                searchTask = new SearchTask(key);
                searchTask.execute(max);
                Looper.loop();
            }
        }).start();
        /*SearchExtract searchExtract = new SearchExtract(key);
        searchT = new Thread(searchExtract,max+"");
        searchT.start();*/
    }

    private void cancelSearchTask() {
        if(searchTask!=null)
            searchTask.cancel(true);
        /*if(searchT!=null && searchT.isAlive()) {
            Thread temp = searchT;
            searchT = null;
            temp.interrupt();
        }*/
    }

    private void startCategoryTask(final String category) {
        cancelMyFeedTask();
        cancelSearchTask();
        cancelBookmarkTask();
        state = news_state.category;
        final int max = 7;
        if(isNetworkAvailable()) {
            showProgress(true);
            Log.w(MainActivity.class.getName(), "Searching for articles in " + category);
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    categoryTask = new CategoryTask(category);
                    categoryTask.execute(max);
                    Looper.loop();
                }
            }).start();
            /*CategoryExtract categoryExtract = new CategoryExtract(category);
            categoryT = new Thread(categoryExtract,max+"");
            categoryT.start();*/
        }
        else
            Toast.makeText(context,getString(R.string.internet_error_msg),Toast.LENGTH_SHORT).show();
    }

    private void cancelCategoryTask() {
        if(categoryTask!=null)
            categoryTask.cancel(true);
        /*if(categoryT!=null &&categoryT.isAlive()) {
            Thread temp = categoryT;
            categoryT = null;
            temp.interrupt();
        }*/
    }

    private void isGpsEnabled() {
        boolean enabled = gpsHandler.isGpsEnabled();
        if (!enabled) {
            Toast.makeText(context, "Please enable GPS", Toast.LENGTH_SHORT).show();
            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsIntent);
        }
        gpsHandler.setLocation();
    }

    private void startBookmarkTask() {
        cancelMyFeedTask();
        cancelSearchTask();
        cancelCategoryTask();
        showProgress(true);
        state = news_state.bookmark;
        Log.w(MainActivity.class.getName(), "Searching for saved articles");
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                saveTask = new SavedTask();
                saveTask.execute(getApplicationContext());
                Looper.loop();
            }
        }).start();
        /*BookmarkExtract bookmarkExtract = new BookmarkExtract(getApplicationContext());
        bookmarkT = new Thread(bookmarkExtract);
        bookmarkT.start();*/
    }

    private void cancelBookmarkTask() {
        if(saveTask!=null)
            saveTask.cancel(true);
        /*if(bookmarkT!=null && bookmarkT.isAlive()) {
            Thread temp = bookmarkT;
            bookmarkT = null;
            temp.interrupt();
        }*/
    }

    public void loadArticles(final Article article) {
        runOnUiThread(new Runnable() {
            public void run() {
                article_list.add(article);
                showProgress(false);
                newsAdapter.notifyDataSetChanged();
            }
        });
    }

    public void displayTotal(final int c) {
        runOnUiThread(new Runnable() {
            public void run() {
                showProgress(false);
                String message = "Total articles "+c;
                Toast.makeText(context,message,Toast.LENGTH_LONG).show();
                toolbar.setTitle(title+" ("+c+")");
                rankArticles(article_list);
                newsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void rankArticles(List<Article> article_list) {
        PreferenceDAO preferenceDAO = new PreferenceDAO(getApplicationContext());
        List<String> preferences = preferenceDAO.getPreferences();
        if(preferences!=null && !preferences.isEmpty()) {
            float unitRank=0.01f;
            for(Article a:article_list) {
                for(String key:preferences) {
                    if(a.getTitle().toLowerCase().matches(".*\\b"+key.toLowerCase()+"\\b.*"))
                        a.setRank(unitRank);
                }
            }
        }
        Comparator<Article> rankCompare = new Article.RankComparator();
        Collections.sort(article_list, rankCompare);
    }

    private void showProgress(boolean show) {
        if(show) {
            mProgressView.setVisibility(View.VISIBLE);
            dialog.show();
        }
        else {
            mProgressView.setVisibility(View.GONE);
            dialog.hide();
        }
    }

    private static int c=0;
    private static final int total=2;
    private class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

        private List<Article> newsList;
        private NewsAdapter(List<Article> news) {
            this.newsList=news;
        }

        @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowView=null;
            if(c%total==0)
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_block,parent,false);
            else if(c%total==1)
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_block2,parent,false);
            c=(++c)%total;
            return new NewsViewHolder(rowView);
        }

        @Override
        public void onBindViewHolder(final NewsViewHolder holder, final int position) {
            final Article article = newsList.get(position);
            holder.title.setText(article.getTitle());
            float time = ( new Date().getTime()-new Date(article.getDate()).getTime() )/(3600*1000);
            String before = String.format(Locale.UK,"%d day(s), %d hour(s) ago",(int)time/24,(int)time%24);
            holder.date.setText(before);
            holder.source.setText(article.getSource());
            String description = article.getDescription();
            if(description!=null && !description.isEmpty()) {
                holder.description.setText(description);
                holder.arrow.setVisibility(View.VISIBLE);
            }
            if(state==news_state.bookmark)
                holder.star_tag.setVisibility(View.VISIBLE);
            holder.arrow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    switch(holder.description.getVisibility()) {
                        case View.GONE:
                            holder.description.setVisibility(View.VISIBLE);
                            holder.arrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_up));
                            break;
                        case View.VISIBLE:
                            holder.description.setVisibility(View.GONE);
                            holder.arrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_down));
                            break;
                        case View.INVISIBLE:break;
                    }
                }
            });
            holder.item_row.setOnClickListener(new LinkListener(article.getLink()));
            holder.item_row.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    PopupMenu options_menu = new PopupMenu(context,view);
                    options_menu.getMenuInflater().inflate(R.menu.article_options_menu,options_menu.getMenu());
                    options_menu.setOnMenuItemClickListener(new OptionListener(holder.getAdapterPosition()));
                    options_menu.show();
                    return false;
                }
            });
            new Thread(new Runnable() {
                public void run() {
                    try {
                        if(isNetworkAvailable()) {
                            URL url = new URL(article.getImage());
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setDoInput(true);
                            con.connect();
                            InputStream inputStream = con.getInputStream();
                            final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    holder.image.setImageBitmap(bitmap);
                                }
                            });
                            inputStream.close();
                            con.disconnect();
                        }
                    } catch (IOException e) {
                        Log.e(MainActivity.class.getName(),"IOException error 3");
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView title, date, source, description;
        private ImageView image, star_tag, arrow;
        private LinearLayout item_row;
        private NewsViewHolder(View itemView) {
            super(itemView);
            item_row = itemView.findViewById(R.id.item_block);
            image = itemView.findViewById(R.id.image);
            star_tag = itemView.findViewById(R.id.star_tag);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            source = itemView.findViewById(R.id.source);
            description = itemView.findViewById(R.id.description);
            arrow = itemView.findViewById(R.id.arrow);
        }
    }

    private class LinkListener implements View.OnClickListener {
        private String url;
        private LinkListener(String url) { this.url=url; }
        public void onClick(View view) {
            if(url!=null) {
                Intent browser = new Intent(Intent.ACTION_VIEW);
                browser.setData(Uri.parse(url));
                startActivity(browser);
            }
        }
    }

    private class OptionListener implements PopupMenu.OnMenuItemClickListener {
        private int position;
        public OptionListener(int position) {
            this.position=position;
        }
        public boolean onMenuItemClick(MenuItem item) {
            switch(item.getItemId()) {
                case R.id.save:
                    if(state!=news_state.bookmark) {
                        new Thread(new Runnable() {
                            public void run() {
                                Looper.prepare();
                                saveArticle(position);
                                savePreferences(position);
                                Looper.loop();
                            }
                        }).start();
                    }
                    break;
                case R.id.share:
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, article_list.get(position).getLink());
                    shareIntent.setType("text/plain");
                    Intent.createChooser(shareIntent, "Share via");
                    startActivity(shareIntent);
                    break;
            }
            return false;
        }
    }

    private void saveArticle(int position) {
        Article article = article_list.get(position);
        ArticleDAO articleDAO = new ArticleDAO(getApplicationContext());
        long result = articleDAO.saveArticle(article);
        if(result==-1)
            Toast.makeText(context,getString(R.string.bookmark_failed_msg),Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context,getString(R.string.bookmark_saved_msg),Toast.LENGTH_SHORT).show();
    }

    private void savePreferences(int position) {
        String[] list = article_list.get(position).getKeywords();
        if(list!=null && list.length>0) {
            PreferenceDAO preferenceDAO = new PreferenceDAO(getApplicationContext());
            preferenceDAO.savePreferences(list);
            Toast.makeText(context,getString(R.string.preference_saved_msg), Toast.LENGTH_SHORT).show();
        }
    }

}
