package com.rationalteam.rtreadymix.routes;


import com.rationalteam.rtreadymix.Client;
import com.rationalteam.rtreadymix.Order;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.core.json.JsonObject;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/readymix")
public class ClientResource {
    @Inject
    @ResourcePath("client")
    Template template;
    @Inject
    AdminResource ares;

    @Path("/client")
    @GET
    @RolesAllowed("admin")
    public TemplateInstance showClient(@QueryParam("itemid") Integer itemid) {
        Client client = new Client();
        client.find(itemid);
//        JsonObject jclient = JsonObject.mapFrom(client);
        return template.data("client", client).data("title","Client Manager");
    }

    @Path("/clientorder")
    @POST
    public TemplateInstance showOrder(@FormParam("orderid") int orderid) {
        ares.itemid = orderid;
        ares.command = "view";
        return ares.manageOrder(orderid);
    }
}
