package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rtreadymix.Order;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("readymix")
public class SchedResources {
    @Inject
    @ResourcePath("schedule")
    Template schedTemplate;

    @GET
    @Path("/sched")
    public TemplateInstance sched(){
        Order order=new Order();
        List<Order> orlist = order.listAll();
        return schedTemplate.data("title","ReadyMix Delivery Sched")
                .data("orders",orlist);
    }
}
