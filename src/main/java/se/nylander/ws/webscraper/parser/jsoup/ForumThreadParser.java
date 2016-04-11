package se.nylander.ws.webscraper.parser.jsoup;

import org.hibernate.service.spi.InjectService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.nylander.ws.webscraper.config.ScraperConstants;
import se.nylander.ws.webscraper.model.Shop;
import se.nylander.ws.webscraper.model.TradeItem;
import se.nylander.ws.webscraper.parser.json.JsonParser;
import se.nylander.ws.webscraper.parser.jsoup.util.DocUtil;
import se.nylander.ws.webscraper.service.ShopService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by erik.nylander on 2016-03-22.
 */
public class ForumThreadParser {

    private static Logger log = LoggerFactory.getLogger(ForumThreadParser.class);

    @Autowired
    private JsonParser jsonParser;

    private String currentLeague;

    @Autowired
    private ShopService shopService;

    public void setCurrentLeague(String currentLeague) {
        this.currentLeague = currentLeague;
    }

    public List<String> extractShopLinks(String href) throws IOException {
        Document htmlBody;
        try {
            htmlBody = DocUtil.getDocument(href);

            //div.title
            Elements tableTitles = htmlBody.getElementById("view_forum_table").select("tr");


            List<String> threads = tableTitles.stream()
                            .filter(tr -> !tr.select("div.sticky.off").isEmpty())
                            .map(title -> title.select("div.title").get(0).child(0).attributes().get("href"))
                            .collect(Collectors.toList());

            Collections.reverse(threads); //Temporärt
            return threads;
        } catch (IOException e) {
            throw e;
        }

    }

    public Shop readForumLinksShops(String href) throws Exception {
        String dirtyJson;
        Document htmlBody = null;
        try {
            htmlBody = DocUtil.getDocument(href);

            dirtyJson = htmlBody
                       .select("script")
                       .get(10)
                       .toString();

        } catch (Exception e) {
            throw e;
        }

        Shop currentShop = extractShopMetaInfo(htmlBody);
        currentShop.setThreadLink(href);

        Optional<List<TradeItem>> tradeItems = jsonParser.processJsonItemDataString(dirtyJson);

        if (tradeItems.isPresent()) {
            tradeItems.get().stream().forEach(item -> item.setShop(currentShop)); //Mappning för jpa
            currentShop.setTradeItems(findTradeItemPrices(tradeItems.get(), htmlBody));

        }

        log.info("Fetched shop: " + currentShop.getShopName() + " Owner: " + currentShop.getShopOwner()
                + "Last edited: " + currentShop.getLastEdited() + " Number of items: " + currentShop.getTradeItems().size());

        System.out.println("##");
        shopService.save(currentShop);

        return currentShop;
    }

    private Shop extractShopMetaInfo(Document htmlBody) {
        String postedByDiv = htmlBody.getElementsByClass(ScraperConstants.SHOP_POSTED_BY).get(0).getElementsByTag("span").text();
        String shopOwner = postedByDiv.substring(0, postedByDiv.indexOf(" "));
        String lastEdited = postedByDiv.substring(postedByDiv.indexOf(" "));
        String shopName = htmlBody.select("h1.topBar").get(0).ownText();

        return new Shop(shopName, shopOwner, lastEdited, currentLeague, LocalDateTime.now());
    }

    private List<TradeItem> findTradeItemPrices(List<TradeItem> tradeItems, Document htmlBody) {
        List<Node> nodes =  cleanUpElements(htmlBody);
        HashMap<String, List<String>> itemsAndPrices = new HashMap<>();
        itemsAndPrices.put("NaN", new ArrayList<>());

        // Hitta regex för pris (~*/*)

        for(int i=0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (element.hasClass(ScraperConstants.ITEM_FRAGMENT)) {

                    itemsAndPrices = findItemAndPriceFromNodes(nodes, itemsAndPrices, "NaN");

                } else
                if (element.hasClass("spoiler")) {
                    Elements spoilerTitle = element.getAllElements().select("div.spoilerTitle");
                    Elements spoilerContent = element.getAllElements().select("div.spoilerContent");

                    String titlePrice = checkSpoilerTitlePrice.apply(spoilerTitle);

                    for (int x = 0; x < spoilerContent.size(); x++) {
                        List<Node> spoilerContentNodes = spoilerContent.get(x).childNodes();
                        itemsAndPrices = findItemAndPriceFromNodes(spoilerContentNodes, itemsAndPrices, titlePrice);

                    }

                }
            }
        }

