package com.rationalteam.rtreadymix.data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Tblsuppliermap {
    private int id;
    private Short isService;
    private Integer itemid;
    private Integer maincat;
    private Integer suppid;
    private Boolean mappedToMain;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "isService", nullable = true)
    public Short getIsService() {
        return isService;
    }

    public void setIsService(Short isService) {
        this.isService = isService;
    }

    @Basic
    @Column(name = "itemid", nullable = true)
    public Integer getItemid() {
        return itemid;
    }

    public void setItemid(Integer itemid) {
        this.itemid = itemid;
    }

    @Basic
    @Column(name = "maincat", nullable = true)
    public Integer getMaincat() {
        return maincat;
    }

    public void setMaincat(Integer maincat) {
        this.maincat = maincat;
    }

    @Basic
    @Column(name = "suppid", nullable = true)
    public Integer getSuppid() {
        return suppid;
    }

    public void setSuppid(Integer suppid) {
        this.suppid = suppid;
    }

    @Basic
    @Column(name = "mappedToMain", nullable = true)
    public Boolean getMappedToMain() {
        return mappedToMain;
    }

    public void setMappedToMain(Boolean mappedToMain) {
        this.mappedToMain = mappedToMain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblsuppliermap that = (Tblsuppliermap) o;
        return id == that.id &&
                Objects.equals(isService, that.isService) &&
                Objects.equals(itemid, that.itemid) &&
                Objects.equals(maincat, that.maincat) &&
                Objects.equals(suppid, that.suppid) &&
                Objects.equals(mappedToMain, that.mappedToMain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isService, itemid, maincat, suppid, mappedToMain);
    }
}
