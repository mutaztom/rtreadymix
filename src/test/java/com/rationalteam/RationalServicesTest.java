package com.rationalteam;

import com.rationalteam.rtreadymix.security.UserManager;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

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
    public void sendEmail(){
        // call a REST endpoint that sends email
        JsonObject body=new JsonObject();body.put("mailto",TO).put("message","Hi Mr. Mutaz; This is to inform you that new services of quarkus are ready. Regards");
        given()
                .when().body(body).accept(MediaType.APPLICATION_JSON)
                .post("/readymix/sendMail")
                .then()
                .statusCode(202)
                .body(is("OK"));

        // verify that it was sent
        List<Mail> sent = mailbox.getMessagesSentTo(TO);
        assertThat("Is Sent",sent.size()==1);
        Mail actual = sent.get(0);
        assertThat("Actual assertion ",actual.getText().contains("Wake up!"));
        assertThat("Subject Equity",actual.getSubject().equals("Alarm!"));

        assertThat("IS SETNT",mailbox.getTotalMessagesSent()==6);
    }

}