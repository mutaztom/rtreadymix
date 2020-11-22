package com.rationalteam.rtreadymix;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rationalteam.core.security.enUserType;
import com.rationalteam.rterp.erpcore.CRtDataObject;
import com.rationalteam.rterp.erpcore.CSearchOption;
import com.rationalteam.rtreadymix.data.Tblclients;

@JsonSerialize
public class Client extends CRtDataObject {
    String username;
    String email;
    String mobile;
    String password;
    enUserType userType;
    String address;
    Tblclients data;
    private Integer accountid;
    private Integer customerid;
    private String pincode;

    @Override
    protected void initSearch() {
        CSearchOption o = new CSearchOption("id", "id");
        o.setOptionTable("tblclients");
        addSearchOption(o);
    }

    @Override
    public Object getData() {
        data = new Tblclients();
        data.setId(id);
        data.setItem(item);
        data.setUsername(username);
        data.setCustomerid(customerid);
        data.setAccountid(accountid);
        data.setEmail(email);
        data.setMobile(mobile);
        if (userType == null)
            userType = enUserType.Guest;
        data.setUsertype(userType.name());
        data.setAddress(address);
        data.setPassword(password);
        data.setPincode(pincode);
        return data;
    }

    @Override
    public void setData(Object o) {
        data = (Tblclients) o;
        id = data.getId();
        item = data.getItem();
        email = data.getEmail();
        mobile = data.getMobile();
        username = data.getUsername();
        customerid = data.getCustomerid();
        accountid = data.getAccountid();
        password=data.getPassword();
        address=data.getAddress();
        pincode =data.getPincode();
    }

    @Override
    public <T> Class<T> getDataType() {
        return (Class<T>) Tblclients.class;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public enUserType getUserType() {
        return userType;
    }

    public void setUserType(enUserType userType) {
        this.userType = userType;
    }

    public Integer getAccountid() {
        return accountid;
    }

    public void setAccountid(Integer accountid) {
        this.accountid = accountid;
    }

    public Integer getCustomerid() {
        return customerid;
    }

    public void setCustomerid(Integer customerid) {
        this.customerid = customerid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
