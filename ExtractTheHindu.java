package com.news.extract;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.news.data.NewsData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractTheHindu extends ExtractNews {

    public void extractNews() {

        String url = NewsData.THE_HINDU;
        articlelist = new ArrayList<StringBuilder>();
        article_url_list = new ArrayList<URL[]>();

        try {
			/*	THE HINDU HTML structure
			 * 	container section-container(3)
			 * 		main
			 * 			row
			 * 				--50_1x_StoryCard mobile-padding [n]
			 * 					--col-lg-6 col-md-6 col-sm-6 col-xs-12
			 * 						--story-card
			 * 							--story-card news
			 * 								--<h3>
			 * 									--<a href> text
			 * 				33_1x_OtherStoryCard mobile-padding [n]
			 * 					col-lg-4 col-md-4 col-sm-4 col-xs-12 hover-icon
			 * 						Other-StoryCard
			 * 							--<h3>
			 * 								<a href> text
			 * 							<a> text (p)
			 */

            Document doc = Jsoup.connect(url).get();
            StringBuilder article_title = new StringBuilder();
            article_title.append(doc.title());
            articlelist.add(article_title);
            article_url_list.add(new URL[]{new URL(url)});

            int c=1;
            Element container = doc.getElementsByClass("container section-container ").get(2);
            container = container.getElementsByClass("main").get(0).getElementsByClass("row").get(0);
            Elements articles = container.getElementsByClass("33_1x_OtherStoryCard mobile-padding");

            for(Element a:articles) {
                Element a1=a;
                a1=a1.getElementsByClass("col-lg-4 col-md-4 col-sm-4 col-xs-12 hover-icon").get(0);
                a1=a1.getElementsByClass("Other-StoryCard").get(0);
                String c1="Article number "+c++;

                Element header = a1.getElementsByTag("a").get(0);
                URL header_href = new URL(header.attr("href"));
                String header_text = header.text();

                Element content = a1.getElementsByTag("a").get(1);
                String para = content.text();

                StringBuilder article_content = new StringBuilder();
                article_content.append(c1).append("\n").append(header_text).append("\n").append(para);
                articlelist.add(article_content);
                article_url_list.add(new URL[] {header_href});
            }

        } catch(IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
