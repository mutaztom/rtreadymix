package com.rationalteam.rtreadymix.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name="Tblorder")
@NamedQueries({@NamedQuery(name="Tblorder.findByClientid",query="select t from Tblorder t where t.clientid=:clientid")})
public class Tblorder extends PanacheEntityBase {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String item;
    private Integer country;
    private Integer state;
    private Integer city;
    private Integer province;
    private String location;
    private Double quantity;
    private Date ondate;
    private Date dateNeeded;
    private Integer clientid;
    private Integer itemid;
    private String status;
    private Double unitprice;
    private Double usdprice;
    private String notes;
    private Integer member;
    private Double rate;


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

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

   public Date getOndate() {
        return ondate;
    }

    public void setOndate(Date ondate) {
        this.ondate = ondate;
    }

    public Date getDateNeeded() {
        return dateNeeded;
    }

    public void setDateNeeded(Date dateNeeded) {
        this.dateNeeded = dateNeeded;
    }

    public Integer getClientid() {
        return clientid;
    }

    public void setClientid(Integer clientid) {
        this.clientid = clientid;
    }

    public Integer getItemid() {
        return itemid;
    }

    public void setItemid(Integer itemid) {
        this.itemid = itemid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getUnitprice() {
        return unitprice;
    }

    public void setUnitprice(Double unitprice) {
        this.unitprice = unitprice;
    }

    public Double getUsdprice() {
        return usdprice;
    }

    public void setUsdprice(Double usdprice) {
        this.usdprice = usdprice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tblorder)) return false;
        Tblorder tblorder = (Tblorder) o;
        return Objects.equals(id, tblorder.id) &&
                Objects.equals(item, tblorder.item) &&
                Objects.equals(country, tblorder.country) &&
                Objects.equals(state, tblorder.state) &&
                Objects.equals(city, tblorder.city) &&
                Objects.equals(province, tblorder.province) &&
                Objects.equals(location, tblorder.location) &&
                Objects.equals(quantity, tblorder.quantity) &&
                Objects.equals(ondate, tblorder.ondate) &&
                Objects.equals(dateNeeded, tblorder.dateNeeded) &&
                clientid.equals(tblorder.clientid) &&
                itemid.equals(tblorder.itemid);
    }


    public int hashCode() {
        return Objects.hash(id, item, country, state, city, province, location, quantity, ondate, dateNeeded, clientid, itemid);
    }

    public Integer getMember() {
        return member;
    }

    public void setMember(Integer member) {
        this.member = member;
    }
}
