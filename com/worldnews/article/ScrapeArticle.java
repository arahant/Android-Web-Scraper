package com.worldnews.article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;

public class ScrapeArticle {

    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "link";
    private static final String KEY_DATE = "pubDate";
    private static final String KEY_PROP = "property";
    private static final String KEY_NAME = "name";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_SOURCE = "og:site_name";
    private static final String KEY_IMAGE = "og:image";
    private static final String KEY_DESCR = "og:description";
    private static final String KEY_WORD = "keyword";

    private static final int beforeLimit = 50;

    private ScrapeArticle() {}

    public static Article scrape(Element item) {
        Article article=null;
        try {
            String title = item.getElementsByTag(KEY_TITLE).get(0).text();
            String link = item.getElementsByTag(KEY_LINK).get(0).text();
            String date = item.getElementsByTag(KEY_DATE).get(0).text();
            Date.parse(date);
            float time = (new Date().getTime()-new Date(date).getTime())/(3600*1000*24);
            if(time<=beforeLimit) {
                article = new Article(title, link, date);
                article = scrapeInner(link, article);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return article;
    }

    private static Article scrapeInner(String url, Article article) {
        try {
            Elements meta_property = Jsoup.connect(url).get().select("meta[property]");
            Elements meta_name = Jsoup.connect(url).get().select("meta[name]");

            for(Element meta:meta_property) {
                if(meta.attr(KEY_PROP).matches(KEY_DESCR))
                    article.setDescription(meta.attr(KEY_CONTENT));
                if(meta.attr(KEY_PROP).matches(KEY_IMAGE))
                    article.setImage(meta.attr(KEY_CONTENT));
                if(meta.attr(KEY_PROP).matches(KEY_SOURCE))
                    article.setSource(meta.attr(KEY_CONTENT));
            }
            String keywords=null;
            for(Element meta:meta_name) {
                if(meta.attr(KEY_NAME).matches(".*"+KEY_WORD+".*")) {
                    keywords = meta.attr(KEY_CONTENT);
                    break;
                }
            }
            if(keywords!=null)
                article.setKeywords(keywords.split(","));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return article;
    }

}
