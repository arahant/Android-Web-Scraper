package com.news.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.news.extract.ExtractIndianExpress;
import com.news.extract.ExtractNews;
import com.news.extract.ExtractORF;
import com.news.extract.ExtractTheHindu;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    private LinearLayout news_linear;
    private LinearLayout.LayoutParams article_param;
    private static URL current_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton enter = (FloatingActionButton) findViewById(R.id.enter);
        enter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current_url!=null) {
                    //Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(current_url.getPath()));
                    Intent browser = new Intent(getApplicationContext(), WebActivity.class);
                    browser.putExtra("url", current_url.toString());
                    Toast.makeText(getApplicationContext(), current_url.toString(), Toast.LENGTH_SHORT).show();
                    startActivity(browser);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        news_linear = (LinearLayout) findViewById(R.id.news_linear);
        article_param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        article_param.setMargins(10,10,10,0);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle navigation view item clicks here.
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.news_orf) {
            displayNews(new ExtractORF());
        }
        else if (id == R.id.news_ie) {
            displayNews(new ExtractIndianExpress());
        }
        else if (id == R.id.news_hindu) {
            displayNews(new ExtractTheHindu());
        }
        else if (id == R.id.nav_share) {
            return true;
        }
        else if (id == R.id.nav_send) {
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayNews(final ExtractNews source) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                source.extractNews();
                ArrayList<StringBuilder> articlelist = source.getArticleList();
                ArrayList<URL[]> url_list = source.getUrlList();
                current_url = url_list.get(0)[0];
                final ArrayList<TextView> newslist = new ArrayList<TextView>();

                for (int i=0;i<articlelist.size();i++) {

                    final URL url = url_list.get(i)[0];
                    StringBuilder article = articlelist.get(i);//.append("\n").append(url);

                    TextView temp = new TextView(getApplicationContext());
                    temp.setText(article.toString());
                    temp.setTextColor(Color.BLACK);
                    temp.setBackgroundColor(Color.LTGRAY);
                    temp.setLayoutParams(article_param);
                    temp.setPadding(10,10,10,10);

                    Linkify.addLinks(temp,Linkify.WEB_URLS);
                    temp.setOnClickListener(new LinkListener(url));
                    newslist.add(temp);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        news_linear.removeAllViews();
                        for(TextView temp:newslist) {
                            news_linear.addView(temp);
                        }
                    }
                });
            }
        }).start();
    }

    private class LinkListener implements OnClickListener {

        private URL url;
        public LinkListener(URL url) { this.url=url; }

        @Override
        public void onClick(View view) {
            if(url!=null) {
                Intent browser = new Intent(getApplicationContext(), WebActivity.class);
                browser.putExtra("url", url.toString());
                Toast.makeText(getApplicationContext(), url.toString(), Toast.LENGTH_SHORT).show();
                startActivity(browser);
            }
        }
    }

}
