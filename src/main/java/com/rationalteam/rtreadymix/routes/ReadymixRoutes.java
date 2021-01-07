package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rterp.erpcore.CRtDataObject;
import com.rationalteam.rterp.erpcore.DataManager;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rtreadymix.Client;
import com.rationalteam.rtreadymix.ClientManager;
import com.rationalteam.rtreadymix.UtilityExt;
import com.rationalteam.rtreadymix.data.Tblclient;
import com.rationalteam.rtreadymix.data.Tblorder;
import io.quarkus.agroal.DataSource;
import io.quarkus.hibernate.orm.PersistenceUnit;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.smallrye.mutiny.Multi;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

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

    @Route(path = "/greetings", methods = HttpMethod.GET)
    void greetings(RoutingExchange ex) {
        ex.ok("hello " + ex.getParam("name").orElse("world"));
    }

    @Route(path = "/showClients",methods = HttpMethod.GET)
    @Transactional
    Multi<Object> showClients(RoutingExchange ex) {
        try {
            List<Tblclient> clist = cman.getCLients();
            return Multi.createFrom().items(clist);
        } catch (Exception exp) {
            ex.serverError().end(exp.getMessage());
            return Multi.createFrom().item(exp.getMessage());
        }
    }

}
