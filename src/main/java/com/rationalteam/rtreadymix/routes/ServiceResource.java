package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rterp.erpcore.COption;
import com.rationalteam.rterp.erpcore.CService;
import com.rationalteam.rterp.erpcore.Specification;
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
    @FormParam("newcat")
    String newcat;
    @FormParam("ddcat")
    Integer ddcat;
    @FormParam("command")
    String command;
    @FormParam("description")
    String description;
    @FormParam("unit")
    Integer unit;
    @FormParam("unitprice")
    Double unitprice;
    CService service;
    Integer itemid;

    private TemplateInstance initTemplate() {
        return template.data("title", "Service Manager")
                .data("units", rtutil.fromTable("tblunits"))
                .data("grades", rtutil.fromTable("tblgrade"))
                .data("member", rtutil.fromTable("tblmember"))
                .data("servicecat", rtutil.fromTable("tblservicecat"));
    }

    @POST
    @Path("saveservice")
    @Transactional
    public TemplateInstance saveService() {
        TemplateInstance t = initTemplate();
        try {
            boolean r = false;
            if (command.equals("cmdsave")) {
                service.setItem(item);
                service.setMainCat(ddcat);
                service.setDescription(description);
                service.setUnitid(unit);
                service.setUnitPrice(unitprice);
                service.setDatasheet(aritem);
                r = service.save();
            } else if (command.equals("cmddelete")) {
                return t.data("result", "Are you sure you want to delete");
            } else if (command.equals("cmdaddcat")) {
                r = newcat != null && !newcat.isBlank();
                if (r) {
                    COption cat = new COption("tblservicecat");
                    cat.setItem(newcat);
                    cat.setAritem(newcat);
                    r = cat.save();
                    return t.data("service", service).data("servicecat", rtutil.fromTable("tblservicecat"))
                            .data("result", r ? "Data saved successfully" : "Error saving data");

                }
            }
            return t.data("service", service).data("result", r ? "Data saved successfully" : "Error saving data");
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
