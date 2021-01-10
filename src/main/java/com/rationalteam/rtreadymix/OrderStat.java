package com.rationalteam.rtreadymix;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rtreadymix.data.Tblorder;
import io.quarkus.qute.TemplateData;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TemplateData
public class OrderStat {
    ClientOrder order;

    @Transactional
    public List<Order> getOrders() {
//        List<Order> orderList = new ArrayList<>();
//        List<Tblorder> open = MezoDB.open("select * from tblorder", Tblorder.class);
//        if (open != null)
//            open.forEach(o -> {
//                Order order = new Order();
//                order.setData(o);
//                orderList.add(order);
//            });
        Order order = new Order();
        List orderList = order.listAll();
        return orderList;
    }

}
