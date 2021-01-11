package com.rationalteam.rtreadymix.data;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(catalog = "rtmix")
public class Tblclient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String username;
    String item;
    String address;
    String password;
    Integer customerid;
    Integer accountid;
    String email;
    String mobile;
    String usertype;
    String pincode;
    Boolean verified;
    Date since;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCustomerid() {
        return customerid;
    }

    public void setCustomerid(Integer customerid) {
        this.customerid = customerid;
    }

    public Integer getAccountid() {
        return accountid;
    }

    public void setAccountid(Integer accountid) {
        this.accountid = accountid;
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

    public Date getSince() {
        return since;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tblclient)) return false;
        Tblclient that = (Tblclient) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username) &&
                Objects.equals(item, that.item) &&
                Objects.equals(password, that.password) &&
                Objects.equals(customerid, that.customerid) &&
                Objects.equals(accountid, that.accountid) &&
                Objects.equals(email, that.email) &&
                Objects.equals(mobile, that.mobile) &&
                Objects.equals(usertype, that.usertype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, item, password, customerid, accountid, email, mobile, usertype);
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
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

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
