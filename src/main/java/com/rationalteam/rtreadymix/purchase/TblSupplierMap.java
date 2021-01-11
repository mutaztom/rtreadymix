/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rationalteam.rtreadymix.purchase;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mutaz
 */
@Entity
@Table(name = "tblSupplierMap")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TblSupplierMap.findAll", query = "SELECT t FROM TblSupplierMap t"),
    @NamedQuery(name = "TblSupplierMap.findById", query = "SELECT t FROM TblSupplierMap t WHERE t.id = :id"),
    @NamedQuery(name = "TblSupplierMap.findByIsService", query = "SELECT t FROM TblSupplierMap t WHERE t.isService = :isService"),
    @NamedQuery(name = "TblSupplierMap.findByItemid", query = "SELECT t FROM TblSupplierMap t WHERE t.itemid = :itemid"),
    @NamedQuery(name = "TblSupplierMap.findByMaincat", query = "SELECT t FROM TblSupplierMap t WHERE t.maincat = :maincat"),
    @NamedQuery(name = "TblSupplierMap.findBySuppid", query = "SELECT t FROM TblSupplierMap t WHERE t.suppid = :suppid"),
    @NamedQuery(name = "TblSupplierMap.findByMappedToMain", query = "SELECT t FROM TblSupplierMap t WHERE t.mappedToMain = :mappedToMain")})
public class TblSupplierMap implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "isService")
    private Boolean isService;
    @Column(name = "itemid")
    private Integer itemid;
    @Column(name = "maincat")
    private Integer maincat;
    @Column(name = "suppid")
    private Integer suppid;
    @Column(name = "mappedToMain")
    private Boolean mappedToMain;

    public TblSupplierMap() {
    }

    public TblSupplierMap(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsService() {
        return isService;
    }

    public void setIsService(Boolean isService) {
        this.isService = isService;
    }

    public Integer getItemid() {
        return itemid;
    }

    public void setItemid(Integer itemid) {
        this.itemid = itemid;
    }

    public Integer getMaincat() {
        return maincat;
    }

    public void setMaincat(Integer maincat) {
        this.maincat = maincat;
    }

    public Integer getSuppid() {
        return suppid;
    }

    public void setSuppid(Integer suppid) {
        this.suppid = suppid;
    }

    public Boolean getMappedToMain() {
        return mappedToMain;
    }

    public void setMappedToMain(Boolean mappedToMain) {
        this.mappedToMain = mappedToMain;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TblSupplierMap)) {
            return false;
        }
        TblSupplierMap other = (TblSupplierMap) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.rationalteam.erpcore.data.TblSupplierMap[ id=" + id + " ]";
    }
    
}