        return mapItemPrices(itemsAndPrices, tradeItems);
    }


    private Predicate<TextNode> checkIfPriceNode = node -> node.text().contains("~") && node.text().contains("/");


    private Function<Elements, String> checkSpoilerTitlePrice = spoilerTitle -> {
        for (Element titlePrice : spoilerTitle){
            if (titlePrice.text().contains("~") && titlePrice.text().contains("/")) {
                return titlePrice.text();
            }
        }
        return "NaN";
    };

    /**
     * Checks if the nextNode after the item is a textnode containing a price. If not sets the price to NaN or titlePrice(if in spoilerTag with title price)
     * @param nodes
     * @param itemsAndPrices
     * @param price
     * @return
     */
    private HashMap<String, List<String>> findItemAndPriceFromNodes(List<Node> nodes, HashMap<String, List<String>> itemsAndPrices, String price) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) instanceof Element && ((Element) nodes.get(i)).hasClass(ScraperConstants.ITEM_FRAGMENT)) {
                Element item = (Element) nodes.get(i);

                if(nodes.size()> 1) {
                    for (Node node : nodes.subList(i + 1, nodes.size())) {
                        if (node instanceof Element && ((Element) node).hasClass(ScraperConstants.ITEM_FRAGMENT)) {
                            itemsAndPrices = putItemPrice(itemsAndPrices, price, item.id());
                            break;
                        } else
                        if (node instanceof TextNode) {
                            TextNode textNode = (TextNode) node;
                            if (checkIfPriceNode.test(textNode)) {
                                itemsAndPrices = putItemPrice(itemsAndPrices, textNode.text(), item.id());
                                break;
                            }
                        }

                    }
                } else {
                    itemsAndPrices = putItemPrice(itemsAndPrices, price, item.id());
                }
            }
        }
        return itemsAndPrices;
    }

    private HashMap<String, List<String>> putItemPrice(HashMap<String, List<String>> itemsAndPrices, String key, String value) {

        if (itemsAndPrices.containsKey(key)) {
            itemsAndPrices.get(key).add(value);
        } else {
            itemsAndPrices.put(key, new ArrayList<>(Arrays.asList(value)));
        }
        return itemsAndPrices;
    }

    private List<TradeItem> mapItemPrices(HashMap<String, List<String>> itemsAndPrices, List<TradeItem> tradeItems){
        itemsAndPrices.entrySet().stream().forEach(s -> {

            s.getValue().stream().forEach(item -> tradeItems
                    .get(Integer.parseInt(getFragmentIdFromString(item)))
                    .setPrice(s.getKey()));
        });
        return tradeItems;
    }

    private String getFragmentIdFromString(String current) {
        return current.substring(current.lastIndexOf("-")+1);
    }

    private List<Node> cleanUpElements(Document htmlBody){
        List<Node> nodeList =  htmlBody.getElementsByClass("content").get(0).childNodes().stream().filter(n ->
                (n instanceof Element && (((Element)n).hasClass(ScraperConstants.ITEM_FRAGMENT)
                        || ((Element)n).hasClass("spoiler")))
                        || (n instanceof TextNode && ((TextNode)n).text().contains("~")))
                .collect(Collectors.toList());
        for (Node n:nodeList) {
            if (n instanceof TextNode && ((TextNode) n).text().contains("\n")) {
                TextNode textNode = (TextNode) n;
                textNode.text(textNode.text().replaceAll("\n",""));
            }
        }
        return nodeList;
    }
}
