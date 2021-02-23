package com.rationalteam.rtreadymix.purchase;

import com.rationalteam.rterp.erpcore.CExchange;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.common.constraint.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tblpricelist")
public class PriceList extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String item;
    public Double price;
    public Double commission = 0D;
    public Double minorder = 1D;
    public Integer supplierid;
    public Integer itemid;
    public LocalDateTime ondate = LocalDateTime.now();
    public String byuser;
    public Double eqprice;
    @NotNull
    public boolean ispercent;


    public static List<PriceList> getPriceList(int forsupplier) {
        return find("supplierid=?1", forsupplier).list();
    }

}
