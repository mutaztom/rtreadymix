package com.rationalteam.rtreadymix.routes;


import com.rationalteam.rtreadymix.Client;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.security.Authenticated;
import io.quarkus.vertx.http.runtime.devmode.Json;
import io.vertx.core.json.JsonObject;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/redaymix")
public class ClientResource {
    @Inject
    @ResourcePath("client")
    Template template;


    @Path("/client")
    @RolesAllowed("admin")
    public TemplateInstance showClient(@QueryParam("itemid") Integer itemid) {
        Client client = new Client();
        client.find(itemid);
        JsonObject jclient = JsonObject.mapFrom(client);
        return template.data("client", jclient);
    }
}
