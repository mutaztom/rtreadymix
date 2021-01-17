package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rterp.erpcore.COption;
import com.rationalteam.rterp.erpcore.CProduct;
import com.rationalteam.rtreadymix.Rtutil;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("readymix")
public class ProductResource {
    @Inject
    Rtutil rtutil;
    @Inject
    @ResourcePath("product")
    Template template;
    @FormParam("item")
    String item;
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
    CProduct product;
    Integer itemid;

    private TemplateInstance initTemplate() {
        return template.data("title", "product Manager")
                .data("units", rtutil.fromTable("tblunits"))
                .data("member", rtutil.fromTable("tblmember"))
                .data("prodcat", rtutil.fromTable("tblprodcat"));
    }

    @POST
    @Path("saveproduct")
    @Transactional
    public TemplateInstance saveproduct() {
        TemplateInstance t = initTemplate();
        try {
            boolean r = false;
            if (command.equals("cmdsave")) {
                product.setItem(item);
                product.setMainCat(ddcat);
                product.setDescription(description);
                product.setUnitid(unit);
                product.setUnitPrice(unitprice);
                r = product.save();
            } else if (command.equals("cmddelete")) {
                return t.data("result", "Are you sure you want to delete");
            } else if (command.equals("cmdaddcat")) {
                r = newcat != null && !newcat.isBlank();
                if (r) {
                    COption cat = new COption("tblproductcat");
                    cat.setItem(newcat);
                    cat.setAritem(newcat);
                    r = cat.save();
                    return t.data("product", product).data("productcat", rtutil.fromTable("tblproductcat"))
                            .data("result", r ? "Data saved successfully" : "Error saving data");

                }
            }
            return t.data("product", product).data("result", r ? "Data saved successfully" : "Error saving data");
        } catch (Exception e) {
            Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
            return t.data("result", e.getMessage());
        }
    }

    @GET
    @Path("product")
    public TemplateInstance viewproduct(@QueryParam("itemid") Integer productid) {
        TemplateInstance t = initTemplate();
        product = new CProduct();
        this.itemid = productid;
        product.find(itemid);
        return t.data("product", product);
    }

}
