package com.rationalteam;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rtreadymix.IRationalEvents;
import com.rationalteam.rtreadymix.Order;
import com.rationalteam.rtreadymix.security.UserManager;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.servlet.FilterConfig;
import javax.transaction.Transactional;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.awt.*;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
public class RationalServicesTest {
    private static final String TO = "mutaztom@gmail.com";
    @Inject
    MockMailbox mailbox;

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/rtmix/getCities")
                .then()
                .statusCode(200)
                .body(is("hello"));
    }

    @Test
    public void sendEmail() {
        // call a REST endpoint that sends email
        JsonObject body = new JsonObject();
        body.put("mailto", TO)
                .put("message", "Hi Mr. Mutaz; This is to inform you that new services of quarkus are ready. Regards");

        given().baseUri("http://localhost:8080")
                .auth().form("admin", "admin",
                new FormAuthConfig("j_security_check", "j_username", "j_password"))
                .when().body(body).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .post("/rtmix/sendMail")
                .then()
                .statusCode(200)
                .body(is("Mail sent successfully"));

        // verify that it was sent
        List<Mail> sent = mailbox.getMessagesSentTo(TO);
        assertThat("Is Sent", sent.size() == 1);
        Mail actual = sent.get(0);
        assertThat("Actual assertion ", actual.getText().contains("Wake up!"));
        assertThat("Subject Equity", actual.getSubject().equals("Alarm!"));
        assertThat("IS SETNT", mailbox.getTotalMessagesSent() == 6);
    }

    @Test
    public void placeOrder() {
        Order order = new Order();
        order.find(78);
        ClientOrder cord = order.toClientOrder();
        given()
                .auth().form("admin", "admin",
                new FormAuthConfig("j_security_check", "j_username", "j_password"))
                .when().body(cord).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .post("/rtmix/placeOrder")
                .then()
                .statusCode(200)
                .extract().body().asString();
    }

    @Test
    public void eventBus() {
        given()
                .when().post("/rtmix/checkevents")
                .then()
                .statusCode(200)
                .extract().body().asString();
    }
}