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
        q.setMaxResults(1);
        Shop shop = (Shop) q.getResultList().get(0);
        return shop;
    }

    @Override
    public Shop getShopByThreadAndLeague(String league, String thread) {
        Query q = em.createQuery("Select s from Shop s where s.league = :league and s.threadLink = :threadLink", Shop.class);
        q.setParameter("league", league);
        q.setParameter("threadLink", thread);
        q.setMaxResults(1);
        Shop shop = (Shop) q.getResultList().get(0);
        return shop;
    }

    @Override
    public Shop update(Shop shop) {
        return em.merge(shop);
    }


}
