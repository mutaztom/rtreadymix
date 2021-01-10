package com.rationalteam.rtreadymix.routes;

import com.rationalteam.core.CUmanager;
import com.rationalteam.core.CUser;
import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rtreadymix.ClientManager;
import com.rationalteam.rtreadymix.Order;
import com.rationalteam.rtreadymix.OrderStat;
import com.rationalteam.rtreadymix.SystemConfig;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.resteasy.runtime.standalone.VertxHttpRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.multipart.MultipartForm;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("checkin")
public class CheckIn {
    @Inject
    Template checkin;

    @Inject
    @ResourcePath("adminspace")
    Template adminspace;

    @ResourcePath("rtbrowse")
    Template rtbrowse;
    @Inject
    ClientManager cman;

    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
    public TemplateInstance get() {
        JsonObject data = new io.vertx.core.json.JsonObject();
        data.put("error", "").put("message", "welcome to rationalteam");
        return checkin.data("rtmessage", "welcome to rationalteam");
    }

    @FormParam("username")
    String username;
    @FormParam("password")
    String pwd;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public TemplateInstance login() {
        JsonObject data = new io.vertx.core.json.JsonObject();
        String uname = username;
        boolean isauth = SystemConfig.USERMANAGER.login(username, pwd);
        if (isauth)
            return browseOrders();
        else
            return checkin.data("rtmessage", uname + ": You are not allowed to use this system.");

    }

    private TemplateInstance browseOrders() {
        OrderStat stat = new OrderStat();
        List<Order> orderList = stat.getOrders();
        List<ClientOrder> col = orderList.stream().map(Order::toClientOrder).collect(Collectors.toList());
        return rtbrowse.data("isstaff", true)
                .data("rtlist", col)
                .data("clientid", "None")
                .data("clients", cman.getCLients());
    }

    @GET
    @Path("/dashboard")
    public TemplateInstance dashboard() {
        return adminspace.data("curruser", username)
                .data("clnt", "Mutaz")
                .data("OrderStat", "No thing to show");
    }
}
