package se.nylander.ws.webscraper.parser.jsoup.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import se.nylander.ws.webscraper.config.ScraperConstants;

import java.io.IOException;

/**
 * Created by erik.nylander on 2016-04-01.
 */
public class DocUtil {

    private static final String USER_AGENT = "Mozilla";

    private static final Integer MAX_BODY_SIZE = 0;

    private static final Integer TIMEOUT = 10000;

    public static Document getDocument(String url) throws IOException{

        return Jsoup
                .connect(ScraperConstants.URL + url)
                .timeout(TIMEOUT)
                .userAgent(USER_AGENT)
                .maxBodySize(MAX_BODY_SIZE)
                .get();
    };
}
