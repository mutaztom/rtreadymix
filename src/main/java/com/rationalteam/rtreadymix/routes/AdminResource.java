package com.rationalteam.rtreadymix.routes;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rtreadymix.*;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import org.jboss.resteasy.annotations.ContentEncoding;
import org.jboss.resteasy.annotations.Form;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Path("readymix")
public class AdminResource {

    @Inject
    @ResourcePath("adminspace")
    Template adminspace;
    @Inject
    @ResourcePath("rtviewer")
    Template viewTemplate;
    @Inject
    Rtutil rtutil;
    @ResourcePath("orders")
    Template orders;
    @Inject
    @ResourcePath("settings")
    Template settingsTemplate;
    @Inject
    ClientManager cman;
    @Context
    javax.ws.rs.core.HttpHeaders request;

    OrderStat stat = new OrderStat();
    List<String> mailtemp;
    List<String> smstemp;
    @Inject
    @ResourcePath("orderman")
    Template orderTemplate;
    //form values for settings
    @FormParam("newcat")
    String newcat;
    @FormParam("adminmail")
    String adminmail;
    @FormParam("adminmobile")
    String adminmobile;
    Integer dayslimit;
    //properties
    Properties properties;


    @Path("/orders")
    @GET
    @RolesAllowed("admin")
    public TemplateInstance browseOrders() {
        List<Order> orderList = stat.getOrders();
        List<ClientOrder> col = orderList.stream().map(Order::toClientOrder).collect(Collectors.toList());
        enOrderStatus[] values = enOrderStatus.values();
        return orders.data("isstaff", true)
                .data("title", "Browse Orders")
                .data("rtlist", col)
                .data("clientid", "Admin")
                .data("clients", cman.getCLients()).data("orderstatus", values);
    }


    @GET
    @Path("/dashboard")
    @Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
    @Transactional
    public TemplateInstance dashboard() {
        try {
            List<CProduct> plist = stat.getProducts();
            dayslimit = dayslimit == null ? 5 : dayslimit;
            List<Order> newarrivals = stat.getLatestOrders(Optional.of(dayslimit));
            TemplateInstance t = adminspace.data("curruser", "Amdmin")
                    .data("title", "Admin Dashboard")
                    .data("dayslimit", dayslimit)
                    .data("prodlist", plist)
                    .data("newarrivals", newarrivals)
                    .data("orderstat", stat.getOrdersByStatus())
                    .data("clientstat", stat.getClientsByStatus());
            if (command == "vieworder") {
                return orderTemplate.data("order", plist.get(itemid));
            }
            return t;
        } catch (Exception exp) {
            Utility.ShowError(exp);
            return adminspace.instance();
        }
    }

    @Path("/manageOrder")
    @POST
    public TemplateInstance manageOrder(@FormParam("orderid") Integer orderid) {
        itemid = orderid;
        command = "view";
        return viewOrder();
    }

    @Path("/dashboard/setdays/{days}")
    @GET
    public TemplateInstance setDays(@PathParam("days") Integer days) {
        dayslimit = days;
        TemplateInstance t = dashboard();
        return t.data("dayslimit", days).data("newarrivals", stat.getLatestOrders(Optional.of(days)));
    }

