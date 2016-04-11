package se.nylander.ws.webscraper.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by erik.nylander on 2016-02-19.
 */
@Entity
@Table(name = "ITEMSOCKETS")
public class ItemSockets {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ITEMSOCKET_ID", nullable = false)
    private Long id;

    @Column(name = "COLOUR", nullable = true)
    private String colour;

    @Column(name = "GROUP_ID", nullable = true)
    private Integer groupId;

    public ItemSockets(String color, Integer groupId){
        this.colour = color;
        this.groupId = groupId;
    }

    public ItemSockets() {
    }


    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
