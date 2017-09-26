package com.news.extract;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.news.data.NewsData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractORF extends ExtractNews {

    //Extracting news articles from ORF
    public void extractNews() {

        final String url = NewsData.ORF;
        articlelist = new ArrayList<StringBuilder>();
        article_url_list = new ArrayList<URL[]>();

        try {
			/*	ORF HTML structure
			 * 	recent-updates-block
			 * 		recent-block-left [n]
			 *  		row
			 *  			col span_4
			 *  				--ul
			 *  					li(2) - date
			 *  			col span_4
			 *  				--<a>
			 *  					--<div>
			 *  						<image class src alt>
			 *  			col span_8 (2)
			 *  			  <div>
			 *  				<h2>
			 *  					<a href> text
			 *  				<ul class> --author
			 *  					<a href> text
			 *  				<span>
			 *  					--<ul>
			 *  						<li> [n] (topics)
			 *  */

            Document doc = Jsoup.connect(url).get();
            StringBuilder article_title = new StringBuilder();
            article_title.append(doc.title());
            articlelist.add(article_title);
            article_url_list.add(new URL[]{new URL(url)});

            Elements articles = doc.getElementsByClass("recent-updates-block").get(0).getElementsByClass("recent-block-left");

            int c=1;
            for (Element content : articles) {

                Elements rows = content.getElementsByClass("row");
                Elements col_span_4 = rows.get(0).getElementsByClass("col span_4");
                Elements col_span_8 = rows.get(0).getElementsByClass("col span_8");

                //extracting header information
                Element header = col_span_8.get(1).getElementsByClass("figcaption").get(0).getElementsByTag("h2").get(0).getElementsByTag("a").get(0);
                URL header_href = new URL(header.attr("href"));
                String header_text = header.text();

				/*//extracting image source
				Element image = col_span_4.get(1).getElementsByTag("image").get(0);
				URL image_src = new URL(image.attr("src"));

				//extracting author information
					Element author = col_span_8.get(1).getElementsByClass("figcaption").get(0).getElementsByTag("ul").get(0).getElementsByTag("a").get(0);
					//String author_href = author.attr("href");
					URL author_href = new URL(author.attr("href"));
					String author_text = author.text();

				//extracting and verifying topics
				Elements topics = col_span_8.get(1).getElementsByClass("figcaption").get(0).getElementsByTag("span").get(0).getElementsByTag("li");
				ArrayList<String> topiclist = new ArrayList<String>();
				for (Element t:topics)
					topiclist.add(t.text());*/

                String a1 = "Article number: "+c++;
                StringBuilder article_content = new StringBuilder();
                article_content.append(a1).append("\n").append(header_text);
                //article_content.append(author_href).append("\n");
                articlelist.add(article_content);
                article_url_list.add(new URL[] {header_href});
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verifyTopics() {
        boolean verify = false;
        return verify;
    }
}
