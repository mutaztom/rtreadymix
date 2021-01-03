package com.rationalteam.rtreadymix.data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Tblnews {
    private Integer id;
    private String item;
    private String details;

    @Id
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblnews tblnews = (Tblnews) o;
        return id == tblnews.id &&
                Objects.equals(item, tblnews.item) &&
                Objects.equals(details, tblnews.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, details);
    }
}
