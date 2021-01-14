package com.rationalteam.rtreadymix.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rationalteam.rterp.erpcore.CRtDataObject;
import com.rationalteam.rterp.erpcore.CService;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rtreadymix.*;
import com.rationalteam.rtreadymix.purchase.Supplier;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("readymix")
public class RtbrowseResource {
    @Inject
    @ResourcePath("rtbrowse")
    Template template;
    @Inject
    @ResourcePath("client")
    Template clientTemplate;
    @Inject
    ClientManager cman;
    @FormParam("_method")
    String command;
    @FormParam("itemid")
    Integer itemid;

    String title;
    List<String> columns = new ArrayList<>();
    String rtype="";

    @Path("rtbrowse")
    @GET
    public TemplateInstance browse(@QueryParam("type") String type) {
        title = "Browse " + type;
        rtype = type;
        popColumns(type);
        TemplateInstance t = popdata(type);
        return t.data("title", title).data("columns", columns).data("type", type);
    }

    private TemplateInstance popdata(String type) {
        TemplateInstance t = template.instance();
        List<Map<String, Object>> rtlist;
        try {
            List<Map<String, Object>> jar = new ArrayList<>();
            switch (type) {
                case "client":
                    Client client = new Client();
                    List<Client> clist = client.listAll();
                    rtlist = new ArrayList<>();
                    clist.forEach(c -> {
                        Map<String, Object> map = new HashMap<>();
                        c.getAsRecord().forEach((k, v) -> {
                            if (columns.contains(k.toLowerCase()))
                                map.put(k.toLowerCase(), v);
                        });
                        rtlist.add(map);
                    });
                    t = template.data("rtlist", rtlist);
                    break;
                case "service":
                    CService service = new CService();
                    rtlist = new ArrayList<>();
                    generateListMap(rtlist, service.listAll(), service);
                    t = template.data("rtlist", rtlist);
                    break;
                case "news":
                    OrderStat stat = new OrderStat();
                    stat.getNews().forEach(jn -> {
                        Map<String, Object> map = null;
                        jar.add(UtilityExt.jsonToMap(jn));
                    });
                    t = template.data("rtlist", jar);
                    break;
                case "supplier":
                    Supplier supplier = new Supplier();
                    rtlist = new ArrayList<>();
                    generateListMap(rtlist, supplier.listAll(), supplier);
                    t = template.data("rtlist", rtlist);
                    break;
                default:
                    break;
            }
        } catch (Exception exp) {
            Utility.ShowError(exp);
        }
        return t;
    }

    private void generateListMap(List<Map<String, Object>> rtlist, List<CRtDataObject> cRtDataObjects, CRtDataObject supplier) {
        cRtDataObjects.forEach(c -> {
            Map<String, Object> map = new HashMap<>();
            c.getAsRecord().forEach((k, v) -> {
                if (columns.contains(k))
                    map.put(k, v);
            });
            rtlist.add(map);
        });
    }

    @Inject
    SupplierResource srec;

    @Path("rtbaction")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public TemplateInstance handleView() {
        TemplateInstance t = clientTemplate.instance();
        if (command != null && !command.isBlank()) {
            title = command;
            System.out.println("recived view parmetr:"+command+ " and type="+rtype);
            switch (command) {
                case "view":
                    if (rtype=="client") {
                        Client c = new Client();
                        c.find(itemid);
                        t = t.data("title", title + " itemid: " + itemid).data("client", c);
                    } else if (rtype.equals("supplier")) {
                        Supplier supplier = new Supplier();
                        supplier.find(itemid);
                        t = t.data("title", title).data("client", supplier);
                    }
                    break;
                case "edit":
                    if(rtype.equals("supplier")){
                        t=srec.viewSupplier(itemid);
                    }else if(rtype.equals("client")){
                        t=t.data("title","this shuld direct to supplier");
                    }
                    break;
                default:
                    title = "nothing to show";
                    t = t.data(title + " itemid: " + itemid);
                    break;
            }

        }
        return t;
    }

    private void popColumns(String type) {
        if (columns == null) columns = new ArrayList<>();
        columns.clear();
        switch (type) {
            case "client":
                Client client = new Client();
                columns.addAll(client.getBrowsable());
                break;
            case "service":
                CService service = new CService();
                columns.addAll(service.getBrowsable());
                break;
            case "supplier":
                Supplier supplier = new Supplier();
                columns.addAll(supplier.getBrowsable());
                break;
            case "news":
                columns.add("id");
                columns.add("title");
                columns.add("details");
                columns.add("ondate");
                break;
            default:
                break;
        }
    }

}
