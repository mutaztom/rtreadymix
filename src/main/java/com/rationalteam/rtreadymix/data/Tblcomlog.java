package com.rationalteam.rtreadymix.data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Tblcomlog {
    private Integer id;
    private String address;
    private String status;
    private Timestamp smstime;
    private String byuser;
    private String title;
    private String message;
    private String email;

    @Basic
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "address", nullable = true, length = -1)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "status", nullable = true, length = -1)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "smstime", nullable = true)
    public Timestamp getSmstime() {
        return smstime;
    }

    public void setSmstime(Timestamp smstime) {
        this.smstime = smstime;
    }

    @Basic
    @Column(name = "byuser", nullable = true, length = -1)
    public String getByuser() {
        return byuser;
    }

    public void setByuser(String byuser) {
        this.byuser = byuser;
    }

    @Basic
    @Column(name = "title", nullable = true, length = -1)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "message", nullable = true, length = -1)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Basic
    @Column(name = "email", nullable = true, length = -1)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblcomlog tblcomlog = (Tblcomlog) o;
        return Objects.equals(id, tblcomlog.id) &&
                Objects.equals(address, tblcomlog.address) &&
                Objects.equals(status, tblcomlog.status) &&
                Objects.equals(smstime, tblcomlog.smstime) &&
                Objects.equals(byuser, tblcomlog.byuser) &&
                Objects.equals(title, tblcomlog.title) &&
                Objects.equals(message, tblcomlog.message) &&
                Objects.equals(email, tblcomlog.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, status, smstime, byuser, title, message, email);
    }
}
