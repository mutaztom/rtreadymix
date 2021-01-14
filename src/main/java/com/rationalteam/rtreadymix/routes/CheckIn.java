package com.rationalteam.rtreadymix.routes;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rtreadymix.ClientManager;
import com.rationalteam.rtreadymix.Order;
import com.rationalteam.rtreadymix.OrderStat;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.core.json.JsonObject;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("readymix")
public class CheckIn {

       @Inject
    @ResourcePath("adminspace")
    Template adminspace;

    @ResourcePath("orders")
    Template orders;


    @Inject
    ClientManager cman;
    OrderStat stat = new OrderStat();

    @Path("/orders")
    @GET
    @RolesAllowed("admin")
    public TemplateInstance browseOrders() {
        List<Order> orderList = stat.getOrders();
        List<ClientOrder> col = orderList.stream().map(Order::toClientOrder).collect(Collectors.toList());
        return orders.data("isstaff", true)
                .data("title", "Browse Orders")
                .data("rtlist", col)
                .data("clientid", "Admin")
                .data("clients", cman.getCLients());
    }

    @GET
    @Path("/dashboard")
    public TemplateInstance dashboard() {
        return adminspace.data("curruser", "Amdmin")
                .data("title","Admin Dashboard")
                .data("clnt", "Mutaz")
                .data("orderstat", stat.getOrdersByStatus())
                .data("clientstat", stat.getClientsByStatus());
    }

}
