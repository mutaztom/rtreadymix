package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rtreadymix.data.Tblclients;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;


@Path("/rtmix/")
public class RationalServices {
    @Inject
    ClientManager cman;


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("hello")
    public String hello() {
        return "hello";
    }

    private Set<Tblclients> userlist = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));


    @GET
    @Path("/getclients")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tblclients> getClients() {
        List<Tblclients> list = new ArrayList<>();
        try {
            list = cman.getCLients();
            return list;
        } catch (Exception exp) {
            System.out.println(exp.getMessage());
        }
        return list;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/signup/")
    public String signup(Tblclients client) {
        String output = "No thing was processed";
        try {
            boolean r;
            Client c = new Client();
            c.setData(client);
            //check if user already exists

            if (!cman.isRegistered(c)) {
                r = cman.addClient(client);
                if (r)
                    output = "User created succesfully";
            } else {
                output = "This user is already registered please select a new username";
            }
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return output;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Tblclients> register(Tblclients client) {
        if (userlist == null)
            userlist = new HashSet<>();
        userlist.add(client);
        boolean r = cman.addClient(client);
        if (r)
            System.out.println("Client was created succesfully");
        return userlist;
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String login(Tblclients c){
        Client client=new Client();
        client.setData(c);
        boolean r = cman.authenticate(client);
        if(r)
            return "User is authenticated";
        else
            return "User is not allowed";
    }
}