package com.rationalteam.rtreadymix;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rterp.erpcore.CRtDataObject;
import com.rationalteam.rterp.erpcore.CSearchOption;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rterp.erpcore.data.TblCity;
import com.rationalteam.rterp.erpcore.data.TblCountry;
import com.rationalteam.rterp.erpcore.data.TblProduct;
import com.rationalteam.rtreadymix.data.Tblclient;
import com.rationalteam.rtreadymix.data.Tblorder;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    Integer member;
    //the date of the order
    LocalDateTime ondate;
    //the date on which the order to be delivered
    LocalDateTime dateNeeded;
    enOrderStatus status;
    Double unitprice;
    Double usdprice;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    Tblorder data;
    private Integer province;

    public Order() {
        status = enOrderStatus.Created;
        unitprice = 0D;
        usdprice = 0D;
        ondate = LocalDateTime.now();
        //default to one day delay
        dateNeeded = LocalDateTime.now().plusDays(1);
        setDbTable(Tblorder.class.getSimpleName());
    }

    @Override
    protected void initSearch() {
        super.initSearch();
        searchOptions.forEach(o -> o.setOptionTable("tbloptions"));
    }

    @Override
    public Object getData() {
        data = new Tblorder();
        try {
            data.setId(id);
            data.setNotes(notes);
            data.setState(state);
            data.setStatus(status.name());
            data.setDateNeeded(Date.valueOf(dateNeeded.toLocalDate()));
            data.setOndate(Date.valueOf(ondate.toLocalDate()));
            data.setItemid(itemid);
            data.setQuantity(quantity);
            data.setUsdprice(usdprice);
            data.setUnitprice(unitprice);
            data.setProvince(province);
            data.setCountry(country);
            data.setClientid(clientid);
            data.setCity(city);
            data.setItem(item);
            data.setMember(member);
            data.setType(type);
            data.setLocation(location);
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return data;
    }

    @Override
    public void setData(Object o) {
        try {
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
                ondate = LocalDateTime.of(data.getOndate().toLocalDate(), LocalDateTime.now().toLocalTime());
            if (Objects.nonNull(data.getDateNeeded()))
                dateNeeded = LocalDateTime.of(data.getDateNeeded().toLocalDate(), LocalDateTime.now().toLocalTime());
            notes = data.getNotes();
            type = data.getType();
            member = data.getMember();
            location = data.getLocation();
        } catch (IllegalArgumentException e) {
            Utility.ShowError(e);
        }
    }

    @Override
    public <T> Class<T> getDataType() {
        return (Class<T>) Tblorder.class;
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

    public Integer getMember() {
        return member;
    }

    public void setMember(Integer member) {
        this.member = member;
    }

    @Transactional
    public void fromClientOrder(ClientOrder cord) {
        try {
            if (cord.getId() != null)
                id = cord.getId() > 0 ? cord.getId() : null;
            clientid = Long.valueOf(MezoDB.getItemID(Tblclient.class.getSimpleName(), "email", cord.getClientid())).intValue();
            city = Long.valueOf(MezoDB.getItemID(TblCity.class.getSimpleName(), "item", cord.getCity())).intValue();
            country = Long.valueOf(MezoDB.getItemID(TblCountry.class.getSimpleName(), "item", cord.getCountry())).intValue();
            type = Long.valueOf(MezoDB.getItemID(TblProduct.class.getSimpleName(), "item", cord.getGrade())).intValue();
            state = Long.valueOf(MezoDB.getItemID("tblprovince", "item", cord.getState())).intValue();
            dateNeeded = UtilityExt.toLocalDateTime(cord.getDateNeeded());
            if (cord.getOndate() == null)
                ondate = LocalDateTime.now();
            else {
                ondate = UtilityExt.toLocalDateTime(cord.getOndate());
            }
            quantity = cord.getQuantity();
            unitprice = cord.getUnitprice();
            location = cord.getLocation();
            notes = cord.getNotes();
            member = Long.valueOf(MezoDB.getItemID("tblmember", "item", cord.getMember())).intValue();
            if (cord.getStatus() != null)
                status = enOrderStatus.valueOf(cord.getStatus());
        } catch (Exception exp) {
            Utility.ShowError(exp);
        }
    }

    @Transactional
    public Client getClient() {
        if (clientid != null)
            if (clientid > 0) {
                Client c = new Client();
                c.find(clientid);
                return c;
            }
        return null;
    }

    @Transactional
    public String getClientName() {
        if (clientid != null) {
            Object v = MezoDB.getValue("select item from tblclient where id=" + clientid);
            if (v != null)
                return v.toString();
        }
        return "Not Set!";
    }

    public ClientOrder toClientOrder() {
        ClientOrder cord = new ClientOrder();
        try {
            cord.setId(id);
            cord.setClientid(getClient()!=null?getClient().getEmail():"None");
            cord.setMobile(getClient()!=null?getClient().getMobile():"None");
            cord.setItemid(String.valueOf(itemid));
            cord.setCountry(MezoDB.getItem(country, TblCountry.class.getSimpleName()) + " ");
            cord.setCity(MezoDB.getItem(city, TblCity.class.getSimpleName()));
            cord.setDateNeeded(dateNeeded.format(DateTimeFormatter.ISO_LOCAL_DATE));
            cord.setOndate(ondate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            cord.setLocation(location);
            cord.setNotes(notes);
            cord.setQuantity(quantity);
            cord.setGrade(MezoDB.getItem(type, " Tblgrade "));
            cord.setMember(MezoDB.getItem(member, " tblmember "));
            cord.setState(MezoDB.getItem(state, " Tblprovince "));
            cord.setStatus(status.name());
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return cord;
    }


}
