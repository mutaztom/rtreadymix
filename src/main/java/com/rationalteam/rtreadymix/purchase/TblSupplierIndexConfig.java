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
@Table(name = "tblSupplierIndexConfig")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TblSupplierIndexConfig.findAll", query = "SELECT t FROM TblSupplierIndexConfig t"),
    @NamedQuery(name = "TblSupplierIndexConfig.findById", query = "SELECT t FROM TblSupplierIndexConfig t WHERE t.id = :id"),
    @NamedQuery(name = "TblSupplierIndexConfig.findByItem", query = "SELECT t FROM TblSupplierIndexConfig t WHERE t.item = :item"),
    @NamedQuery(name = "TblSupplierIndexConfig.findByPvolumeIndex", query = "SELECT t FROM TblSupplierIndexConfig t WHERE t.pvolumeIndex = :pvolumeIndex"),
    @NamedQuery(name = "TblSupplierIndexConfig.findByDeliveryIndex", query = "SELECT t FROM TblSupplierIndexConfig t WHERE t.deliveryIndex = :deliveryIndex"),
    @NamedQuery(name = "TblSupplierIndexConfig.findByRejectIndex", query = "SELECT t FROM TblSupplierIndexConfig t WHERE t.rejectIndex = :rejectIndex"),
    @NamedQuery(name = "TblSupplierIndexConfig.findByPaymentIndex", query = "SELECT t FROM TblSupplierIndexConfig t WHERE t.paymentIndex = :paymentIndex"),
    @NamedQuery(name = "TblSupplierIndexConfig.findByQualityIndex", query = "SELECT t FROM TblSupplierIndexConfig t WHERE t.qualityIndex = :qualityIndex")})
public class TblSupplierIndexConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "item")
    private String item;
    @Column(name = "pvolumeIndex")
    private Integer pvolumeIndex;
    @Column(name = "deliveryIndex")
    private Integer deliveryIndex;
    @Column(name = "rejectIndex")
    private Integer rejectIndex;
    @Column(name = "paymentIndex")
    private Integer paymentIndex;
    @Column(name = "qualityIndex")
    private Integer qualityIndex;
    @Column(name = "commitmentindex")
    private Integer commitmentindex;

    public TblSupplierIndexConfig() {
    }

    public TblSupplierIndexConfig(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getPvolumeIndex() {
        return pvolumeIndex;
    }

    public void setPvolumeIndex(Integer pvolumeIndex) {
        this.pvolumeIndex = pvolumeIndex;
    }

    public Integer getDeliveryIndex() {
        return deliveryIndex;
    }

    public void setDeliveryIndex(Integer deliveryIndex) {
        this.deliveryIndex = deliveryIndex;
    }

    public Integer getRejectIndex() {
        return rejectIndex;
    }

    public void setRejectIndex(Integer rejectIndex) {
        this.rejectIndex = rejectIndex;
    }

    public Integer getPaymentIndex() {
        return paymentIndex;
    }

    public void setPaymentIndex(Integer paymentIndex) {
        this.paymentIndex = paymentIndex;
    }

    public Integer getQualityIndex() {
        return qualityIndex;
    }

    public void setQualityIndex(Integer qualityIndex) {
        this.qualityIndex = qualityIndex;
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
        if (!(object instanceof TblSupplierIndexConfig)) {
            return false;
        }
        TblSupplierIndexConfig other = (TblSupplierIndexConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.rationalteam.Purchase.data.TblSupplierIndexConfig[ id=" + id + " ]";
    }

    public Integer getCommitmentindex() {
        return commitmentindex;
    }

    public void setCommitmentindex(Integer commitmentindex) {
        this.commitmentindex = commitmentindex;
    }
    
}
