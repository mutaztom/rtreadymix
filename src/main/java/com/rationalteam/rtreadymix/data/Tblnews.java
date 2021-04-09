package com.rationalteam.rtreadymix.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.Objects;
import java.sql.Time;

@Entity
public class Tblnews extends PanacheEntityBase {
    private Integer id;
    private String item;
    private String details;
    private Integer clientid;
    private Time ontime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "item", nullable = true, length = 200)
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Basic
    @Column(name = "details", nullable = true, length = -1)
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblnews tblnews = (Tblnews) o;
        return id == tblnews.id &&
                Objects.equals(item, tblnews.item) &&
                Objects.equals(details, tblnews.details);
    }


    public int hashCode() {
        return Objects.hash(id, item, details);
    }

    public Integer getClientid() {
        return clientid;
    }

    public void setClientid(Integer cid) {
        clientid = cid;
    }

    public Time getOntime() {
        return ontime;
    }

    public void setOntime(Time t) {
        ontime = t;
    }
}
