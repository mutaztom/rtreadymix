package com.rationalteam.rtreadymix.routes;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rterp.erpcore.COption;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.SystemOptionManager;
import com.rationalteam.rtreadymix.*;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        return orders.data("isstaff", true)
                .data("title", "Browse Orders")
                .data("rtlist", col)
                .data("clientid", "Admin")
                .data("clients", cman.getCLients());
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
                .data("sms_templates", mailtemp)
                .data("mail_templates", smstemp)
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

    @FormParam("command")
    String command;
    @FormParam("itemid")
    Integer itemid;

    @Path("/manageorder")
    @POST
    @RolesAllowed("admin")
    @Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
    @Transactional
    public TemplateInstance viewOrder() {
        TemplateInstance t = viewTemplate.instance();
        if (command == null || command.isBlank())
            return viewTemplate.data("error", "Command cannot be blank");
        try {
            if (command.equals("view")) {
                Order order = new Order();
                order.find(itemid);
                System.out.println(order.getAsRecord());
                t = viewTemplate.data("client", order);
            } else if (command.equals("process")) {
                t = viewTemplate.data("error", "Feature not implemented yet");
            } else if (command.equals("deliver")) {
                t = viewTemplate.data("error", "Feature not implemented yet");
            }
            return t;
        } catch (Exception exp) {
            return viewTemplate.data("error", exp.getMessage());
        }
    }
}

@TemplateExtension(namespace = "str")
class SettingExtenstion {
    public static Boolean isPassword(String item) {
        return (item.contains("password") || item.contains("Password"));
    }

}
