package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rterp.erpcore.CExchange;
import com.rationalteam.rterp.erpcore.COption;
import com.rationalteam.rterp.erpcore.CProduct;
import com.rationalteam.rterp.erpcore.CRtDataObject;
import com.rationalteam.rtreadymix.Rtutil;
import com.rationalteam.rtreadymix.UtilityExt;
import com.rationalteam.rtreadymix.purchase.PriceList;
import com.rationalteam.rtreadymix.purchase.Supplier;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.reactivex.ext.web.templ.TemplateEngine;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

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
    @FormParam("command")
    String command;

    private TemplateInstance initTemplate() {
        return supplierTemplate.data("title", "Supplier Manager")
                .data("cities", rtutil.fromTable("tblcity"))
                .data("countries", rtutil.fromTable("tblcountry"));
    }

    @POST
    @Path("savesupplier")
    @Transactional
    public TemplateInstance saveSupplier() {
        TemplateInstance t = initTemplate();
        try {
            boolean r = false;
            if (command.equals("cmdsave")) {
                supplier = itemid > 0 ? supplier : new Supplier();
                supplier.setItem(item);
                supplier.setAritem(aritem);
                supplier.setAddress(address);
                supplier.setLocation(location);
                supplier.setCountry(country);
                supplier.setCity(city);
                supplier.setEmail(email);
                supplier.setPhone(mobile);
                supplier.setActive(true);
                r = supplier.save();
            } else if (command.equals("cmddelete")) {
                return t.data("result", "Are you sure you want to delete");
            } else if (command.equals("createplist")) {
                List<PriceList> pl = createPriceList();
                //put price list back on form
                t.data("pricelist", pl);
            }
            if(itemid>0)t=t.data("pricelist",PriceList.getPriceList(itemid));
            return t.data("supplier", supplier).data("result", r ? "Success" : "Error saving supplier");

        } catch (Exception e) {
            Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),"error: "+ e.getMessage()).build();
            return t.data("result", e.getMessage());
        }
    }

    private List<PriceList> createPriceList() {
        List<PriceList> list = new ArrayList<>();
        CProduct prod = new CProduct();
        List<CProduct> products = prod.listAll();
        products.forEach(p -> {
            PriceList plist = new PriceList();
            plist.supplierid = itemid;
            plist.itemid = p.getId();
            plist.price = p.getUnitPrice();
            plist.eqprice = p.getEqprice();
            plist.byuser = "admin";
            plist.item = p.getItem() + " from " + supplier.getName();
            plist.persist();
            list.add(plist);
        });
        return list;
    }

    @GET
    @Path("supplier")
    public TemplateInstance viewSupplier(@QueryParam("itemid") Integer supplierid) {
        TemplateInstance t = initTemplate();
        supplier = new Supplier();
        this.itemid = supplierid;
        supplier.find(itemid);
        return t.data("supplier", supplier).data("pricelist", PriceList.getPriceList(itemid));
    }
}
