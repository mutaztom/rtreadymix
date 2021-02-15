package com.rationalteam.rtreadymix.routes;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("readymix")
public class SchedResources {
    @Inject
    @ResourcePath("schedule")
    Template schedTemplate;

    @GET
    @Path("/sched")
    public TemplateInstance sched(){
        return schedTemplate.data("title","ReadyMix Delivery Sched");
    }
}
