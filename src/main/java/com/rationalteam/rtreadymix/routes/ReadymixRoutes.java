package com.rationalteam.rtreadymix.routes;

import com.rationalteam.reaymixcommon.ServerMessage;
import com.rationalteam.rterp.erpcore.CExchange;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rtreadymix.ClientManager;
import com.rationalteam.rtreadymix.CommHub;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;


@ApplicationScoped
public class ReadymixRoutes {
    @Inject
    ClientManager cman;
    @Inject
    CommHub commHub;

    @Route(path = "/")
    void root(RoutingContext rc) {
        rc.reroute("/readymix/dashboard");
    }

    @Route(path = "/readymix/removeOption", methods = HttpMethod.POST)
    @Transactional
    Uni<String> removeOption(RoutingContext cont) {
        try {
            if (cont.getBody() == null)
                return Uni.createFrom().item("Please put json body with required items");

            @Nullable JsonObject o = cont.getBodyAsJson();
            return Uni.createFrom().item("delete from $tbl where id=$id")
                    .onItem().transform(t -> t.replace("$tbl", o.getString("table")))
                    .onItem().transform(t -> t.replace("$id", o.getInteger("itemid").toString()))
                    .onItem().invoke(MezoDB::doSqlIn).runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                    .onItem().invoke((Consumer<String>) System.out::println).onItem().transform(t -> "OK").onFailure().recoverWithItem(e -> "error:" + e.getMessage());
        } catch (Exception e) {
            return Uni.createFrom().item("error:" + e.getMessage());
        }
    }

    @Route(path = "/readymix/modifyOption", methods = HttpMethod.POST, produces = "text/plain", consumes = "application/json")
    @Transactional
    Uni<String> modifyOption(RoutingContext cont) {
        try {
            if (cont.getBody() == null)
                return Uni.createFrom().item("Please put json body with required items");
            @Nullable JsonObject o = cont.getBodyAsJson();
            String item = o.getString("item");
            String table = o.getString("table");
            String aritem = o.getString("aritem");
            Integer itemid = o.getInteger("itemid");
            return Uni.createFrom().item("update $tbl set item='$item', aritem='$aritem' where id=$id")
                    .onItem().transform(t -> t.replace("$tbl", table))
                    .onItem().transform(t -> t.replace("$item", item))
                    .onItem().transform(t -> t.replace("$aritem", aritem))
                    .onItem().transform(t -> t.replace("$id", itemid.toString()))
                    .onItem().transform(t -> t).invoke(MezoDB::doSqlIn).runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                    .onItem().transform(t -> "OK").onFailure().recoverWithItem(e -> "error:" + e.getMessage());
        } catch (Exception e) {
            return Uni.createFrom().item("error: " + e.getMessage());
        }
    }

    @Route(path = "/readymix/addOption", methods = HttpMethod.POST, produces = "text/plain",
            consumes = "application/json", type = Route.HandlerType.BLOCKING)
    @Transactional
    Uni<String> addOption(RoutingContext cont) {
        try {
            if (cont.getBody() == null)
                return Uni.createFrom().item("Please put json body with required items");
            @Nullable JsonObject o = cont.getBodyAsJson();
            System.out.println(o);
            String table = o.getString("table");
            String item = o.getString("item");
            String aritem = o.getString("aritem");
            Integer exists = MezoDB.getInteger("select count(id) from " + table + " where item='" + item + "'");
            if (exists > 0) {
                return Uni.createFrom().item("error:Cant create existing object");
            }
            Uni<String> main = Uni.createFrom().item("insert into $tbl (item,aritem) values('$item','$aritem')")
                    .onItem().transform(t -> t.replace("$tbl", table))
                    .onItem().transform(t -> t.replace("$item", item))
                    .onItem().transform(t -> t.replace("$aritem", aritem))
                    .onItem().transform(t -> t).invoke(MezoDB::doSqlIn)
                    .onItem().transform(t -> "OK").onFailure().recoverWithItem(e -> "error:" + e.getMessage());

            return main;
        } catch (Exception e) {
            Utility.ShowError(e);
            return Uni.createFrom().item(e.getMessage());
        }
    }

