package com.rationalteam.rtreadymix.routes;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.net.URI;
import java.util.Scanner;

@Provider
public class AppExceptions implements ExceptionMapper<NotFoundException>
{

    public Response toResponse(NotFoundException exception) {
        String text = new Scanner(this.getClass().getResourceAsStream("/META-INF/resources/404.html"), "UTF-8").useDelimiter("\\A").next();
        return Response.status(404).entity(text).build();
    }
}

