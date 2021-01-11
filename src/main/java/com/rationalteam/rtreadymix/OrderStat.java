package com.rationalteam.rtreadymix;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.reaymixcommon.News;
import com.rationalteam.rterp.erpcore.CService;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rtreadymix.data.Tblnews;
import com.rationalteam.rtreadymix.purchase.Supplier;
import io.quarkus.qute.TemplateData;

import javax.lang.model.element.NestingKind;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@TemplateData
public class OrderStat {
    ClientOrder order;

    @Transactional
    public List<Order> getOrders() {
        Order order = new Order();
        List<Order> orderList = order.listAll();
        orderList.sort((o1, o2) -> (o2.getId().compareTo(o1.getId())));
        return orderList;
    }

    @Transactional
    public List<CService> getServices() {
        List<CService> serviceList = new ArrayList();
        CService s = new CService();
        serviceList = s.listAllItems();
        return serviceList;
    }
    @Transactional
    public List<Supplier> getSuppliers() {
        List<Supplier> suplierlist = new ArrayList();
        Supplier s = new Supplier();
        suplierlist = s.listAllItems();
        return suplierlist;
    }
    @Transactional
    public List<News> getNews() {
        List<News> newslist = new ArrayList();
        List<Tblnews> newsdb = MezoDB.open("select * from tblnews", Tblnews.class);
        if (newsdb != null)
            newsdb.forEach(n -> {
                News news = new News(n.getItem(), n.getDetails());
                newslist.add(news);
            });
        return newslist;
    }
}
