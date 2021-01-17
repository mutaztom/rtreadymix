package com.rationalteam.rtreadymix.routes;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rterp.erpcore.COption;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.SystemOptionManager;
import com.rationalteam.rtreadymix.*;
import io.quarkus.qute.Mapper;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.core.json.JsonObject;
import org.jboss.resteasy.annotations.Form;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.naming.spi.DirectoryManager;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("readymix")
public class AdminResource {

    @Inject
    @ResourcePath("adminspace")
    Template adminspace;
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

    public List<COption> getOptionValues(String tbl) {
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

        //templates for sms
    }
}

@TemplateExtension(namespace = "str")
class SettingExtenstion {
    public static Boolean isPassword(String item) {
        return (item.contains("password") || item.contains("Password"));
    }
}
