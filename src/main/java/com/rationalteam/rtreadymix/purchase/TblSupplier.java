/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rationalteam.rtreadymix.purchase;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mutaz
 */
@Entity
@Table(name = "TblSupplier")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TblSupplier.findAll", query = "SELECT t FROM TblSupplier t"),
    @NamedQuery(name = "TblSupplier.findById", query = "SELECT t FROM TblSupplier t WHERE t.id = :id"),
    @NamedQuery(name = "TblSupplier.findByLongName", query = "SELECT t FROM TblSupplier t WHERE t.longName = :longName"),
    @NamedQuery(name = "TblSupplier.findByShortName", query = "SELECT t FROM TblSupplier t WHERE t.shortName = :shortName"),
    @NamedQuery(name = "TblSupplier.findByWebSite", query = "SELECT t FROM TblSupplier t WHERE t.webSite = :webSite"),
    @NamedQuery(name = "TblSupplier.findByAddress", query = "SELECT t FROM TblSupplier t WHERE t.address = :address"),
    @NamedQuery(name = "TblSupplier.findByISServiceProv", query = "SELECT t FROM TblSupplier t WHERE t.iSServiceProv = :iSServiceProv"),
    @NamedQuery(name = "TblSupplier.findByISProductProv", query = "SELECT t FROM TblSupplier t WHERE t.iSProductProv = :iSProductProv"),
    @NamedQuery(name = "TblSupplier.findByISActive", query = "SELECT t FROM TblSupplier t WHERE t.iSActive = :iSActive"),
    @NamedQuery(name = "TblSupplier.findByType", query = "SELECT t FROM TblSupplier t WHERE t.type = :type"),
    @NamedQuery(name = "TblSupplier.findByAccountNo", query = "SELECT t FROM TblSupplier t WHERE t.accountNo = :accountNo"),
    @NamedQuery(name = "TblSupplier.findByCountry", query = "SELECT t FROM TblSupplier t WHERE t.country = :country")})
public class TblSupplier implements Serializable {

    @Column(name = "currency")
    private Integer currency;
    private static final long serialVersionUID = 1L;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "LongName")
    private String longName;
    @Column(name = "ShortName")
    private String shortName;
    @Column(name = "WebSite")
    private String webSite;
    @Column(name = "Address")
    private String address;
    @Column(name = "ISServiceProv")
    private Boolean iSServiceProv;
    @Column(name = "ISProductProv")
    private Boolean iSProductProv;
    @Column(name = "ISActive")
    private Boolean iSActive;
    @Column(name = "Type")
    private Integer type;
    @Column(name = "AccountNo")
    private String accountNo;
    @Column(name = "country")
    private Integer country;
    @Column(name = "city")
    private Integer city;
    @Column(name = "item")
    private String item;
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;
    @Column(name = "fax")
    private String fax;
    @Column(name = "paymentmethod")
    private Integer paymentMethod;
    @Column(name = "paymenttype")
    private String paymenttype;
    @Column(name = "regno")
    private String regno;
    @Column(name = "aritem")
    private String aritem;
    @Column(name = "araddress")
    private String araddress;
    @Column(name = "location")
    private String location;
    @Column(name = "owner")
    private String owner;

    public TblSupplier() {
    }

    public TblSupplier(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getISServiceProv() {
        return iSServiceProv;
    }

    public void setISServiceProv(Boolean iSServiceProv) {
        this.iSServiceProv = iSServiceProv;
    }

    public Boolean getISProductProv() {
        return iSProductProv;
    }

    public void setISProductProv(Boolean iSProductProv) {
        this.iSProductProv = iSProductProv;
    }

    public Boolean getISActive() {
        return iSActive;
    }

    public void setISActive(Boolean iSActive) {
        this.iSActive = iSActive;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }


    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }


    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TblSupplier)) {
            return false;
        }
        TblSupplier other = (TblSupplier) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


    public String toString() {
        return "com.rationalteam.FinanSys.Purchase.data.TblSupplier[ id=" + id + " ]";
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymenttype() {
        return paymenttype;
    }

    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

    
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getAritem() {
        return aritem;
    }

    public void setAritem(String aritem) {
        this.aritem = aritem;
    }

    public String getAraddress() {
        return araddress;
    }

    public void setAraddress(String araddress) {
        this.araddress = araddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
