package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rtreadymix.*;
import com.rationalteam.rtreadymix.purchase.Supplier;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
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
    @ResourcePath("client")
    Template ctemp;

    @FormParam("_method")
    String command;


    String title;
    List<String> columns = new ArrayList<>();
    String rtype = "";
    String icon;
    Map<String, String> iconmap = new HashMap<>();
    private String searchFor;

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
        return t.data("title", title)
                .data("columns", columns)
                .data("type", type)
                .data("findwhat", searchFor)
                .data("filteraction", "filter");
    }

    @Path("/filter")
    @POST
    @RolesAllowed("admin")
    public void filter(@FormParam("command") String command,
                       @FormParam("findwhat") String findwhat) {
        if (command == null)
            return;
        if (command.equals("search") && findwhat != null && !findwhat.isBlank()) {
            searchFor = findwhat;
        } else if (command.equals("clear")) {
            searchFor = null;
        }
    }

    private TemplateInstance popdata(String type) {
        TemplateInstance t = template.instance();
        List<Map<String, Object>> rtlist;
        try {
            List<Map<String, Object>> jar = new ArrayList<>();
            Map<String, Object> filterMap = new HashMap<>();
            if (searchFor != null) filterMap.put("item", searchFor);
            switch (type) {
                case "client":
                    Client client = new Client();
                    if (searchFor != null) {
                        filterMap.clear();
                        if (searchFor.matches("[+]?[\\d]*")) filterMap.put("mobile", searchFor);
                        else if (searchFor.matches("\\w*[@]\\w*")) filterMap.put("email", searchFor);
                        else filterMap.put("item", searchFor);
                    }
                    List<Client> clist = searchFor == null ? client.listAll() : client.filter(filterMap);
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
                    generateListMap(rtlist, searchFor == null ? service.listAll() : service.filter(filterMap));
                    t = t.data("rtlist", rtlist);
                    break;
                case "product":
                    CProduct product = new CProduct();
                    rtlist = new ArrayList<>();
                    generateListMap(rtlist, searchFor == null ? product.listAll() : product.filter(filterMap));
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
                    generateListMap(rtlist, searchFor == null ? supplier.listAll() : supplier.filter(filterMap));
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
        NumberFormat nform = NumberFormat.getInstance(Locale.US);
        CExchange xchange = new CExchange();
        cRtDataObjects.forEach(c -> {
            Map<String, Object> map = new HashMap<>();
            c.getAsRecord().forEach((k, v) -> {
                if (columns.contains(k))
                    map.put(k, v);
            });
            if (rtype.equals("service")) {
                CService s = (CService) c;
                map.put("unitprice", nform.format(s.getUnitPrice()));
                double sequiv = xchange.convert(SystemConfig.getDefaultCurrencyId(), SystemConfig.getCompCurrency().getId(), s.getUnitPrice());
                map.put("equivPrice", nform.format(sequiv));
                map.put("unit", s.getUnit());
                map.put("description", s.getDescription());
            } else if (rtype.equals("product")) {
                CProduct s = (CProduct) c;
                map.put("unitprice", nform.format(s.getUnitPrice()));
                double equiv = xchange.convert(SystemConfig.getDefaultCurrencyId(), SystemConfig.getCompCurrency().getId(), s.getUnitPrice());
                map.put("unitprice", nform.format(s.getUnitPrice()));
                map.put("equivPrice", nform.format(equiv));
                map.put("unit", s.getUnit());
                map.put("description", s.getDescription());
            }
            rtlist.add(map);
        });

    }

    @Path("rtbaction")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response handleView() {
        TemplateInstance t = browseTemplate.instance();
        if (command != null && !command.isBlank()) {
            StringTokenizer tkn = new StringTokenizer(command, "_");
            String cmd = tkn.countTokens() > 0 ? tkn.nextToken() : "";
            Integer itemid = tkn.hasMoreTokens() ? Integer.parseInt(tkn.nextToken()) : -1;
            switch (cmd) {
                case "view":
                case "edit":
                    return Response.seeOther(UriBuilder.fromPath("/readymix/" + rtype).queryParam("itemid", itemid).build()).build();
                default:
                    title = "nothing to show";
                    break;
            }
        }
        return Response.notModified().build();
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
                columns.add("equivPrice");
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
                columns.add("equivPrice");
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
