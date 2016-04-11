package se.nylander.ws.webscraper.service.imp;

import org.hibernate.mapping.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import se.nylander.ws.webscraper.dao.ShopDao;
import se.nylander.ws.webscraper.model.Shop;
import se.nylander.ws.webscraper.service.ShopService;

import java.util.Optional;

/**
 * Created by erik.nylander on 2016-03-31.
 */
@Service
public class ShopServiceImp implements ShopService{

    @Autowired
    private ShopDao shopDao;

    @Override
    public void save(Shop shop) {
        Optional<Shop> optional = getShopByNameAndLeague(shop.getLeague(), shop.getShopName());
        if(optional.isPresent()){
            shop.setId(optional.get().getId());
            shopDao.update(shop);
        }else {
            shopDao.save(shop);
        }
    }



    @Override
    public Optional<Shop> getLatestIndexed(String league) {
            try {
                return Optional.ofNullable(shopDao.getLatestIndexed(league));
            } catch (IndexOutOfBoundsException e) {
                return Optional.empty();
            }
    }

    @Override
    public Optional<Shop> getShopByNameAndLeague(String league, String shopName) {
        try {

            return Optional.ofNullable(shopDao.getShopByNameAndLeague(league, shopName));

        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public Shop update(Shop shop) {
        return shopDao.update(shop);
    }


}
