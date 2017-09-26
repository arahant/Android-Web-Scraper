package com.news.extract;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.news.data.NewsData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractIndianExpress extends ExtractNews {

    //Extracting news articles from THE INDIAN EXPRESS
    public void extractNews() {

        final String url = NewsData.THE_INDIAN_EXPRESS;
        articlelist = new ArrayList<StringBuilder>();
        article_url_list = new ArrayList<URL[]>();

        try {
			/*	INDIAN EXPRESS HTML structure
			 * 	lead-story
			 * 		short-info
			 * 			<h1>
			 * 				<a>
			 * 			<p>
			 * 	--second-stories
			 * 		--story [2]+story last
			 * 	article first
			 * 		title
			 * 			<a href> text
			 * 		<p>
			 * 	articles[n(36)]
			 * */

            //extracting title
            Document doc = Jsoup.connect(url).get();
            StringBuilder article_title = new StringBuilder();
            article_title.append(doc.title());
            articlelist.add(article_title);
            article_url_list.add(new URL[]{new URL(url)});

            int c=1;

            //Extracting lead story
            Element leadstory = doc.getElementsByClass("lead-story").get(0).getElementsByClass("short-info").get(0);

            String a1="Article number "+c++;
            Element lead_header = leadstory.getElementsByTag("h1").get(0).getElementsByTag("a").get(0);
            URL lead_href = new URL(lead_header.attr("href"));
            String lead_header_text = lead_header.text();
            String lead_para = leadstory.getElementsByTag("p").get(0).text();

            StringBuilder article_lead = new StringBuilder();
            article_lead.append(a1).append("\n").append(lead_header_text).append("\n").append(lead_para);
            articlelist.add(article_lead);
            article_url_list.add(new URL[] {lead_href});


			/*//Extracting second stories
				Element second_stories = doc.getElementsByClass("second-stories").get(0);
				Elements story = second_stories.getElementsByClass("story");
				story.add ( second_stories.getElementsByClass("story last").get(0) );
				for(Element s:story) {
					String s1="Article number "+c++;
					String s2="Header: "+s.getElementsByTag("h6").get(0).text();
					String s3="Content: "+s.getElementsByTag("p").get(0).text();
					String s4="------------------------------------";
					String s5[]={s1,s2,s3,s4};
					articlelist.add(s5);
				}*/

            //Extracting headline first
            String b1="Article number "+c++;
            Element headline_first = doc.getElementsByClass("articles first").get(0);
            //header
            Element header_first = headline_first.getElementsByClass("title").get(0).getElementsByTag("a").get(0);
            URL header_first_href = new URL(header_first.attr("href"));
            String header_first_text = header_first.text();
            //paragraph (text)
            String content_first = headline_first.getElementsByTag("p").get(0).text();
            StringBuilder article_first = new StringBuilder();
            article_first.append(b1).append("\n").append(header_first_text).append("\n").append(content_first);
            articlelist.add(article_first);
            article_url_list.add(new URL[] {header_first_href});


            //extracting remaining headlines
            Elements headlines_rest = doc.getElementsByClass("articles");
            for(Element h:headlines_rest) {

                String c1="Article number "+c++;

                Element header_rest = h.getElementsByClass("title").get(0).getElementsByTag("a").get(0);
                URL header_rest_href = new URL(header_rest.attr("href"));
                String header_rest_text = header_rest.text();

                //paragraph (text)
                String content_rest = h.getElementsByTag("p").get(0).text();
                StringBuilder article_rest = new StringBuilder();
                article_rest.append(c1).append("\n").append(header_rest_text).append("\n").append(content_rest);
                articlelist.add(article_rest);
                article_url_list.add(new URL[] {header_rest_href});
            }

        } catch(IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
