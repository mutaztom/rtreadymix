package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rtreadymix.data.Tblclient;
import com.rationalteam.rtreadymix.data.Tblorder;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
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
    SubscriptionServer server = new SubscriptionServer();

    @PostConstruct
    public void init() {
        System.out.println(">>>> INITIALIZING DATASOURCE....");
        MezoDB.setEman(eman);
        DataManager.setEntityManager(eman);
    }

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
            int cid = MezoDB.getInteger("select id from tblclient where email='" + email + "' and password='" + pwd + "'");
            boolean r = cid > 0;
            if (r)
                return Response.ok("User is authenticated").build();
            else {
                return Response.status(401, "No such user.").build();
            }
        } catch (Exception e) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/getStates/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSates() {
        MezoDB.setEman(eman);
        OptionLocal st = new COption("tblstate");
        List<OptionLocal> olist = st.listOptions();
        List<String> list = olist.stream().map(OptionLocal::getItem).collect(Collectors.toList());
        return Response.ok(list).build();
    }

    @GET
    @Path("/getCities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCities() {
        MezoDB.setEman(eman);
        OptionLocal st = new COption("tblcity");
        List<OptionLocal> olist = st.listOptions();
        List<String> list = olist.stream().map(OptionLocal::getItem).collect(Collectors.toList());
        return Response.ok(list).build();
    }

    @GET
    @Path("/getCountries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCountries() {
        MezoDB.setEman(eman);
        OptionLocal st = new COption("tblcountry");
        List<OptionLocal> olist = st.listOptions();
        List<String> list = olist.stream().map(OptionLocal::getItem).collect(Collectors.toList());
        return Response.ok(list).build();
    }

    @GET
    @Path("/getProvencies")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProvencies() {
        MezoDB.setEman(eman);
        OptionLocal st = new COption("tblprovince");
        List<OptionLocal> olist = st.listOptions();
        List<String> list = olist.stream().map(OptionLocal::getItem).collect(Collectors.toList());
        return Response.ok(list).build();
    }

    @GET
    @Path("/getProducts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts() {
//        ProductLocal p = new CProduct();
//        List<ProductLocal> olist = p.listAllItems();
//        List<String> list = olist.stream().map(ProductLocal::getItem).collect(Collectors.toList());
        List<String> list = new ArrayList<>();
        String[] prods = new String[]{"C10", "C15", "C20", "C25", "C30", "C35", "C40"};
        for (String p :
                prods) {
            list.add(p);
        }
        return Response.ok(list).build();
    }

    @GET
    @Path("/getServices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServices() {
//        MezoDB.setEman(eman);
//        ServiceLocal p = new CService();
//        List<ServiceLocal> olist = p.listAllItems();
//        List<String> list = olist.stream().map(ServiceLocal::getItem).collect(Collectors.toList());
        List<String> list = new ArrayList<>();
        String[] prods = new String[]{"ReadyMix", "Site Readyness Inspectin", "Concreate Inspection", "Soil Inpsectoin", "Other.."};
        for (String p :
                prods) {
            list.add(p);
        }
        return Response.ok(list).build();
    }

    @POST
    @Path("/placeOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response placeOrder(ClientOrder order) {
        try {
            if (order == null) {
                //send sms to confirm recieval
                return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Order cannot be null!").build();
            }
            MezoDB.setEman(eman);
            DataManager.setEntityManager(eman);
            //save order to our database
            Order inorder = new Order();
            inorder.fromClientOrder(order);
            boolean r = inorder.save();
            if (r) {
//                server.confirmOrder(order, enCommMedia.SMS);
                server.confirmOrder(order, enCommMedia.EMAIL);
                return Response.ok("Received order from client: " + order.getClientid() + " Notes:" + order.getNotes()).build();
            } else
                return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Cant' accept order now.").build();
        } catch (Exception e) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/getLookup/{rtype}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLookup(@PathParam("rtype") String rtype) {
        try {
            MezoDB.setEman(eman);
            List show_tables = MezoDB.Open("show tables");

            List<String> list;
            if (!rtype.startsWith("tbl") && !show_tables.contains(rtype)) {
                rtype = "tbl" + rtype;
                if (!show_tables.contains(rtype))
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Table not found in databases: " + rtype).build();
            }
            COption option = new COption(rtype);
            List<OptionLocal> oplist = option.listOptions();
            list = oplist.stream().map(OptionLocal::getItem).collect(Collectors.toList());
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/getOrders/{clientid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders(@PathParam("clientid") String clientid) {
        long clid = MezoDB.getItemID(Tblclient.class.getSimpleName(), "email", clientid);
        if(clid<=0)
            return Response.status(Response.Status.FORBIDDEN.getStatusCode(),"You are not allowed access this service").build();
        Order p = new Order();
        Map<String, Object> map = new HashMap<>();
        map.put("clientid", clid);
        List<Order> olist = p.filter(map);
        List<ClientOrder> list = olist.stream().map(Order::toClientOrder).collect(Collectors.toList());
        list.sort((o1, o2) -> o1.getId()>o2.getId()?1:0);
        return Response.ok(list).build();
    }

}