package com.worldnews.source;

import java.net.URI;
import java.net.URISyntaxException;

public class Source implements Comparable<Source>{

    private String source,link,category;
    private float rank;

    public Source() {}

    public Source(String link) {
        this.link=link;
        try {
            this.source=new URI(link).getAuthority();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setRank(float rank) {
        this.rank += rank;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public String getSource() {
        return source;
    }

    public String getCategory() {
        return category;
    }

    public float getRank() {
        return rank;
    }

    public int compareTo(Source arg) {
        if(arg.rank>this.rank)
            return 1;
        else if (arg.rank<this.rank)
            return -1;
        else return 0;
    }
}
