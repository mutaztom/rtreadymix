package com.rationalteam.rtreadymix.routes;

import com.rationalteam.core.ISearchable;
import com.rationalteam.core.security.enUserType;
import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.reaymixcommon.ServerMessage;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rterp.erpcore.data.TblProduct;
import com.rationalteam.rtreadymix.*;
import com.rationalteam.rtreadymix.data.Tblorder;
import com.rationalteam.rtreadymix.data.Tblusers;
import com.rationalteam.rtreadymix.security.UserManager;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
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
    @ResourcePath("schedule")
    Template schedTemp;
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
    Integer dayslimit;
    //properties
    Properties properties;

    static List<Order> orderList = null;
    private String searchFor;

    @Path("/orders")
    @GET
    @RolesAllowed("admin")
    public TemplateInstance browseOrders() {
        if (orderList == null)
            orderList = stat.getOrders();
        System.out.println("called browse orders returned size=" + orderList.size());
        List<ClientOrder> col = orderList.stream().map(Order::toClientOrder).collect(Collectors.toList());
        enOrderStatus[] values = enOrderStatus.values();
        return orders.data("isstaff", true)
                .data("title", "Browse Orders")
                .data("rtlist", col)
                .data("clientid", "Admin")
                .data("filteraction", "filterorder")
                .data("findwhat", searchFor)
                .data("clients", cman.getCLients()).data("orderstatus", values);
    }

    @Path("/filterorder")
    @POST
    @RolesAllowed("admin")
    public void filter(@FormParam("findwhat") String findwhat,
                       @FormParam("client") String client) {
        String sql = "";
        if (command.equals("search") && findwhat != null && !findwhat.isBlank()) {
            searchFor = findwhat;
            if (findwhat.matches("(C|c)\\d*")) {
                sql = "type in (select id from TblProduct where item like '%" + findwhat + "%')";
            } else if (findwhat.matches("(Raft|Footing|Wall|Colum|Slab)")) {
                sql = "member in (select id from tblmember where item like '%" + findwhat + "%')";
            } else {
                sql = "item like '%" + findwhat + "%' or notes like '%" + findwhat + "%'";
            }
            System.out.println(sql);
            PanacheQuery<Tblorder> olist = Tblorder.find(sql);
            orderList.clear();
            olist.stream().forEach(o -> {
                Order order = new Order();
                order.setData(o);
                orderList.add(order);
            });
        } else if (command.equals("clear")) {
            searchFor = null;
            orderList = stat.getOrders();
        } else if (command.equals("filterbyclient")) {
            Order order = new Order();
            Map<String, Object> map = new HashMap<>();
            map.put("clientid", client);
            orderList = order.filter(map);
            System.out.println("filter called:" + map + " size:" + orderList.size());
        }

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
                    .data("sales", stat.getSales())
                    .data("volumes", stat.getVolumes())
                    .data("permember", stat.getPerMember())
                    .data("clientstat", stat.getClientsByStatus());
            if (command != null)
                if (command.equals("vieworder")) {
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
        List<Tblusers> users = UserManager.getUsers();
        String[] adminmobiles = Utility.getProperty("adminmobile").split(",");
        return settingsTemplate.data("title", "System Settings")
                .data("icon", "settings.png")
                .data("sms_templates", smstemp)
                .data("mail_templates", mailtemp)
                .data("props", properties)
                .data("currlist", rtutil.listCurrency())
                .data("users", users)
                .data("adminmobiles", adminmobiles)
                .data("options", optionMap);
    }

    @Path("/setNotification")
    @POST
    @RolesAllowed("admin")
    public void setNotification(@FormParam("emailnotify") boolean notifysms,
                                @FormParam("smsnotify") boolean notifyemail,
                                @FormParam("adminemails") List<String> adminemails,
                                @FormParam("adminmobiles") List<String> adminmobiles) {
        Utility.updateProperty("notify.email", notifysms ? String.valueOf(notifyemail) : "false", SystemConfig.PROPFILE);
        Utility.updateProperty("notify.sms", notifyemail ? String.valueOf(notifysms) : "false", SystemConfig.PROPFILE);
        Utility.updateProperty("adminmobile", adminmobiles.toString().replace("[", "").replace("]", "")
                .replace(",", ";"), SystemConfig.PROPFILE);
        Utility.updateProperty("adminmail", adminemails.toString().replace("[", "").replace("]", "")
                .replace(",", ";"), SystemConfig.PROPFILE);
        //reload properties
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

            if (command.equals("view")) {
                Order order = new Order();
                order.find(itemid);
                Client client = order.getClient();
                t = t.data("order", order).data("client", client);
            } else if (command.equals("process")) {
                t = t.data("error", "Feature not implemented yet");
            } else if (command.equals("deliver")) {
                t = t.data("error", "Feature not implemented yet");
            } else if (command.equals("schedule")) {
                Order order = new Order();
                List<Order> orlist = order.listAll();
                return schedTemp.data("title", "ReadyMix Schedule Manager")
                        .data("orders", orlist);
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

    public static String getProduct(int findid) {
        return MezoDB.getItem(findid, "TblProduct");
    }

    public static String getSupplier(int findid) {
        return MezoDB.getItem(findid, "tblsupplier");
    }

    public static boolean isNotifyEmail(String item) {
        Object src = System.getProperties().get("adminmail");
        return src != null && (src.toString().contains(item));
    }

    public static boolean isNotifyMobile(String item) {
        Object src = System.getProperties().get("adminmobile");
        return src != null && (src.toString().contains(item));
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

    public static String getStyle(String st) {
        String stcolor = "Brown";
        switch (st) {
            case "Processing":
                stcolor = "color:blue;";
                break;
            case "Confirmed":
                stcolor = "color:darkred;font-weight:bold;background-color:#bb9976;";
                break;
            case "Created":
                stcolor = "color:red;font-weight:bold";
                break;
            case "Rejected":
                stcolor = "color:gray;text-decoration: line-through;";
                break;
            case "Delivered":
                stcolor = "color:darkgreen;font-style:italic;background-color:#74ba50";
                break;
            case "Canceled":
                stcolor = "color:orange;text-decoration: line-through";
                break;
            default:
                stcolor = "color:red;";
        }
        return stcolor;
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
