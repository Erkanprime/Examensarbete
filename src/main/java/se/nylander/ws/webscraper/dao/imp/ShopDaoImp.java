package se.nylander.ws.webscraper.dao.imp;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import se.nylander.ws.webscraper.dao.ShopDao;
import se.nylander.ws.webscraper.model.Shop;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

/**
 * Created by erik.nylander on 2016-04-04.
 */

@Repository
public class ShopDaoImp implements ShopDao{

    EntityManagerFactory factory;

    @Override
    public void save(Shop shop) {
        EntityManager em = factory.createEntityManager();
        em.persist(shop);
    }
}
