package com.rationalteam.rtreadymix.routes;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rtreadymix.ClientManager;
import com.rationalteam.rtreadymix.Order;
import com.rationalteam.rtreadymix.OrderStat;
import com.rationalteam.rtreadymix.SystemConfig;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.core.json.JsonObject;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("readymix")
public class CheckIn {

    @Inject
    @ResourcePath("checkin")
    Template checkin;

    @Inject
    @ResourcePath("adminspace")
    Template adminspace;

    @ResourcePath("rtbrowse")
    Template rtbrowse;
    @ResourcePath("services")
    Template services;

    @ResourcePath("news")
    Template news;
    @ResourcePath("suppliers")
    Template suppliers;

    @ResourcePath("clients")
    Template clients;
    @Inject
    ClientManager cman;
    OrderStat stat = new OrderStat();

    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
    @Path("/login")
    @PermitAll
    public TemplateInstance login() {
        JsonObject data = new io.vertx.core.json.JsonObject();
        data.put("error", "").put("message", "welcome to rationalteam");
        return checkin.instance();
    }

    @FormParam("_method")
    String command;

//    @POST
//    @PermitAll
//    @Produces({MediaType.TEXT_PLAIN,MediaType.TEXT_HTML})
//    @Path("checkin")
//    public TemplateInstance checkin() {
//        JsonObject data = new io.vertx.core.json.JsonObject();
//        String uname = username;
//        boolean isauth = false;//SystemConfig.USERMANAGER.login(username, pwd);
//        if (isauth)
//            return browseOrders();
//        else
//            return checkin.data("rtmessage", uname + ": You are not allowed to use this system.");
//
//    }

    @Path("/orders")
    @GET
    @RolesAllowed("admin")
    public TemplateInstance browseOrders() {
        List<Order> orderList = stat.getOrders();
        List<ClientOrder> col = orderList.stream().map(Order::toClientOrder).collect(Collectors.toList());
        return rtbrowse.data("isstaff", true)
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

    @GET
    @Path("/services")
    @RolesAllowed("admin")
    public TemplateInstance serviceManager() {
        return services.data("title", "Service Browser")
                .data("rtlist", stat.getServices());
    }

    @GET
    @Path("/suppliers")
    public TemplateInstance supplierManager() {
        return suppliers.data("title", "Suppliers Browser")
                .data("rtlist", stat.getSuppliers());
    }

    @GET
    @Path("/clients")
    public TemplateInstance clientsManager() {
        return clients.data("title", "Clients Browser")
                .data("rtlist", cman.getCLients());
    }

    @GET
    @Path("/news")
    public TemplateInstance newsManager() {
        return news.data("title", "News Browser")
                .data("rtlist", stat.getNews());
    }
}
