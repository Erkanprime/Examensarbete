package se.nylander.ws.webscraper.dao;

import se.nylander.ws.webscraper.model.Shop;

/**
 * Created by erik.nylander on 2016-04-04.
 */
public interface ShopDao {

    void save(Shop shop);

    Shop getLatestIndexed(String league);

    Shop getShopByNameAndLeague(String league, String shopName);

    Shop update(Shop shop);
}
