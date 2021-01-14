package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rterp.erpcore.COption;
import com.rationalteam.rtreadymix.Rtutil;
import com.rationalteam.rtreadymix.UtilityExt;
import com.rationalteam.rtreadymix.purchase.Supplier;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static com.rationalteam.utility.enSearchAttrib.City;

@Path("readymix")
public class SupplierResource {
    @Inject
    Rtutil rtutil;
    @Inject
    @ResourcePath("supplier")
    Template supplierTemplate;
    @FormParam("item")
    String item;
    @FormParam("country")
    Integer country;
    @FormParam("city")
    Integer city;
    @FormParam("address")
    String address;
    @FormParam("email")
    String email;
    @FormParam("mobile")
    String mobile;
    @FormParam("person")
    String person;
    @FormParam("location")
    String location;
    @FormParam("aritem")
    String aritem;
    Supplier supplier;
    Integer itemid;

    private TemplateInstance initTemplate() {
        return supplierTemplate.data("title", "Supplier Manager")
                .data("cities", rtutil.fromOptions("tblcity"))
                .data("countries", rtutil.fromOptions("tblcountry"));
    }

    @POST
    @Path("savesupplier")
    public TemplateInstance saveSupplier() {
        try {
            TemplateInstance t = initTemplate();
            supplier = itemid > 0 ? supplier : new Supplier();
            supplier.setItem(item);
            supplier.setAddress(address);
            supplier.setLocation(location);
            supplier.setCountry(country);
            supplier.setCity(city);
            supplier.setActive(true);
            supplier.setAritem(aritem);
            boolean r = supplier.save();
            return supplierTemplate.data("supplier", supplier).data("Result", r ? "Success" : "Error saving supplier");
        } catch (Exception e) {
            Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
            return supplierTemplate.data("result", e.getMessage());
        }
    }

    @GET
    @Path("supplier")
    public TemplateInstance viewSupplier(@QueryParam("itemid") Integer supplierid) {
        supplier = new Supplier();
        this.itemid = supplierid;
        supplier.find(itemid);
        return supplierTemplate.data("supplier", supplier).data("title", "Supplier Manager")
                .data("cities", new String[]{"Khartoum", "Shandi", "Madani", "Kusti"});
    }
}
