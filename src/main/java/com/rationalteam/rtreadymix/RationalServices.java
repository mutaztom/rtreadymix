package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rtreadymix.data.Tblclient;


import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Path("/rtmix/")
public class RationalServices {
    @Inject
    ClientManager cman;
    @Inject
    EntityManager eman;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("hello")
    public String hello() {
        return "hello";
    }

    private Set<Tblclient> userlist = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));


    @GET
    @Path("/getclients")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tblclient> getClients() {
        List<Tblclient> list = new ArrayList<>();
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
    public Response signup(Tblclient client) {
        String output = "No thing was processed";
        try {
            boolean r;
            System.out.println("Recieved json object is as follows: " + client.toString());
            Client c = new Client();
            c.setData(client);
            System.out.println(output);
            //check if user already exists
            if (!cman.isRegistered(c)) {
                r = cman.addClient(client);
                if (r)
                    output = "User created succesfully";
            } else {
                output = "This user is already registered please select a new username";
            }
            System.out.println(output);
        } catch (Exception e) {
            Utility.ShowError(e);
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
        return Response.accepted().status(Response.Status.OK.getStatusCode(), output).build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Tblclient> register(Tblclient client) {
        if (userlist == null)
            userlist = new HashSet<>();
        userlist.add(client);
        boolean r = cman.addClient(client);
        if (r)
            System.out.println("Client was created succesfully");
        return userlist;
    }

    @POST
    @Path("/login/{email}/{pwd}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(@PathParam("email") String email, @PathParam("pwd") String pwd) {
        if (Objects.isNull(email) || Objects.isNull("pwd"))
            return Response.serverError().status(Response.Status.EXPECTATION_FAILED.getStatusCode(), "User name and password must not be empty").build();
       try {
           MezoDB.setEman(eman);
           int cid = MezoDB.getInteger("select id from tblclient where email='" +email + "' and password='" + pwd + "'");
           boolean r = cid > 0;
           if (r)
               return Response.ok("User is authenticated").build();
           else {
               return Response.status(401, "No such user.").build();
           }
       }catch(Exception e){
           return   Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
       }
    }

    @GET
    @Path("/getStates/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getSates() {
        MezoDB.setEman(eman);
        OptionLocal st = new COption("tblstate");
        List<OptionLocal> olist = st.listOptions();
        List<String> list = olist.stream().map(OptionLocal::getItem).collect(Collectors.toList());
        return list;
    }

    @GET
    @Path("/getCities")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getCities() {
        MezoDB.setEman(eman);
        OptionLocal st = new COption("tblcity");
        List<OptionLocal> olist = st.listOptions();
        List<String> list = olist.stream().map(OptionLocal::getItem).collect(Collectors.toList());
        return list;
    }

    @GET
    @Path("/getCountries")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getCountries() {
        MezoDB.setEman(eman);
        OptionLocal st = new COption("tblccountry");
        List<OptionLocal> olist = st.listOptions();
        List<String> list = olist.stream().map(OptionLocal::getItem).collect(Collectors.toList());
        return list;
    }

    @GET
    @Path("/getProducts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getProducts() {
        MezoDB.setEman(eman);
        ProductLocal p = new CProduct();
        List<ProductLocal> olist = p.listAllItems();
        List<String> list = olist.stream().map(ProductLocal::getItem).collect(Collectors.toList());
        return list;
    }

    @GET
    @Path("/getServices")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getServices() {
        MezoDB.setEman(eman);
        ServiceLocal p = new CService();
        List<ServiceLocal> olist = p.listAllItems();
        List<String> list = olist.stream().map(ServiceLocal::getItem).collect(Collectors.toList());
        return list;
    }

    @POST
    @Path("/placeOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response placeOrder(Order order) {
        if (order == null) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Order cannot be null!").build();
        }
        return Response.ok().build();
    }
}