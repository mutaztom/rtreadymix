package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rtreadymix.ClientManager;
import com.rationalteam.rtreadymix.SystemConfig;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;


@ApplicationScoped
public class ReadymixRoutes {
    @Inject
    ClientManager cman;

    // neither path nor regex is set - match a path derived from the method name
    @Route(methods = HttpMethod.GET)
    void hello(RoutingContext rc) {
        rc.response().end("hello");
    }

    @Route(path = "/world")
    String helloWorld() {
        return "Hello world!";
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
            boolean r = MezoDB.doSqlIn("update tblproduct set datasheet='"+ondate+"', unitprice="
                    + cont.getParam("newprice").get() + " where id="
                    + cont.getParam("prodid").get());
            return Uni.createFrom().item(r ? "OK : "+ondate : "error:Could not update price");
        } catch (Exception exp) {
            Utility.ShowError(exp);
            return Uni.createFrom().item("error:Price updated succesfully");
        }
    }
}