    @Route(path = "/readymix/updateProdPrice", consumes = "text/plain", methods = HttpMethod.POST,
            produces = "text/plain", type = Route.HandlerType.BLOCKING)
    @Transactional
    public Uni<String> updateProdPrice(RoutingExchange cont) {
        try {
            String ondate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            boolean r = MezoDB.doSqlIn("update tblproduct set datasheet='" + ondate + "', unitprice="
                    + cont.getParam("newprice").get() + " where id="
                    + cont.getParam("prodid").get());
            return Uni.createFrom().item(r ? "OK : " + ondate : "error:Could not update price");
        } catch (Exception exp) {
            Utility.ShowError(exp);
            return Uni.createFrom().item("error:Price updated succesfully");
        }
    }

    @Route(path = "/readymix/updateRate", methods = HttpMethod.POST, produces = "application/json")
    @Transactional
    Uni<ServerMessage> updateRate(RoutingContext rex) {
        if (rex.getBodyAsJson().containsKey("rate")
                && rex.getBodyAsJson().containsKey("compcur")) {
            double rate = Double.parseDouble(rex.getBodyAsJson().getString("rate"));
            Integer compcur = Integer.parseInt(rex.getBodyAsJson().getString("compcur"));
            return Uni.createFrom().item(new CExchange()).onItem().invoke(t -> t.saveRate(compcur, rate)).runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                    .onItem().transform(t -> new ServerMessage("Exchange was saved :" + t))
                    .onFailure().recoverWithItem(t -> new ServerMessage("failed to save"));
        } else
            return Uni.createFrom().item(new ServerMessage("body didn't contain all required params " + rex.getBodyAsString()));
    }

    @Inject
    ReactiveMailer remail;

    @Route(path = "/readymix/sendMail", methods = HttpMethod.PUT, produces = "application/json", consumes = "application/json")
    @RolesAllowed("admin")
    public CompletionStage<Response> sendMail(RoutingContext cont) {
        @Nullable JsonObject json = cont.getBodyAsJson();
            String msg = json.getString("message");
            String mailto = json.getString("mailto");
            ServerMessage m = new ServerMessage("Mail sent successfully");
            return remail.send(Mail.withText(mailto, "ReadyMix Admin Message", msg)).subscribeAsCompletionStage()
                    .thenApply(t -> {
                        System.out.println("Mail sent successfully");
                        return Response.ok(m).build();
                    }).
                            exceptionally(t -> {
                                System.out.println("An error has occurred at mail sending, " + t.getMessage());
                                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), t.getMessage()).build();
                            });

    }

    @Route(path = "/readymix/sendSMS", methods = HttpMethod.PUT, produces = "application/json", consumes = "application/json", type = Route.HandlerType.BLOCKING)
    @RolesAllowed({"admin", "user"})
    public Uni<Response> sendSMS(RoutingContext cont) {
        try {
            String mobile = cont.getBodyAsJson().getString("mobile");
            String msg = cont.getBodyAsJson().getString("message");
            System.out.println("Message" + msg);
            return Uni.createFrom().completionStage(Uni.createFrom().item(commHub.sendSMS(mobile, msg)).subscribe().asCompletionStage())
                    .onItem().transform(t -> {
                        System.out.println("SMS sent successfully");
                        return Response.ok("SMS Sent succesfully").build();
                    })
                    .onFailure().recoverWithItem(t -> {
                        System.out.println("Could not send sms: " + t.getMessage());
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), t.getMessage()).build();
                    });
        } catch (Exception e) {
            return Uni.createFrom().item(Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build());
        }
    }

    @Route(path = "/readymix/saveCall", methods = HttpMethod.POST, produces = "application/json", type = Route.HandlerType.BLOCKING)
    @RolesAllowed({"admin", "user"})
    @Transactional
    public Uni<Response> saveCall(RoutingContext con) {
        try {
            String message = con.getBodyAsJson().getString("message");
            String mobile = con.getBodyAsJson().getString("mobile");
            String clientid = con.getBodyAsJson().getString("clientid");
            String sql = "insert into tblcomlog(email,address,message,smstime,byuser)" +
                    " values('" + clientid + "','" + mobile + "','" + message + "',current_date(),'admin')";
            return Uni.createFrom().completionStage(Uni.createFrom().item(MezoDB.doSqlIn(sql))
                    .subscribeAsCompletionStage()).onItem().transform(t -> {
                System.out.println("Call saved successfully " + t);
                return Response.ok("Call saved successfully").build();
            }).onFailure().recoverWithItem(t -> {
                System.out.println("Could not save call log: " + t.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), t.getMessage()).build();
            });
        } catch (Exception e) {
            return Uni.createFrom().item(Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build());
        }
    }

}
