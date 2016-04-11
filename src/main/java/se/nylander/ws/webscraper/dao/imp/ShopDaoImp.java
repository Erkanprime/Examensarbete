package se.nylander.ws.webscraper.dao.imp;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.nylander.ws.webscraper.dao.ShopDao;
import se.nylander.ws.webscraper.model.Shop;

import javax.persistence.*;
import java.util.List;

/**
 * Created by erik.nylander on 2016-04-04.
 */
@Repository
@Transactional
public class ShopDaoImp implements ShopDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(Shop shop) {
        em.persist(shop);

    }

    @Override
    public Shop getLatestIndexed(String league) {
        Query q = em.createQuery("Select s from Shop s where s.league = :league order by s.timeOfIndexed desc", Shop.class);
        q.setParameter("league", league);
        Shop shop = (Shop) q.getResultList().get(0);
        return shop;
    }

    @Override
    public Shop getShopByNameAndLeague(String league, String shopName) {
        Query q = em.createQuery("Select s from Shop s where s.league = :league and s.shopName = :shopName", Shop.class);
        q.setParameter("league", league);
        q.setParameter("shopName", shopName);
        Shop shop = (Shop) q.getResultList().get(0);
        return shop;
    }

    @Override
    public Shop update(Shop shop) {
        return em.merge(shop);
    }


}
