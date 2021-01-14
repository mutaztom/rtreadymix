package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rterp.erpcore.CService;
import com.rationalteam.rtreadymix.Rtutil;
import com.rationalteam.rtreadymix.purchase.Supplier;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("readymix")
public class ServiceResource {
    @Inject
    Rtutil rtutil;
    @Inject
    @ResourcePath("service")
    Template template;
    @FormParam("item")
    String item;
    @FormParam("aritem")
    String aritem;

    @FormParam("command")
    String command;
    CService service;
    Integer itemid;

    private TemplateInstance initTemplate() {
        return template.data("title", "Service Manager")
                .data("units", rtutil.fromTable("tblunits"))
                .data("grades", rtutil.fromTable("tblgrade"))
                .data("member", rtutil.fromTable("tblmember"));
    }

    @POST
    @Path("saveservice")
    @Transactional
    public TemplateInstance saveService() {
        TemplateInstance t = initTemplate();
        try {
            boolean r = false;
            if (command.equals("cmdsave")) {
//                r = supplier.save();
            } else if (command.equals("cmddelete")) {
                return t.data("result", "Are you sure you want to delete");
            }
            return t.data("service", service).data("result", r ? "Success" : "Error saving supplier");
        } catch (Exception e) {
            Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
            return t.data("result", e.getMessage());
        }
    }

    @GET
    @Path("service")
    public TemplateInstance viewService(@QueryParam("itemid") Integer serviceid) {
        TemplateInstance t = initTemplate();
        service = new CService();
        this.itemid = serviceid;
        service.find(itemid);
        return t.data("service", service);
    }
}
