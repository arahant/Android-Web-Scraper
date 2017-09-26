package com.news.extract;

import java.net.URL;
import java.util.ArrayList;

public abstract class ExtractNews {

    protected ArrayList<StringBuilder> articlelist;
    protected ArrayList<URL[]> article_url_list;

    public abstract void extractNews();

    public ArrayList<StringBuilder> getArticleList() { return this.articlelist; }
    public ArrayList<URL[]> getUrlList() {
        return this.article_url_list;
    }
}
