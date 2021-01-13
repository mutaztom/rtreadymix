package com.rationalteam.rtreadymix.routes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rationalteam.reaymixcommon.News;
import com.rationalteam.rterp.erpcore.CService;
import com.rationalteam.rtreadymix.*;
import com.rationalteam.rtreadymix.data.Tblclient;
import com.rationalteam.rtreadymix.purchase.Supplier;
import io.quarkus.jackson.ObjectMapperCustomizer;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Path("readymix")
public class RtbrowseResource {
    @Inject
    @ResourcePath("clients")
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

    @Path("rtbrowse")
    @GET
    public TemplateInstance browse(@QueryParam("type") String type) {
        title = "Browse " + type;
        popColumns(type);
        TemplateInstance t = popdata(type);
        return t.data("title", title).data("columns", columns);
    }

    private TemplateInstance popdata(String type) {
        TemplateInstance t = template.instance();
        switch (type) {
            case "client":
                Client client = new Client();
                List<Field> bf = client.getBrowsableFields();

                t = template.data("rtlist", client.listAll());
                break;
            case "service":
                CService service = new CService();
                t = template.data("rtlist", service.listAll());
                break;
            case "news":
                OrderStat stat = new OrderStat();
                t = template.data("rtlist", stat.getNews());
                break;
            case "supplier":
                Supplier supplier = new Supplier();
                t = template.data("rtlist", supplier.listAll());
                break;
            default:
                break;
        }
        return t;
    }

    @Inject
    JsonCustomizer jcust;

    @Path("rtbaction")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public TemplateInstance handleView() {
        TemplateInstance t = clientTemplate.instance();
        if (command != null && !command.isBlank()) {
            title = command;
            switch (command) {
                case "view":
                    Client c = new Client();
                    c.find(itemid);
                    System.out.println("itemid value:" + itemid);
                    t = t.data("title", title + " itemid: " + itemid).data("client", c);
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
        columns.add("Id");
        columns.add("Item");
        switch (type) {
            case "client":
                Client client = new Client();
                columns.clear();
                columns.addAll(client.getBrowsable());
                break;
            case "service":
                CService service = new CService();
                columns.clear();
                columns.addAll(service.getBrowsable());
                break;
            case "supplier":
                Supplier supplier = new Supplier();
                columns.clear();
                columns.addAll(supplier.getBrowsable());
                break;
            case "news":
                columns.add("details");
                columns.add("ondate");
                break;
            default:
                break;
        }
    }

}
