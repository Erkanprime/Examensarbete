package se.nylander.ws.webscraper.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by erik.nylander on 2016-02-19.
 */
@Entity
@Table(name = "TRADEITEM")
public class TradeItem implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TRADEITEM_ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_ID")
    private Shop shop;

    @Column(name = "CORRUPTED")
    private Boolean corrupted;

    @Column(name = "ITEM_TYPE")
    private String itemType;

    @Column(name = "LEVEL_REQ")
    private Integer levelRequirment;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Mods> mods = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSockets> itemSockets = new ArrayList<>();

    @Column(name = "LEAGUE")
    private String league;

    @Column(name = "VERIFIED")
    private Boolean verified;

    @Column(name = "ICON")
    private String icon;

    @Column(name = "IDENTIFIED")
    private Boolean identified;

    @Column(name = "NAME")
    private String name;

    @ElementCollection
    private Map<String, String> baseProperties = new HashMap<>();

    @Column(name = "PRICE", nullable = true)
    private String price;

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price; }

    public Boolean getCorrupted() {
        return corrupted;
    }

    public void setCorrupted(Boolean corrupted) {
        this.corrupted = corrupted;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Integer getLevelRequirment() {
        return levelRequirment;
    }

    public void setLevelRequirment(Integer levelRequirment) {
        this.levelRequirment = levelRequirment;
    }

    public List<ItemSockets> getItemSockets() {
        return itemSockets;
    }

    public void addItemSocket(Integer groupId, String colour) {
        this.itemSockets.add(new ItemSockets(colour, groupId));
    }

    public void setItemSockets(List<ItemSockets> itemSockets) {
        this.itemSockets = itemSockets;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getIdentified() {
        return identified;
    }

    public void setIdentified(Boolean identified) {
        this.identified = identified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getBaseProperties() {
        return baseProperties;
    }

    public void setBaseProperties(String property, String value) {
        this.baseProperties.put(property, value);
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMods(Set<Mods> mods) {
        this.mods = mods;
    }

    public Set<Mods> getMods() {
        return mods;
    }

    public void addMod(String key, Integer value) {

    }

    @Override
    public String toString() {
        return "TradeItem{" +
                "corrupted=" + corrupted +
                ", itemType='" + itemType + '\'' +
                ", levelRequirment=" + levelRequirment +
                ",\n itemSockets=" + itemSockets +
                ", league='" + league + '\'' +
                ", verified=" + verified +
                ",\n icon='" + icon + '\'' +
                ", identified=" + identified +
                ", name='" + name + '\'' +
                ",\n baseProperties=" + baseProperties +
                ", price='" + price + '\'' +
                '}' +
                "\n";
    }
}
