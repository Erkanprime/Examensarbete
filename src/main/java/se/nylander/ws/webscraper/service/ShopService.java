package se.nylander.ws.webscraper.service;

import se.nylander.ws.webscraper.model.Shop;

import java.util.Optional;

/**
 * Created by erik.nylander on 2016-03-31.
 */
public interface ShopService {

    void save(Shop shop);

    Optional<Shop> getLatestIndexed(String league);

    Optional<Shop> getShopByNameAndLeague(String league, String shopName);

    Shop update(Shop shop);

}
