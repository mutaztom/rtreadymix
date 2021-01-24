package com.rationalteam.rtreadymix;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.reaymixcommon.News;
import com.rationalteam.rterp.erpcore.CProduct;
import com.rationalteam.rterp.erpcore.CService;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rtreadymix.data.Tblnews;
import com.rationalteam.rtreadymix.data.Tblorder;
import com.rationalteam.rtreadymix.purchase.Supplier;
import io.quarkus.qute.TemplateData;

import javax.lang.model.element.NestingKind;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        serviceList = s.listAll();
        return serviceList;
    }

    @Transactional
    public List<CProduct> getProducts() {
        List<CProduct> prodlist = null;
        try {
            prodlist = new ArrayList();
            CProduct s = new CProduct();
            prodlist = s.listAll();
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return prodlist;
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
//                news.setId(n.getId());
                newslist.add(news);
            });
        return newslist;
    }

    public Map<String, Integer> getOrdersByStatus() {
        Map<String, Integer> stat = new HashMap<>();
        List open = MezoDB.Open("select count(id),status from tblorder group by status");
        if (open != null)

            for (Object o :
                    open) {
                Object[] rec = (Object[]) o;
                stat.put(rec[1].toString(), Integer.valueOf(rec[0].toString()));
            }
        return stat;
    }

    public Map<String, Integer> getClientsByStatus() {
        Map<String, Integer> stat = new HashMap<>();
        List open = MezoDB.Open("select count(id) as stat,datediff(curdate(),since) bydate from tblclient group by datediff(curdate(),since) order by datediff(curdate(),since) desc ;");
        if (open != null)
            for (Object o :
                    open) {
                Object[] rec = (Object[]) o;
                stat.put(rec[0].toString(), Integer.valueOf(rec[1].toString()));
            }
        return stat;
    }
}
