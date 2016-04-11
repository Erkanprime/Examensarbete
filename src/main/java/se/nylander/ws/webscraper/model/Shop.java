package se.nylander.ws.webscraper.model;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by erik.nylander on 2016-02-22.
 */
@Entity
@Table(name = "SHOP")
public class Shop implements Serializable{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SHOP_ID", nullable = false)
    private Long id;

    @Column(name = "SHOP_NAME")
    private String shopName;

    @Column(name = "SHOP_OWNER")
    private String shopOwner;

    @Column(name = "LAST_EDITED")
    private String lastEdited;

    @Column(name = "THREAD_LINK")
    private String threadLink;

    @Column(name = "TIME_OF_INDEXED", nullable = false)
    private LocalDateTime timeOfIndexed;

    @Column(name = "LEAGUE")
    private String league;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "shop", orphanRemoval = true)
    private List<TradeItem> tradeItems;


    public Shop(String shopName, String shopOwner, String lastEdited, String league, LocalDateTime timeOfIndexed){
        this.shopName = shopName;
        this.shopOwner = shopOwner;
        this.lastEdited = lastEdited;
        this.league = league;
        this.timeOfIndexed = timeOfIndexed;
    }

    public Shop() {
    }


    public List<TradeItem> getTradeItems() {
        return tradeItems;
    }

    public void setTradeItems(List<TradeItem> tradeItems) {
        this.tradeItems = tradeItems;
    }

    public String getLastEdited() {
        return lastEdited;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopOwner() {
        return shopOwner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastEdited(String lastEdited) {
        this.lastEdited = lastEdited;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShopOwner(String shopOwner) {
        this.shopOwner = shopOwner;
    }

    public LocalDateTime getTimeOfIndexed() {
        return timeOfIndexed;
    }

    public void setTimeOfIndexed(LocalDateTime timeOfIndexed) {
        this.timeOfIndexed = timeOfIndexed;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getThreadLink() {
        return threadLink;
    }

    public void setThreadLink(String threadLink) {
        this.threadLink = threadLink;
    }
}
