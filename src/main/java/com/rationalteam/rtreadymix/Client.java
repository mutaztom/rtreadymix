package com.rationalteam.rtreadymix;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rationalteam.core.security.enUserType;
import com.rationalteam.reaymixcommon.MobileUser;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rtreadymix.data.Tblclient;

import javax.json.bind.annotation.JsonbProperty;
import javax.swing.text.DateFormatter;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client extends CRtDataObject {
    @Browsable
    String username;
    @Browsable
    String email;
    @Browsable
    String mobile;
    String password;
    enUserType userType;
    @Browsable
    String address;
    Tblclient data;
    private Integer accountid;
    private Integer customerid;
    private String pincode;
    private boolean verfied;
    @Browsable(isDate = true)
    private LocalDate since;

    @Override
    protected void initSearch() {
        CSearchOption o = new CSearchOption("id", "id");
        o.setOptionTable("tblclients");
        addSearchOption(o);
    }

    @Override
    public Object getData() {
        data = new Tblclient();
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
        data.setVerified(verfied);
        data.setSince(Date.valueOf(since));
        return data;
    }

    @Override
    public void setData(Object o) {
        data = (Tblclient) o;
        id = data.getId();
        item = data.getItem();
        email = data.getEmail();
        mobile = data.getMobile();
        username = data.getUsername();
        customerid = data.getCustomerid();
        accountid = data.getAccountid();
        password = data.getPassword();
        address = data.getAddress();
        pincode = data.getPincode();
        verfied = data.getVerified() != null ? data.getVerified() : false;
        if (data.getSince() != null)
            since = data.getSince().toLocalDate();
    }

    public void fromMobileUser(MobileUser muser) {
        id = null;
        item = muser.getItem();
        email = muser.getEmail();
        mobile = muser.getMobile();
        username = muser.getUsername();
        customerid = -1;
        accountid = -1;
        password = muser.getPassword();
        address = muser.getAddress();
    }

    @Override
    public <T> Class<T> getDataType() {
        return (Class<T>) Tblclient.class;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        item = username;
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

    public boolean isMobileUsed() {
        if (mobile == null || mobile.isEmpty())
            return false;
        String mob = mobile;
        int used = MezoDB.getInteger("select count(mobile) from Tblclient where mobile='" + mob + "'");
        return used > 0;
    }

    public boolean isEmailUsed() {
        if (email == null || email.isEmpty())
            return false;
        String mob = email;
        int used = MezoDB.getInteger("select count(email) from Tblclient where email='" + mob + "'");
        return used > 0;
    }

    public boolean isNameUsed() {
        if (username == null || username.isEmpty())
            return false;
        String mob = username;
        int used = MezoDB.getInteger("select count(username) from Tblclient where username='" + mob + "'");
        return used > 0;
    }

    public boolean isRegistered() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        List<Object> filter = getFacade().filter(map);
        return !filter.isEmpty();
    }

    @Transactional
    public static Client findByEmail(String findemail) {
        try {
            Map<Integer, Object> map = new HashMap<>();
            map.put(1, findemail);
            List<Tblclient> list = MezoDB.openNative("select * from tblclient where email=?", Tblclient.class, map);
            if (!list.isEmpty()) {
                Tblclient dat = list.get(0);
                Client c = new Client();
                c.setData(dat);
                return c;
            }
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return null;
    }

    @Override
    public boolean checkEntries() throws ValidationException {
        if (isEmailUsed())
            throw new ValidationException("Email is already registered in our database.");
        if (isMobileUsed())
            throw new ValidationException("Mobile phone number is already registered.");
        if (isNameUsed())
            throw new ValidationException("Username already registered.");
        return true;
    }

    public boolean isVerfied() {
        return verfied;
    }

    public void setVerfied(boolean verfied) {
        this.verfied = verfied;
    }

    public LocalDate getSince() {
        return since;
    }

    public void setSince(LocalDate since) {
        this.since = since;
    }

    @Override
    public Map<String, Object> getAsRecord() {
        Map<String, Object> map = new HashMap<>();
        map.put("Id", id);
        map.put("Name", username);
        map.put("Item", item);
        map.put("Mobile", mobile);
        map.put("Email", email);
        map.put("Country", "Sudan");
        map.put("City", "Khartoum");
        map.put("Address", address);
        map.put("Accountid", this.accountid);
        map.put("Customerid", customerid);
        map.put("Since", since != null ? since.format(DateTimeFormatter.ISO_LOCAL_DATE) : "None");
        map.put("Verified", verfied);
        return map;
    }
}
