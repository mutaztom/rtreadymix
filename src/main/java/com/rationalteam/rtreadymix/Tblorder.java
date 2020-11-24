package com.rationalteam.rtreadymix;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table
public class Tblorder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String item;
    private Integer country;
    private Integer state;
    private Integer city;
    private Integer province;
    private String location;
    private Double quantity;
    private Integer type;
    private Time ondate;
    private Time dateNeeded;
    private Integer clientid;
    private Integer itemid;
    private String status;
    private Double unitprice;
    private Double usdprice;
    private String notes;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Time getOndate() {
        return ondate;
    }

    public void setOndate(Time ondate) {
        this.ondate = ondate;
    }

    public Time getDateNeeded() {
        return dateNeeded;
    }

    public void setDateNeeded(Time dateNeeded) {
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

    @Override
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
                Objects.equals(type, tblorder.type) &&
                Objects.equals(ondate, tblorder.ondate) &&
                Objects.equals(dateNeeded, tblorder.dateNeeded) &&
                clientid.equals(tblorder.clientid) &&
                itemid.equals(tblorder.itemid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, country, state, city, province, location, quantity, type, ondate, dateNeeded, clientid, itemid);
    }
}
