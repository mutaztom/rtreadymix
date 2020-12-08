package com.rationalteam.rtreadymix;

public class ClientOrder {
    private String clientid;
    private String location;
    private String country;
    private String state;
    private String city;
    private Double quantity;
    private String type;
    private String notes;
    private String itemid;
    //the date of the order
    private String ondate;
    //the date on which the order to be delivered
    private String dateNeeded;
    private Double unitprice;
    private Double usdprice;
    private String province;
    private String member;
    private Integer id;
    private String status;

    public ClientOrder() {
        unitprice = 0D;
        usdprice = 0D;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getOndate() {
        return ondate;
    }

    public void setOndate(String ondate) {
        this.ondate = ondate;
    }

    public String getDateNeeded() {
        return dateNeeded;
    }

    public void setDateNeeded(String dateNeeded) {
        this.dateNeeded = dateNeeded;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
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

    public void setMember(String member) {
        this.member=member;
    }

    public String getMember() {
        return member;
    }

    public void setId(Integer id) {
        this.id=id;
    }

    public Integer getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status=status;
    }

    public String getStatus() {
        return status;
    }
}
