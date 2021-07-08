package com.rationalteam.rtreadymix.routes;


import com.rationalteam.rtreadymix.Client;
import com.rationalteam.rtreadymix.ClientManager;
import com.rationalteam.rtreadymix.CommHub;
import com.rationalteam.rtreadymix.Order;
import com.rationalteam.rtreadymix.data.Tblclient;
import com.rationalteam.rtreadymix.data.Tblusers;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.core.json.JsonObject;
import org.checkerframework.common.reflection.qual.GetClass;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

@Path("/readymix")
public class ClientResource {
    @Inject
    @ResourcePath("client")
    Template template;
    @Inject
    AdminResource ares;
    @ResourcePath("clientmanager")
    Template clientmanTemplate;
    String message;
    @Inject
    CommHub commHub;
    private String findwhat;
    private List<Client> clients;
    Client client;
    List<Tblclient> pending;

    public ClientResource() {
        client = new Client();
        clients = client.listAll();
        pending = Tblclient.find("verified", false).list();
    }

    @Path("/client")
    @GET
    @RolesAllowed("admin")
    public TemplateInstance showClient(@QueryParam("itemid") Integer itemid) {
        Client client = new Client();
        client.find(itemid);
//        JsonObject jclient = JsonObject.mapFrom(client);
        return template.data("client", client).data("title", "Client Manager")
                .data("message", message);
    }

    @Path("/clientorder")
    @POST
    public TemplateInstance showOrder(@FormParam("orderid") int orderid) {
        ares.itemid = orderid;
        ares.command = "view";
        return ares.manageOrder(orderid);
    }

    @Path("clientman")
    @RolesAllowed("admin")
    @GET
    public TemplateInstance clientMan() {
        List<Client> filteredClients=clients;
        if(findwhat!=null && !findwhat.isEmpty())
            filteredClients= clients.stream().filter(f -> f.getItem().contains(findwhat)).collect(Collectors.toList());
        return clientmanTemplate.data("title", "Client Manager")
                .data("pending", pending)
                .data("message", message)
                .data("findwhat", findwhat)
                .data("rtlist", filteredClients)
                .data("filteraction", "filterClient");
    }

    @Path("/filterClient")
    @POST
    @RolesAllowed("admin")
    public Response filter(@FormParam("command") String command,
                       @FormParam("findwhat") String fwhat) {
        if (command == null) {
            message="No command to process";
            return Response.seeOther(URI.create("/readymix/clientman")).build();
        }
        if (command.equals("search") && fwhat != null && !fwhat.isBlank()) {
            {
                findwhat = fwhat;
            }
        } else if (command.equals("clear")) {
            findwhat = null;

        }
        return Response.seeOther(URI.create("/readymix/clientman")).build();
    }

    @Path("adminactions")
    @RolesAllowed("admin")
    @POST
    @Transactional
    public Response adminActions(@FormParam("command") String command,@Context SecurityContext securityContext) {
        try {
            if (command == null) {
                message = "No command was selected";
                System.out.println(message);
                return Response.seeOther(URI.create("/readymix/clientman")).build();
            }
            int itemid = Integer.parseInt(command.split("_")[1]);

            if (command.startsWith("sendPin")) {
                Tblclient c = Tblclient.findById(itemid);
                if (c.getMobile() == null) {
                    System.out.println(message);
                    message = "error: Mobile number not found to send SMS to.";
                    return Response.seeOther(URI.create("/readymix/clientman")).build();
                }
                if (c.getPincode() == null) {
                    ClientManager cman = new ClientManager();
                    Client client = new Client();
                    client.setData(c);
                    String pin = cman.generatePin(client);
                    c.setPincode(pin);
                }
                boolean r=commHub.sendSMS(c.getMobile(), c.getPincode());
                if(r) {
                    pending = Tblclient.find("verified", false).list();
                    message=">>>Response message from SMS server is OK";
                }
            } else if (command.startsWith("setVerified")) {
                Tblusers user = Tblusers.find("username", securityContext.getUserPrincipal().getName()).singleResult();
                int update = Tblclient.update("customerid=?2 ,verified =true where id= ?1", itemid,user.getId());
                if (update > 0) {
                    message = "Client status set to 'Verified' user may now login via his mobile.";
                    pending = Tblclient.find("verified", false).list();
                }
            }else if(command.startsWith("delete")){
                Tblclient.delete("id=?1",itemid);
                clients=client.listAll();
            }
        } catch (Exception e) {
            message = e.getMessage();
            System.out.println(e.getMessage());
            return Response.seeOther(URI.create("/readymix/clientman")).build();
        }
        return Response.seeOther(URI.create("/readymix/clientman")).build();
    }
}
