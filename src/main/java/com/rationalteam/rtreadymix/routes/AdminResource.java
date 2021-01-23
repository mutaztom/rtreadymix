package com.rationalteam.rtreadymix.routes;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rtreadymix.*;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.core.json.JsonObject;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    @FormParam("smshost")
    String smshost;
    @FormParam("smsport")
    String smsport;
    @FormParam("smsuser")
    String smsuser;
    @FormParam("smspassword")
    String smspassword;
    @FormParam("smtphost")
    String smtphost;
    @FormParam("mailuser")
    String mailuser;
    @FormParam("smtpport")
    String smtpport;
    @FormParam("smtppassword")
    String smtppassword;
    @FormParam("mailpassword")
    String mailpassword;

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
                .data("clients", cman.getCLients()).data("orderstatus",values);
    }

    @GET
    @Path("/dashboard")
    public TemplateInstance dashboard() {
        return adminspace.data("curruser", "Amdmin")
                .data("title", "Admin Dashboard")
                .data("clnt", "Mutaz")
                .data("orderstat", stat.getOrdersByStatus())
                .data("clientstat", stat.getClientsByStatus());
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
                .data("sms_templates",smstemp )
                .data("mail_templates",mailtemp )
                .data("props", properties)
                .data("currlist", rtutil.listCurrency())
                .data("activecur",rtutil.listCurrency().get(0))
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
