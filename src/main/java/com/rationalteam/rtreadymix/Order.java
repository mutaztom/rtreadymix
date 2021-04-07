package com.rationalteam.rtreadymix;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rterp.erpcore.data.TblCity;
import com.rationalteam.rterp.erpcore.data.TblCountry;
import com.rationalteam.rterp.erpcore.data.TblProduct;
import com.rationalteam.rtreadymix.data.Tblclient;
import com.rationalteam.rtreadymix.data.Tblorder;
import okhttp3.internal.connection.Exchange;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Order extends CRtDataObject {
    Integer clientid;
    @Browsable
    String location;
    Integer country;
    Integer state;
    Integer city;
    @Browsable
    Double quantity;
    @Browsable
    String notes;
    Integer itemid;
    Integer member;
    //the date of the order
    @Browsable(isDate = true)
    LocalDateTime ondate;
    //the date on which the order to be delivered
    @Browsable(isDate = true)
    LocalDateTime dateNeeded;
    enOrderStatus status;
    @Browsable
    Double unitprice;
    @Browsable
    Double rate;
    Double usdprice;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    Tblorder data;
    private Integer province;
    CExchange exchange = new CExchange();
    private LocalTime ontime;

    public Order() {
        status = enOrderStatus.Created;
        unitprice = 0D;
        usdprice = 0D;
        ondate = LocalDateTime.now();
        //rate get last rate
        rate = SystemConfig.getRate();
        //default to one day delay
        dateNeeded = LocalDateTime.now().plusDays(1);
        setDbTable(Tblorder.class.getSimpleName());
        ontime = LocalTime.now();
    }


    protected void initSearch() {
        super.initSearch();
        searchOptions.forEach(o -> o.setOptionTable("tbloptions"));
    }


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
            data.setLocation(location);
            data.setRate(rate);
            data.setOntime(Time.valueOf(ontime));
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return data;
    }


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
            member = data.getMember();
            location = data.getLocation();
            rate = data.getRate() > 0 ? data.getRate() : rate;
            ontime = data.getOntime().toLocalTime();
        } catch (IllegalArgumentException e) {
            Utility.ShowError(e);
        }
    }


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

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }


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
                Objects.equals(notes, order.notes) &&
                Objects.equals(itemid, order.itemid) &&
                Objects.equals(ondate, order.ondate) &&
                Objects.equals(dateNeeded, order.dateNeeded) &&
                Objects.equals(status, order.status) &&
                Objects.equals(data, order.data);
    }


    public int hashCode() {
        return Objects.hash(super.hashCode(), clientid, location, country, state, city, quantity, notes, itemid, ondate, dateNeeded, status, data);
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
            itemid = Long.valueOf(MezoDB.getItemID(TblProduct.class.getSimpleName(), "item", cord.getGrade())).intValue();
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
            item = notes + " volume of " + quantity + " q.m " + "from " + getClientName();
            if (cord.getStatus() != null)
                status = enOrderStatus.valueOf(cord.getStatus());
        } catch (Exception exp) {
            Utility.ShowError(exp);
        }
    }

    @Transactional
    public Client getClient() {
        Client c = new Client();
        if (clientid != null)
            if (clientid > 0) {
                c.find(clientid);
                return c;
            }
        return c;
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
            cord.setClientid(getClient() != null ? getClient().getEmail() : "None");
            cord.setMobile(getClient() != null ? getClient().getMobile() : "None");
            cord.setItemid(String.valueOf(itemid));
            cord.setCountry(MezoDB.getItem(country, TblCountry.class.getSimpleName()) + " ");
            cord.setCity(MezoDB.getItem(city, TblCity.class.getSimpleName()));
            cord.setDateNeeded(dateNeeded.format(DateTimeFormatter.ISO_LOCAL_DATE));
            cord.setOndate(ondate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            cord.setLocation(location);
            cord.setNotes(notes);
            cord.setUnitprice(unitprice);
            cord.setUsdprice(usdprice);
            cord.setQuantity(quantity);
            cord.setGrade(MezoDB.getItem(itemid, " Tblgrade "));
            cord.setMember(MezoDB.getItem(member, " tblmember "));
            cord.setState(MezoDB.getItem(state, " Tblprovince "));
            cord.setStatus(status.name());
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return cord;
    }


    public Map<String, Object> getAsRecord() {
        Map<String, Object> map = new HashMap<>();
        try {
            getBrowsableFields().forEach(f -> {
                try {
                    map.put(f.getName(), f.get(this));
                } catch (IllegalAccessException exp) {
                    Utility.ShowError(exp);
                }
            });
        } catch (Exception exp) {
            Utility.ShowError(exp);
        }
        return map;
    }

    public void updatePrice() {
        try {
            if (itemid == null || itemid <= 0)
                return;
            Map<Integer, Object> param = new HashMap<>();
            param.put(1, itemid);
            Object v = MezoDB.getValue("select unitprice from tblproduct where id=?", param);
            unitprice = (v == null ? 0D : Double.parseDouble(v.toString()));
        } catch (Exception e) {
            Utility.ShowError(e);
        }
    }

    public Double getTotal() {
        if ((itemid != null && itemid > 0) && unitprice == 0) updatePrice();
        return unitprice * quantity;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getEquiv() {
        Double equiv = exchange.convert(SystemConfig.getCompCurrency(), getTotal());
        return equiv;
    }

    public static boolean isComplete(ClientOrder ord) {
        boolean r = false;
        try {
            r = ord.getItemid() != null ||
                    (ord.getMember() == null) ||
                    ord.getQuantity() == null ||
                    ord.getQuantity() <= 0 ||
                    (ord.getClientid() == null);
            r = !r;
            if (Utility.IsNumber(ord.getItemid())) {
                r = r && (Integer.parseInt(ord.getItemid()) > 0);
            }
            if (Utility.IsNumber(ord.getGrade()))
                r = r && (Integer.parseInt(ord.getGrade()) > 0);
            if (Utility.IsNumber(ord.getMember()))
                r = r && (Integer.parseInt(ord.getMember()) > 0);
            return r;
        } catch (Exception exp) {
            Utility.ShowError(exp);
            return false;
        }
    }

    public LocalTime getOntime() {
        return ontime;
    }

    public void setOntime(LocalTime ontime) {
        this.ontime = ontime;
    }
}
