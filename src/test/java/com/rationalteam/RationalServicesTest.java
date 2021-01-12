package com.rationalteam;

import com.rationalteam.rtreadymix.security.UserManager;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class RationalServicesTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/rtmix/getCities")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }
    @Test
    public void addUser(){
        UserManager uman=new UserManager();
        uman.add("admin","admin","admin");
        uman.add("user","user","user");
    }

}