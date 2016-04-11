package se.nylander.ws.webscraper.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by erik.nylander on 2016-04-06.
 */
@Entity
@Table(name = "MODS")
public class Mods {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MOD_ID")
    private Long id;

    @Column(name = "MIN_VALUE", nullable = true)
    private Integer minValue;

    @Column(name = "MAX_VALUE", nullable = true)
    private Integer maxValue;

    @Column(name = "MOD_NAME")
    private String modName;



    public Mods(String modName, Integer minValue, Integer maxValue){
        this.modName = modName;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Mods(){}

    public Long getId() {
        return id;
    }

    public String getModName() {
        return modName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setModName(String modName) {
        this.modName = modName;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public Integer getMinValue() {
        return minValue;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Mods){
            Mods mod = (Mods) obj;
            return this.modName.equals(mod.modName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return modName.hashCode();
    }
}
