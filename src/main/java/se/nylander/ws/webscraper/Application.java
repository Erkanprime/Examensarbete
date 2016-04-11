package se.nylander.ws.webscraper;

import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.nylander.ws.webscraper.dao.ShopDao;
import se.nylander.ws.webscraper.dao.imp.ShopDaoImp;
import se.nylander.ws.webscraper.parser.json.JsonParser;
import se.nylander.ws.webscraper.parser.jsoup.ForumLeagueParser;
import se.nylander.ws.webscraper.parser.jsoup.ForumThreadParser;
import se.nylander.ws.webscraper.service.ShopService;
import se.nylander.ws.webscraper.service.imp.ShopServiceImp;

/**
 * Created by erik.nylander on 2016-03-16.
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String args[]) throws Exception{
        SpringApplication.run(Application.class);
    }

    @Bean
    public ForumLeagueParser forumLeagueParser() {
        return new ForumLeagueParser();
    }

    @Bean
    public ForumThreadParser forumThreadParser() {
        return new ForumThreadParser();
    }

    @Bean
    public ShopService shopService(){
        return new ShopServiceImp();
    }

    @Bean
    public ShopDao shopDao() {
        return new ShopDaoImp();
    }

    @Bean
    public JsonParser jsonParser(){
        return new JsonParser();
    }
}
