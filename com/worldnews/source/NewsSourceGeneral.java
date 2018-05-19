package com.worldnews.source;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.worldnews.service.GPSHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class NewsSourceGeneral {

    public static final String KEY_LOCAL="local";
    public static final String KEY_ENTERTAINMENT="entertainment";
    public static final String KEY_INTERNATIONAL="international";
    public static final String KEY_NATIONAL="national";
    public static final String KEY_ECONOMY="economy";
    public static final String KEY_TECHNOLOGY="science";
    public static final String KEY_SPORTS="sports";
    public static final String KEY_POLITICS="politics";
    public static final String KEY_EDUCATION="education";
    public static final String KEY_AUTO="auto";
    public static final String KEY_MARKET="market";

    //Indian states' location details
    private static final double latAN = 11.7400867, lonAN = 92.6586401;
    private static final double latAP = 16, lonAP = 80;
    private static final double latARP = 28, lonARP = 95;
    private static final double latAS = 26.2006043, lonAS = 92.9375739;
    private static final double latBH = 25.5, lonBH = 85;
    private static final double latCG = 21.5, lonCG = 82;
    private static final double latGA = 15.33, lonGA = 74;
    private static final double latGJ = 23, lonGJ = 72;
    private static final double latHR = 29, lonHR = 76;
    private static final double latMG = 25.5, lonMG = 91;
    private static final double latMZ = 23.5, lonMZ = 93;
    private static final double latNG = 26, lonNG = 94;
    private static final double latOD = 21, lonOD = 85;
    private static final double latPJ = 31, lonPJ = 75.5;
    private static final double latRJ = 27, lonRJ = 74;
    private static final double latSK = 27.5, lonSK = 88.5;
    private static final double latTN = 11, lonTN = 78;
    private static final double latTR = 23.75, lonTR = 91.5;
    private static final double latUP = 27, lonUP = 81;
    private static final double latUK = 30, lonUK = 79;
    private static final double latWB = 23, lonWB = 88;
    private static final double latKA = 15, lonKA = 75;
    private static final double latHP = 32, lonHP = 77;
    private static final double latJK = 34, lonJK = 75;
    private static final double latJH = 24, lonJH = 85.5;
    private static final double latKL = 10, lonKL = 76.5;
    private static final double latMP = 23.5, lonMP = 78;
    private static final double latMH = 20, lonMH = 75;
    private static final double latMN = 24.75, lonMN = 94;
    private static final double latTL = 18, lonTL = 79;

    private static final double latCD = 30.75, lonCD = 76.75;
    private static final double latDLH = 28.38, lonDLH = 77.1;
    private static final double latPD = 12, lonPD = 79.8;
    private static final double latDD = 20.51, lonDD = 71;
    private static final double latDNH = 20.33, lonDNH = 72.95;
    private static final double latLK = 10.5, lonLK = 72.5;

    private static DatabaseReference dbRef;
    private static List<Source> feeds;
    private static int c=0;

    static {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbRef = db.getReference();
        feeds = new LinkedList<>();
    }

    public List<Source> getAllFeeds() {
        getInternationalFeeds2();
        getNationalFeeds2();
        getPoliticsFeeds2();
        getEconomyFeeds2();
        getMarketFeeds2();
        getAutoFeeds2();
        getScienceFeeds2();
        getEducationFeeds2();
        getSportsFeeds2();
        getEntertainmentFeeds2();
        Log.w(NewsSourceGeneral.class.getName(),feeds.size()+" total sources "+c);
        return feeds;
    }

    public List<Source> getCategoryFeeds(String category) {
        feeds.clear();
        switch(category) {
            case KEY_INTERNATIONAL:
                getInternationalFeeds2();
                break;
            case KEY_NATIONAL:
                getNationalFeeds2();
                break;
            case KEY_POLITICS:
                getPoliticsFeeds2();
                break;

            case KEY_LOCAL:
                Location location = GPSHandler.getLocation();
                if(location!=null)
                    feeds = getLocalFeeds(location);
                else
                    getNationalFeeds2();
                break;

            case KEY_ECONOMY:
                getEconomyFeeds2();
                break;
            case KEY_MARKET:
                getMarketFeeds2();
                break;
            case KEY_AUTO:
                getAutoFeeds2();
                break;

            case KEY_EDUCATION:
                getEducationFeeds2();
                break;
            case KEY_TECHNOLOGY:
                getScienceFeeds2();
                break;

            case KEY_SPORTS:
                getSportsFeeds2();
                break;
            case KEY_ENTERTAINMENT:
                getEntertainmentFeeds2();
                break;
        }
        return feeds;
    }

    private ValueEventListener newsSourceListener = new ValueEventListener() {
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot data: dataSnapshot.getChildren()) {
                String link = data.getValue(String.class);
                feeds.add(new Source(link));++c;
            }
            Log.w(NewsSourceGeneral.class.getName(),c+" sources");
        }

        public void onCancelled(DatabaseError databaseError) {
            Log.e(NewsSourceGeneral.class.getName(),"News source retrieval failure");
        }
    };

    private void getInternationalFeeds2() {
        Query query = dbRef.child(KEY_INTERNATIONAL);
        query.addListenerForSingleValueEvent(newsSourceListener);
    }

    private void getNationalFeeds2() {
        Query query = dbRef.child(KEY_NATIONAL);
        query.addListenerForSingleValueEvent(newsSourceListener);
    }

    private void getPoliticsFeeds2() {
        Query query = dbRef.child(KEY_POLITICS);
        query.addListenerForSingleValueEvent(newsSourceListener);
    }

    private void getEconomyFeeds2() {
        Query query = dbRef.child(KEY_ECONOMY);
        query.addListenerForSingleValueEvent(newsSourceListener);
    }

    private void getMarketFeeds2() {
        Query query = dbRef.child(KEY_MARKET);
        query.addListenerForSingleValueEvent(newsSourceListener);
    }

    private void getAutoFeeds2() {
        Query query = dbRef.child(KEY_AUTO);
        query.addListenerForSingleValueEvent(newsSourceListener);
    }

    private void getScienceFeeds2() {
        Query query = dbRef.child(KEY_TECHNOLOGY);
        query.addListenerForSingleValueEvent(newsSourceListener);
    }

    private void getEducationFeeds2() {
        Query query = dbRef.child(KEY_EDUCATION);
        query.addListenerForSingleValueEvent(newsSourceListener);
    }

    private void getSportsFeeds2() {
        Query query = dbRef.child(KEY_SPORTS);
        query.addListenerForSingleValueEvent(newsSourceListener);
    }

    private void getEntertainmentFeeds2() {
        Query query = dbRef.child(KEY_ENTERTAINMENT);
        query.addListenerForSingleValueEvent(newsSourceListener);
    }

    private List<Source> getLocalFeeds(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        TreeMap<Float,Integer> distances = new TreeMap<>();
        float[] result = new float[1];
        int k=1;

        /**/Location.distanceBetween(latitude,longitude,latAN,lonAN,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latAP,lonAP,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latARP,lonARP,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latAS,lonAS,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latBH,lonBH,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latCD,lonCD,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latCG,lonCG,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latDD,lonDD,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latDLH,lonDLH,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latDNH,lonDNH,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latGA,lonGA,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latGJ,lonGJ,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latHP,lonHP,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latHR,lonHR,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latJH,lonJH,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latJK,lonJK,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latKA,lonKA,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latKL,lonKL,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latLK,lonLK,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latMG,lonMG,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latMH,lonMH,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latMN,lonMN,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latMP,lonMP,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latMZ,lonMZ,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latNG,lonNG,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latOD,lonOD,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latPD,lonPD,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latPJ,lonPJ,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latRJ,lonRJ,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latSK,lonSK,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latTL,lonTL,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latTN,lonTN,result);
        distances.put(result[0],k++);
        /**/Location.distanceBetween(latitude,longitude,latTR,lonTR,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latUK,lonUK,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latUP,lonUP,result);
        distances.put(result[0],k++);
        Location.distanceBetween(latitude,longitude,latWB,lonWB,result);
        distances.put(result[0],k);

        List<Source> feeds = new LinkedList<>();
        String url;
        k=1;
        for(Float gap:distances.keySet()) {
            if(k++>4) break;
            int local = distances.get(gap);
            switch (local) {
                case 1:
                    url = "";
                    feeds.add(new Source(url));
                    break;
                case 2:
                    url = "https://www.newkerala.com/andhra-pradesh-news.xml";
                    feeds.add(new Source(url));
                    break;
                case 3:
                    url = "https://www.telegraphindia.com/feeds/rss/home";
                    feeds.add(new Source(url));
                    break;
                case 4:
                    url = "http://newsrack.in/crawled.feeds/at.rss.xml";
                    feeds.add(new Source(url));
                    url = "https://www.assamtimes.org/rss.xml";
                    feeds.add(new Source(url));
                    break;
                case 5:
                    url = "https://www.hindustantimes.com/rss/cities/patna/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
                case 6:
                    url = "https://www.hindustantimes.com/rss/cities/chandigarh/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
                case 7:
                    url = "";
                    feeds.add(new Source(url));
                    break;
                case 8:
                    url = "https://www.telegraphindia.com/feeds/rss/home";
                    feeds.add(new Source(url));
                    break;
                case 9:
                    url = "http://www.thehindu.com/news/cities/Delhi/?service=rss";
                    feeds.add(new Source(url));
                    url = "https://www.hindustantimes.com/rss/cities/delhi/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
                case 10:
                    url = "https://www.telegraphindia.com/feeds/rss/home";
                    feeds.add(new Source(url));
                    break;
                case 11:
                    url = "https://timesofindia.indiatimes.com/rssfeeds/3012535.cms";
                    feeds.add(new Source(url));
                    break;
                case 12:
                    url = "https://www.newkerala.com/gujarat-news.xml";
                    feeds.add(new Source(url));
                    break;
                case 13:
                    url = "";
                    feeds.add(new Source(url));
                    break;
                case 14:
                    url = "https://timesofindia.indiatimes.com/rssfeeds/6547154.cms";
                    feeds.add(new Source(url));
                    url = "https://www.hindustantimes.com/rss/cities/gurgaon/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
                case 15:
                    url = "https://www.hindustantimes.com/rss/cities/ranchi/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
                case 16:
                    url = "";
                    feeds.add(new Source(url));
                    break;
                case 17:
                    url = "https://www.newkerala.com/karnataka-news.xml";
                    feeds.add(new Source(url));
                    url = "http://www.thehindu.com/news/cities/Bangalore/?service=rss";
                    feeds.add(new Source(url));
                    break;
                case 18:
                    url = "https://www.newkerala.com/kerala-news.xml";
                    feeds.add(new Source(url));
                    break;
                case 19:
                    url = "";
                    feeds.add(new Source(url));
                    break;
                case 20:
                    url = "http://meghalayatimes.info/index.php?format=feed&type=rss";
                    feeds.add(new Source(url));
                    url = "https://www.telegraphindia.com/feeds/rss/home";
                    feeds.add(new Source(url));
                    break;
                case 21:
                    url = "https://www.newkerala.com/maharashtra-news.xml";
                    feeds.add(new Source(url));
                    break;
                case 22:
                    url = "http://newsrack.in/crawled.feeds/epao.rss.xml";
                    feeds.add(new Source(url));
                    break;
                case 23:
                    url = "http://www.centralchronicle.com/madhya-pradesh-news/feed";
                    feeds.add(new Source(url));
                    url = "https://www.hindustantimes.com/rss/cities/bhopal/rssfeed.xml";
                    feeds.add(new Source(url));
                    url = "https://www.hindustantimes.com/rss/cities/indore/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
                case 24:
                    url = "https://www.telegraphindia.com/feeds/rss/home";
                    feeds.add(new Source(url));
                    url = "http://newsrack.in/crawled.feeds/epao.rss.xml";
                    feeds.add(new Source(url));
                    break;
                case 25:
                    url = "https://www.telegraphindia.com/feeds/rss/home";
                    feeds.add(new Source(url));
                    url = "http://newsrack.in/crawled.feeds/epao.rss.xml";
                    feeds.add(new Source(url));
                    break;
                case 26:
                    url = "";
                    feeds.add(new Source(url));
                    break;
                case 27:
                    url = "";
                    feeds.add(new Source(url));
                    break;
                case 28:
                    url = "https://www.newkerala.com/punjab-news.xml";
                    feeds.add(new Source(url));
                    url = "https://www.hindustantimes.com/rss/punjab/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
                case 29:
                    url = "https://www.hindustantimes.com/rss/cities/jaipur/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
                case 30:
                    url = "http://voiceofsikkim.com/feed/";
                    feeds.add(new Source(url));
                    break;
                case 31:
                    url = "http://www.thehindu.com/news/cities/Hyderabad/?service=rss";
                    feeds.add(new Source(url));
                    break;
                case 32:
                    url = "https://www.newkerala.com/tamil-nadu-news.xml";
                    feeds.add(new Source(url));
                    url = "http://www.thehindu.com/news/cities/Chennai/?service=rss";
                    feeds.add(new Source(url));
                    break;
                case 33:
                    url = "http://newsrack.in/crawled.feeds/epao.rss.xml";
                    feeds.add(new Source(url));
                    url = "https://www.telegraphindia.com/feeds/rss/home";
                    feeds.add(new Source(url));
                    break;
                case 34:
                    url = "https://www.hindustantimes.com/rss/cities/dehradun/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
                case 35:
                    url = "https://www.hindustantimes.com/rss/cities/lucknow/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
                case 36:
                    url = "https://www.newkerala.com/west-bengal-news.xml";
                    feeds.add(new Source(url));
                    url = "https://www.telegraphindia.com/feeds/rss/calcutta";
                    feeds.add(new Source(url));
                    url = "https://www.hindustantimes.com/rss/cities/kolkata/rssfeed.xml";
                    feeds.add(new Source(url));
                    break;
            }
        }
        return feeds;
    }

    /*private List<Source> getInternationalFeeds() {
        List<Source> feeds = new LinkedList<>();
        String url;
        url = "https://www.theguardian.com/uk/rss";feeds.add(new Source(url));
        url = "https://www.theguardian.com/world/rss";feeds.add(new Source(url));
        url = "https://www.theguardian.com/global-development/rss";feeds.add(new Source(url));
        url = "http://feeds.bbci.co.uk/news/world/rss.xml";feeds.add(new Source(url));
        url = "http://www.aljazeera.com/xml/rss/all.xml";feeds.add(new Source(url));
        url = "http://www.economist.com/feeds/print-sections/74/international.xml";feeds.add(new Source(url));
        url = "http://www.economist.com/feeds/print-sections/75/europe.xml";feeds.add(new Source(url));
        url = "http://www.economist.com/feeds/print-sections/73/asia.xml";feeds.add(new Source(url));

        url = "http://www.orfonline.org/feed/?post_type=research";feeds.add(new Source(url));
        url = "http://indianexpress.com/section/world/feed/";feeds.add(new Source(url));
        url = "http://www.thehindu.com/news/international/?service=rss";feeds.add(new Source(url));
        url = "https://timesofindia.indiatimes.com/rssfeeds/296589292.cms";feeds.add(new Source(url));
        url = "https://timesofindia.indiatimes.com/rssfeeds/3907412.cms";feeds.add(new Source(url));
        url = "https://timesofindia.indiatimes.com/rssfeeds/1898274.cms";feeds.add(new Source(url));
        return feeds;
    }

    private List<Source> getNationalFeeds() {
        List<Source> feeds = new LinkedList<>();
        String url;
        url = "http://www.hindustantimes.com/rss/india/rssfeed.xml";feeds.add(new Source(url));
        url = "http://www.thehindu.com/news/national/?service=rss";feeds.add(new Source(url));
        url = "http://www.thehindubusinessline.com/news/national/?service=rss";feeds.add(new Source(url));
        url = "http://www.financialexpress.com/print/india/feed/";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/news/politics-and-nation/rssfeeds/1052732854.cms";feeds.add(new Source(url));
        return feeds;
    }

    private List<Source> getNationalFeeds(Location location) {
        List<Source> feeds = new LinkedList<>();
        String url;

        return feeds;
    }

    private List<Source> getPoliticsFeeds() {
        List<Source> feeds = new LinkedList<>();
        String url;
        url = "https://www.theguardian.com/politics/rss";feeds.add(new Source(url));
        url = "http://feeds.bbci.co.uk/news/politics/rss.xml";feeds.add(new Source(url));

        url = "http://www.frontline.in/politics/?service=rss";feeds.add(new Source(url));
        url = "http://www.firstpost.com/rss/politics.xml";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/news/politics-and-nation/rssfeeds/1052732854.cms";feeds.add(new Source(url));
        url = "https://www.news18.com/rss/politics.xml";feeds.add(new Source(url));
        return feeds;
    }

    private List<Source> getEconomyFeeds() {
        List<Source> feeds = new LinkedList<>();
        String url;
        url = "https://www.theguardian.com/uk/business/rss";feeds.add(new Source(url));
        url = "http://feeds.bbci.co.uk/news/business/rss.xml";feeds.add(new Source(url));

        url = "http://www.thehindu.com/business/?service=rss";feeds.add(new Source(url));
        url = "http://www.thehindubusinessline.com/economy/?service=rss";feeds.add(new Source(url));
        url = "http://www.thehindubusinessline.com/companies/?service=rss";feeds.add(new Source(url));
        url = "http://www.thehindubusinessline.com/markets/stock-markets/?service=rss";feeds.add(new Source(url));

        url = "http://www.financialexpress.com/feed/";feeds.add(new Source(url));
        url = "http://www.financialexpress.com/economy/feed/";feeds.add(new Source(url));
        url = "http://www.financialexpress.com/market/feed/";feeds.add(new Source(url));
        url = "http://www.financialexpress.com/industry/feed/";feeds.add(new Source(url));

        url = "https://economictimes.indiatimes.com/rssfeedsdefault.cms";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/news/rssfeeds/1715249553.cms";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/news/economy/rssfeeds/1373380680.cms";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/wealth/tax/rssfeeds/47119912.cms";feeds.add(new Source(url));

        return feeds;
    }

    private List<Source> getMarketFeeds() {
        List<Source> feeds = new LinkedList<>();
        String url;
        url = "http://feeds.marketwatch.com/marketwatch/bulletins";feeds.add(new Source(url));
        url = "http://articlefeeds.nasdaq.com/nasdaq/categories?category=International";feeds.add(new Source(url));

        url = "https://www.thehindubusinessline.com/markets/stock-markets/?service=rss";feeds.add(new Source(url));
        url = "https://www.thehindubusinessline.com/markets/?service=rss";feeds.add(new Source(url));
        return feeds;
    }

    private List<Source> getAutoFeeds() {
        List<Source> feeds = new LinkedList<>();
        String url;
        url = "https://www.hindustantimes.com/rss/auto/rssfeed.xml";feeds.add(new Source(url));
        url = "https://www.autocarindia.com/RSS/rss.ashx?type=News";feeds.add(new Source(url));
        url = "https://www.autocarindia.com/RSS/rss.ashx?type=Category&ID=539";feeds.add(new Source(url));
        url = "https://www.autocarindia.com/RSS/rss.ashx?type=Category&ID=542";feeds.add(new Source(url));
        url = "https://www.autocarindia.com/RSS/rss.ashx?type=Category&ID=543";feeds.add(new Source(url));
        url = "https://www.autocarindia.com/RSS/rss.ashx?type=Category&ID=876";feeds.add(new Source(url));
        url = "https://auto.economictimes.indiatimes.com/rss/topstories";feeds.add(new Source(url));
        url = "https://auto.economictimes.indiatimes.com/rss/recentstories";feeds.add(new Source(url));
        url = "https://auto.economictimes.indiatimes.com/rss/industry";feeds.add(new Source(url));
        url = "https://auto.economictimes.indiatimes.com/rss/auto-technology";feeds.add(new Source(url));
        return feeds;
    }

    private List<Source> getScienceFeeds() {
        List<Source> feeds = new LinkedList<>();
        String url;
        url = "https://www.theguardian.com/uk/technology/rss";feeds.add(new Source(url));
        url = "https://www.theguardian.com/science/rss";feeds.add(new Source(url));
        url = "http://feeds.bbci.co.uk/news/technology/rss.xml";feeds.add(new Source(url));
        url = "http://feeds.bbci.co.uk/news/science_and_environment/rss.xml";feeds.add(new Source(url));

        url = "http://www.thehindu.com/sci-tech/science/?service=rss";feeds.add(new Source(url));
        url = "http://www.thehindu.com/sci-tech/technology/?service=rss";feeds.add(new Source(url));
        url = "http://www.thehindubusinessline.com/news/science/?service=rss";feeds.add(new Source(url));
        url = "http://www.thehindubusinessline.com/info-tech/?service=rss";feeds.add(new Source(url));
        url = "https://www.newkerala.com/technology-news.xml";feeds.add(new Source(url));
        url = "http://zeenews.india.com/rss/science-environment-news.xml";feeds.add(new Source(url));
        url = "http://www.frontline.in/science-and-technology/?service=rss";feeds.add(new Source(url));
        url = "http://www.financialexpress.com/lifestyle/science/feed/";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/tech/software/rssfeeds/13357555.cms";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/tech/hardware/rssfeeds/13357565.cms";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/tech/rssfeeds/13357270.cms";feeds.add(new Source(url));
        return feeds;
    }

    private List<Source> getEducationFeeds() {
        List<Source> feeds = new LinkedList<>();
        String url;
        url = "https://www.theguardian.com/education/rss";feeds.add(new Source(url));
        url = "http://feeds.bbci.co.uk/news/education/rss.xml";feeds.add(new Source(url));

        url = "http://www.business-standard.com/rss/news-ians-education-15009.rss";feeds.add(new Source(url));
        url = "http://www.thehindu.com/education/?service=rss";feeds.add(new Source(url));
        url = "https://www.hindustantimes.com/rss/education/rssfeed.xml";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/rssfeeds/25466841.cms";feeds.add(new Source(url));
        return feeds;
    }

    private List<Source> getSportsFeeds() {
        List<Source> feeds = new LinkedList<>();
        String url;
        url = "https://www.theguardian.com/uk/sport/rss";feeds.add(new Source(url));
        url = "https://www.bbc.com/sport/rss.xml";feeds.add(new Source(url));

        url = "http://www.hindustantimes.com/rss/sports/rssfeed.xml";feeds.add(new Source(url));
        url = "http://www.hindustantimes.com/rss/othersports/rssfeed.xml";feeds.add(new Source(url));
        url = "http://www.thehindu.com/sport/?service=rss";feeds.add(new Source(url));
        url = "http://www.thehindubusinessline.com/news/sports/?service=rss";feeds.add(new Source(url));
        url = "http://www.rediff.com/rss/sportsrss.xml";feeds.add(new Source(url));
        url = "https://www.telegraphindia.com/feeds/rss/sports";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/industry/sports/rssfeeds/58571631.cms";feeds.add(new Source(url));
        return feeds;
    }

    private List<Source> getEntertainmentFeeds() {
        List<Source> feeds = new LinkedList<>();
        String url;
        url = "https://www.theguardian.com/uk/culture/rss";feeds.add(new Source(url));
        url = "http://feeds.bbci.co.uk/news/entertainment_and_arts/rss.xml";feeds.add(new Source(url));

        url = "http://www.deccanherald.com/rss/entertainment.rss";feeds.add(new Source(url));
        url = "https://economictimes.indiatimes.com/rssfeeds/13357410.cms";feeds.add(new Source(url));
        url = "https://timesofindia.indiatimes.com/rssfeeds/1081479906.cms";feeds.add(new Source(url));
        return feeds;
    }*/

}
