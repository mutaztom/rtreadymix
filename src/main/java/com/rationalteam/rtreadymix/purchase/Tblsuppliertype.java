package com.rationalteam.rtreadymix.purchase;

import com.rationalteam.rterp.erpcore.data.TblAbstractOption;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "tblsuppliertype", schema = "cloudrest", catalog = "")
public class Tblsuppliertype {
    private Integer id;
    private String item;
    private Integer mainid;
    private String aritem;
    private String forfield;


    public String getForfield() {
        return forfield;
    }


    public void setForfield(String s) {
        forfield = s;
    }

    @javax.persistence.Id
    @Basic
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "item")
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getMainid() {
        return mainid;
    }

    public void setMainid(Integer mid) {
        this.mainid = mid;
    }

    public String getAritem() {
        return aritem;
    }

    public void setAritem(String s) {
        this.aritem = s;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblsuppliertype that = (Tblsuppliertype) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(item, that.item);
    }


    public int hashCode() {
        return Objects.hash(id, item);
    }
}
