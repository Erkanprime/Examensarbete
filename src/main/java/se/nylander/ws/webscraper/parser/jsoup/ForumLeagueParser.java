package se.nylander.ws.webscraper.parser.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.nylander.ws.webscraper.config.ScraperConstants;
import se.nylander.ws.webscraper.exception.JavascriptJsonFormatException;
import se.nylander.ws.webscraper.model.Shop;
import se.nylander.ws.webscraper.parser.jsoup.util.DocUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by erik.nylander on 2016-03-22.
 */
public class ForumLeagueParser {

    private HashMap<String, String> forumLinks = new HashMap<>();

    private static Logger log = LoggerFactory.getLogger(ForumLeagueParser.class);

    @Autowired
    private ForumThreadParser forumThreadParser;

    public ForumLeagueParser(){}

    /**
     * Fetches all trading-shop-league urls
     */
    public void initForumParsing() {
        try {
            final Document doc = DocUtil.getDocument("/forum");

            final Elements forumLinksElements = doc.getElementsByClass("forum_name").select("div.name");

            forumLinksElements.stream()
                    .filter(name -> name.text().contains("Shops"))
                    .forEach(link -> {
                        String name = link.text();
                        String href = link.getElementsByAttribute("href").get(0).attributes().get("href");
                        forumLinks.put(name, href);
                    });

        } catch (IOException e) {
            log.warn("Coulden't parse: " + ScraperConstants.URL + "/forum\n", e);;
        }
    }

    /**
     * Fetches all trading-shop thread urls from a league
     */
    public void startForumParsing(){

        if (forumLinks.isEmpty()) {
            return;
        }

        List<Shop> indexedShops = new ArrayList<>();
        List<String> shopLinks = new ArrayList<>();

        for (String href : forumLinks.values()) {

            log.info("###############################################");
            log.info("####### Processing league: " + href);
            log.info("###############################################");

            try {
                forumThreadParser.setCurrentLeague(href);
                shopLinks = forumThreadParser.extractShopLinks(href);
            } catch (IOException e) {
                log.warn("Coulden't parse/connect to League: " + ScraperConstants.URL + href + "\n", e);
            }

            // Parse the content of each thread-post
            if (!shopLinks.isEmpty()) {
                shopLinks.stream().forEach(shopLink -> {
                    log.info("###### Parsing forum thread: " + shopLink + " ######");
                    try {
                        indexedShops.add(forumThreadParser.readForumLinksShops(shopLink));

                    } catch (Exception e) {
                        log.warn("Couldent parse/connect to Thread: " + ScraperConstants.URL + shopLink + "\n");
                    }
                });
            }

            System.out.println("##");
        }
        System.out.println("##");
    }
}
