package com.rationalteam.rtreadymix.data;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tblusers")
@UserDefinition
public class Tblusers extends PanacheEntityBase {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String usertype;
    private String roles;

    @Basic
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "username", nullable = false, length = 255,unique = true)
    @Username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password", nullable = false, length = 255)
    @Password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "roles", nullable = false, length = 255)
    @Roles
    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Basic
    @Column(name = "email", nullable = false, length = 255)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "phone", nullable = false, length = 255)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "address", nullable = true, length = 255)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "usertype", nullable = true, length = 255)
    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblusers tblusers = (Tblusers) o;
        return id == tblusers.id &&
                Objects.equals(username, tblusers.username) &&
                Objects.equals(password, tblusers.password) &&
                Objects.equals(email, tblusers.email) &&
                Objects.equals(phone, tblusers.phone) &&
                Objects.equals(address, tblusers.address) &&
                Objects.equals(usertype, tblusers.usertype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, email, phone, address, usertype);
    }

}