    @Path("settings")
    @GET
    @RolesAllowed("admin")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance settings() {
        Map<String, List<COption>> optionMap = new HashMap<>();
        List optables = MezoDB.Open("select tblname from tblsystemoption");
        for (Object rec :
                optables) {
            String tbl = rec.toString();
            List<COption> optionlist = getOptionValues(tbl);
            if (tbl.startsWith("tbl"))
                tbl = tbl.replace("tbl", "");
            optionMap.put(tbl, optionlist);
        }
        //load properties
        properties = new Properties();
        File f = new File(SystemConfig.PROPFILE);
        InputStream fs = null;
        try {
            fs = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fs != null) {
            try {
                properties.load(fs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mailtemp = new ArrayList<>();
        smstemp = new ArrayList<>();
        listTemplates();

        return settingsTemplate.data("title", "System Settings")
                .data("icon", "settings.png")
                .data("sms_templates", smstemp)
                .data("mail_templates", mailtemp)
                .data("props", properties)
                .data("currlist", rtutil.listCurrency())
                .data("options", optionMap);

    }

    private List<COption> getOptionValues(String tbl) {
        SystemOptionManager man = new SystemOptionManager();
        COption op = new COption(tbl);
        List result = op.listOptions();
        return result;
    }

    private void listTemplates() {
        //templates for email
        java.nio.file.Path email = Paths.get(SystemConfig.TEMPLATE, "email");
        java.nio.file.Path sms = Paths.get(SystemConfig.TEMPLATE, "sms");

        try {
            mailtemp = Files.list(email).map(java.nio.file.Path::getFileName).map(java.nio.file.Path::toString).collect(Collectors.toList());
            smstemp = Files.list(sms).map(java.nio.file.Path::getFileName).map(java.nio.file.Path::toString).collect(Collectors.toList());
        } catch (IOException e) {
            UtilityExt.ShowError(e);
        }
    }

    @Path("savePrice")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response savePrice(@QueryParam("itemid") int itid, @QueryParam("newprice") double newprice) {
        JsonObject o = new JsonObject();
        try {
            String sql = "update tblorder set unitprice=" + newprice +
                    " where id=" + itid;
            boolean r = MezoDB.doSqlIn(sql);
            o.put("result", r);
            return Response.ok(o).build();
        } catch (Exception e) {
            o.put("result", false).put("error", e.getMessage());
            return Response.serverError().entity(o).build();
        }
    }

    @Path("updateStatus")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateStatus(@QueryParam("itemid") int itid, @QueryParam("newstatus") String newstatus) {
        JsonObject o = new JsonObject();
        try {
            String sql = "update tblorder set status='" + newstatus +
                    "' where id=" + itid;
            boolean r = MezoDB.doSqlIn(sql);
            o.put("result", r);
            return Response.ok(o).build();
        } catch (Exception e) {
            o.put("result", false).put("error", e.getMessage());
            return Response.serverError().entity(o).build();
        }
    }

    @FormParam("command")
    String command;
    @FormParam("itemid")
    Integer itemid;

    @Path("/orderman")
    @POST
    @RolesAllowed("admin")
    @Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
    @Transactional
    public TemplateInstance viewOrder() {
        TemplateInstance t = orderTemplate.data("title", "Order Manager (" + itemid + ")")
                .data("icon", "readymix.png");
        if (command == null || command.isBlank())
            return viewTemplate.data("error", "Command cannot be blank");
        try {
            System.out.println("received command: " + command+ "with itemid= "+itemid);
            if (command.equals("view")) {
                Order order = new Order();
                order.find(itemid);
                Client client = order.getClient();
                t = t.data("order", order).data("client", client);
            } else if (command.equals("process")) {
                t = t.data("error", "Feature not implemented yet");
            } else if (command.equals("deliver")) {
                t = t.data("error", "Feature not implemented yet");
            }
            return t;
        } catch (Exception exp) {
            return t.data("error", exp.getMessage());
        }
    }
}

@TemplateExtension(namespace = "str")
class SettingExtenstion {
    public static Boolean isPassword(String item) {
        return (item.contains("password") || item.contains("Password"));
    }

    public static String format(Number n) {
        NumberFormat nfor = NumberFormat.getNumberInstance(Locale.forLanguageTag("en"));
        return nfor.format(n);
    }

    public static String getItem(Integer itsid, String tbl) {
        try {
            if (itsid == null || itsid <= 0)
                return "Not Found";
            return MezoDB.getItem(itsid, tbl);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}

@TemplateExtension
class OrderTools {
    public static String getItemName(Order order, Integer itsid, String tbl) {
//        try {
//            if (itsid == null || itsid <= 0)
//                return "Not Found";
//            return MezoDB.getItem(itsid, tbl);
//        } catch (Exception e) {
//            return e.getMessage();
//        }
        return "Test Tool";
    }

    public static String getCityName(Order order) {
        try {
            if (order.getCity() == null || order.getCity() <= 0)
                return "Not Found";
            return MezoDB.getItem(order.getCity(), "Tblcity");
        } catch (Exception e) {
            Utility.ShowError(e);
            return "Not Set";
        }
    }

    public static String getCountryName(Order order) {
        try {
            Integer index = order.getCountry();
            if (index == null || index <= 0)
                return "Not Found";
            return MezoDB.getItem(index, "tblcountry");
        } catch (Exception e) {
            Utility.ShowError(e);
            return "Not Set";
        }
    }

    public static String getStateName(Order order) {
        try {
            Integer index = order.getState();
            if (index == null || index <= 0)
                return "Not Found";
            return MezoDB.getItem(index, "tblState");
        } catch (Exception e) {
            Utility.ShowError(e);
            return "Not Set";
        }
    }

    public static String getProvinceName(Order order) {
        try {
            Integer index = order.getProvince();
            if (index == null || index <= 0)
                return "Not Found";
            return MezoDB.getItem(index, "tblprovince");
        } catch (Exception e) {
            Utility.ShowError(e);
            return "Not Set";
        }
    }

    public static String getMemberName(Order order) {
        try {
            Integer index = order.getMember();
            if (index == null || index <= 0)
                return "Not Found";
            return MezoDB.getItem(index, "tblmember");
        } catch (Exception e) {
            Utility.ShowError(e);
            return "Not Set";
        }
    }

    public static String getTypeName(Order order) {
        try {
            Integer index = order.getItemid();
            if (index == null || index <= 0)
                return "Not Found";
            return MezoDB.getItem(index, "tblproduct");
        } catch (Exception e) {
            Utility.ShowError(e);
            return "Not Set";
        }
    }

    public static String getStrOndate(Order order) {
        return order.getOndate().format(DateTimeFormatter.ISO_ORDINAL_DATE);
    }

    public static String getStrDateNeeded(Order order) {
        return order.getDateNeeded().format(DateTimeFormatter.ISO_ORDINAL_DATE);
    }
}
