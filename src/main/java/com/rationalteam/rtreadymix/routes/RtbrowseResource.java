package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rtreadymix.*;
import com.rationalteam.rtreadymix.purchase.Supplier;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.NumberFormat;
import java.util.*;

@Path("readymix")
public class RtbrowseResource {
    @Inject
    @ResourcePath("rtbrowse")
    Template template;
    @Inject
    @ResourcePath("rtviewer")
    Template browseTemplate;
    @Inject
    ClientManager cman;
    @Inject
    SupplierResource srec;
    @Inject
    @ResourcePath("client")
    Template ctemp;
    @Inject
    ServiceResource servtemp;
    @Inject
    ProductResource productTemp;

    @FormParam("_method")
    String command;
    @FormParam("itemid")
    Integer itemid;

    String title;
    List<String> columns = new ArrayList<>();
    String rtype = "";
    String icon;
    Map<String, String> iconmap = new HashMap<>();

    public RtbrowseResource() {
        iconmap.put("supplier", "businessman.png");
        iconmap.put("product", "product.png");
        iconmap.put("service", "service.png");
        iconmap.put("client", "mobiles.png");
        iconmap.put("settings", "settings.png");
    }

    private void appendCommon(TemplateInstance tmp) {
        tmp.data("title", rtype + " Browser ")
                .data("icon", iconmap.getOrDefault(rtype, "truck.png"));
    }


    @Path("rtbrowse")
    @GET
    public TemplateInstance browse(@QueryParam("type") String type) {
        rtype = type != null ? type : "Dashboard";
        title = String.valueOf(rtype.charAt(0)).toUpperCase().concat(rtype.substring(1)).concat(" Browser ");
        popColumns(type);
        TemplateInstance t = popdata(type);
        appendCommon(t);
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
                    t = t.data("rtlist", rtlist);
                    break;
                case "service":
                    CService service = new CService();
                    rtlist = new ArrayList<>();
                    generateListMap(rtlist, service.listAll());
                    t = t.data("rtlist", rtlist);
                    break;
                case "product":
                    CProduct product = new CProduct();
                    rtlist = new ArrayList<>();
                    generateListMap(rtlist, product.listAll());
                    t = t.data("rtlist", rtlist);
                    break;
                case "news":
                    OrderStat stat = new OrderStat();
                    stat.getNews().forEach(jn -> {
                        Map<String, Object> map = null;
                        jar.add(UtilityExt.jsonToMap(jn));
                    });
                    t = t.data("rtlist", jar);
                    break;
                case "supplier":
                    Supplier supplier = new Supplier();
                    rtlist = new ArrayList<>();
                    generateListMap(rtlist, supplier.listAll());
                    t = t.data("rtlist", rtlist);
                    break;
                default:
                    break;
            }
        } catch (Exception exp) {
            Utility.ShowError(exp);
        }

        return t;
    }

    private void generateListMap(List<Map<String, Object>> rtlist, List<CRtDataObject> cRtDataObjects) {
        NumberFormat nform = NumberFormat.getInstance();
        cRtDataObjects.forEach(c -> {
            Map<String, Object> map = new HashMap<>();
            c.getAsRecord().forEach((k, v) -> {
                if (columns.contains(k))
                    map.put(k, v);
            });
            if (rtype.equals("service")) {
                CService s = (CService) c;
                map.put("unitprice", nform.format(s.getUnitPrice()));
                map.put("unit", s.getUnit());
                map.put("description", s.getDescription());
            } else if (rtype.equals("product")) {
                CProduct s = (CProduct) c;
                map.put("unitprice", nform.format(s.getUnitPrice()));
                map.put("unit", s.getUnit());
                map.put("description", s.getDescription());
            }
            rtlist.add(map);
        });

    }


    @Path("rtbaction")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public TemplateInstance handleView() {
        TemplateInstance t = browseTemplate.instance();
        if (command != null && !command.isBlank()) {
            title = command;
            switch (command) {
                case "view":
                    if (rtype.equals("client")) {
                        Client c = new Client();
                        c.find(itemid);
                        t = t.data("title", title + " itemid: " + itemid).data("client", c);
                    } else if (rtype.equals("supplier")) {
                        Supplier supplier = new Supplier();
                        supplier.find(itemid);
                        t = t.data("title", title).data("client", supplier);
                    } else if (rtype.equals("service")) {
                        CService s = new CService();
                        s.find(itemid);
                        t = t.data("title", title).data("client", s);
                    } else if (rtype.equals("product")) {
                        CProduct p = new CProduct();
                        p.find(itemid);
                        t = t.data("title", title).data("client", p);
                    }
                    break;
                case "edit":
                    if (rtype.equals("supplier")) {
                        t = srec.viewSupplier(itemid);
                    } else if (rtype.equals("client")) {
                        Client client = new Client();
                        client.find(itemid);
                        return ctemp.data("client", client)
                                .data("title", "Client Profile")
                                .data("cities",new String[]{"Khartoum","Shandi","PortSudan"});
                    } else if (rtype.equals("service")) {
                        t = servtemp.viewService(itemid);
                    } else if (rtype.equals("product")) {
                        t = productTemp.viewproduct(itemid);
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
                columns.add("unit");
                columns.add("unitprice");
                columns.add("description");
                break;
            case "supplier":
                Supplier supplier = new Supplier();
                columns.addAll(supplier.getBrowsable());
                break;
            case "product":
                CProduct product = new CProduct();
                columns.addAll(product.getBrowsable());
                columns.add("unit");
                columns.add("unitprice");
                columns.add("description");
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
