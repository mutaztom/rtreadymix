package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.CRtDataObject;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rterp.erpcore.data.TblCity;
import com.rationalteam.rterp.erpcore.data.TblCountry;
import com.rationalteam.rterp.erpcore.data.TblProduct;
import com.rationalteam.rtreadymix.data.Tblclient;
import com.rationalteam.rtreadymix.data.Tblorder;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Objects;

public class Order extends CRtDataObject {
    Integer clientid;
    String location;
    Integer country;
    Integer state;
    Integer city;
    Double quantity;
    Integer type;
    String notes;
    Integer itemid;
    //the date of the order
    LocalDateTime ondate;
    //the date on which the order to be delivered
    LocalDateTime dateNeeded;
    enOrderStatus status;
    Double unitprice;
    Double usdprice;

    Tblorder data;
    private Integer province;

    public Order() {
        status = enOrderStatus.Created;
        unitprice = 0D;
        usdprice = 0D;
        ondate = LocalDateTime.now();
        //default to one day delay
        dateNeeded = LocalDateTime.now().plusDays(1);

    }

    @Override
    public Object getData() {
        data = new Tblorder();
        data.setNotes(notes);
        data.setState(state);
        data.setStatus(status.name());
        data.setDateNeeded(Time.valueOf(dateNeeded.toLocalTime()));
        data.setOndate(Time.valueOf(ondate.toLocalTime()));
        data.setItemid(itemid);
        data.setQuantity(quantity);
        data.setUsdprice(usdprice);
        data.setUnitprice(unitprice);
        data.setProvince(province);
        data.setCountry(country);
        data.setClientid(clientid);
        data.setCity(city);
        data.setItem(item);
        data.setId(id);
        return data;
    }

    @Override
    public void setData(Object o) {
        data = (Tblorder) o;
        id = data.getId();
        item = data.getItem();
        clientid = data.getClientid();
        country = data.getCountry();
        province = data.getProvince();
        state = data.getState();
        city = data.getCity();
        quantity = data.getQuantity();
        type = data.getType();
        itemid = data.getItemid();
        unitprice = data.getUnitprice();
        usdprice = data.getUsdprice();
        if (data.getStatus() != null)
            status = enOrderStatus.valueOf(data.getStatus());
        if (Objects.nonNull(data.getOndate()))
            ondate = LocalDateTime.from(data.getOndate().toLocalTime());
        if (Objects.nonNull(data.getDateNeeded()))
            dateNeeded = LocalDateTime.from(data.getDateNeeded().toLocalTime());
        notes = data.getNotes();
    }

    @Override
    public <T> Class<T> getDataType() {
        return null;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getOndate() {
        return ondate;
    }

    public void setOndate(LocalDateTime ondate) {
        this.ondate = ondate;
    }

    public LocalDateTime getDateNeeded() {
        return dateNeeded;
    }

    public void setDateNeeded(LocalDateTime dateNeeded) {
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

    public enOrderStatus getStatus() {
        return status;
    }

    public void setStatus(enOrderStatus status) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        if (!super.equals(o)) return false;
        Order order = (Order) o;
        return Objects.equals(clientid, order.clientid) &&
                Objects.equals(location, order.location) &&
                Objects.equals(country, order.country) &&
                Objects.equals(state, order.state) &&
                Objects.equals(city, order.city) &&
                Objects.equals(quantity, order.quantity) &&
                Objects.equals(type, order.type) &&
                Objects.equals(notes, order.notes) &&
                Objects.equals(itemid, order.itemid) &&
                Objects.equals(ondate, order.ondate) &&
                Objects.equals(dateNeeded, order.dateNeeded) &&
                Objects.equals(status, order.status) &&
                Objects.equals(data, order.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), clientid, location, country, state, city, quantity, type, notes, itemid, ondate, dateNeeded, status, data);
    }

    public void fromClientOrder(ClientOrder cord) {
        try {
            clientid = Math.toIntExact(MezoDB.getItemID(Tblclient.class.getSimpleName(), "item", cord.getClientid()));
            city = Math.toIntExact(MezoDB.getItemID(TblCity.class.getSimpleName(), "item", cord.getCity()));
            country = Math.toIntExact(MezoDB.getItemID(TblCountry.class.getSimpleName(), "item", cord.getCountry()));
            type = Math.toIntExact(MezoDB.getItemID(TblProduct.class.getSimpleName(), "item", cord.getType()));
            state= Math.toIntExact(MezoDB.getItemID("tblprovince", "item", cord.getState()));
            dateNeeded = LocalDateTime.parse(cord.getDateNeeded());
            ondate = LocalDateTime.parse(cord.getOndate());
            quantity = cord.getQuantity();
            unitprice=cord.getUnitprice();
            location=cord.getLocation();
            notes=cord.getNotes();
        }catch (Exception exp){
            Utility.ShowError(exp);
        }
    }
}
