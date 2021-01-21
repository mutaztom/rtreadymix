package com.rationalteam.rtreadymix;

import com.rationalteam.reaymixcommon.*;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rtreadymix.data.Tblclient;
import com.rationalteam.rtreadymix.data.Tblnews;
import com.rationalteam.rtreadymix.data.Tblorder;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.*;
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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/signup/")
    @Transactional
    public ServerMessage signup(MobileUser muser) {
        ServerMessage output = new ServerMessage();
        output.setMessage("Nothing was processed");
        try {
            boolean r;
            System.out.println("Received json object is as follows: " + muser.toString());
            Client c = new Client();
            c.fromMobileUser(muser);
            //check if name is used
            if (c.isNameUsed()) {
                output.setMessage("Name of account holder '" + c.getItem() + "' is used, please type a new one.");
                return output;
            }
            //check if user already exists
            if (!c.isRegistered()) {
                r = cman.addClient(c);
                if (r) {
                    output.setMessage("User created successfully");
                    output.setDetails("VERIFY");
                    //send verification sms
                    CommHub.sendSMS(c.getMobile(), c.getPincode());
                    //return
                    return output;
                }
            } else {
                output.setMessage("This user is already registered please select a new username");
            }
            System.out.println(output);
        } catch (Exception e) {
            Utility.ShowError(e);
            output.setMessage(e.getMessage());
            return output;
        }
        return output;
    }


    @POST
    @Path("/login/{email}/{pwd}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@PathParam("email") String email, @PathParam("pwd") String pwd) {
        ServerMessage output = new ServerMessage();
        if (Objects.isNull(email) || Objects.isNull("pwd")) {
            output.setMessage("User name and password must not be empty");
            return Response.serverError().status(Response.Status.EXPECTATION_FAILED.getStatusCode(), output.getMessage()).build();
        }
        try {
            MezoDB.setEman(eman);
            int cid = MezoDB.getInteger("select id from tblclient where email='" + email + "' and password='" + pwd + "'");
            boolean r = cid > 0;
            if (r) {
                Client client = new Client();
                client.find(cid);
                if (client.isVerfied()) {
                    output.setMessage("User is authenticated");
                    return Response.ok(output).build();
                } else {
                    output.setMessage("Client is not yet verified please verify your account first.");
                    output.setDetails("VERIFY");
                    return Response.status(Response.Status.UNAUTHORIZED.getStatusCode(), output.getMessage()).build();
                }
            } else {
                output.setMessage("No such user.");
                output.setDetails("REGISTER");
                return Response.status(Response.Status.FORBIDDEN.getStatusCode(), output.getMessage()).build();
            }
        } catch (Exception e) {
            output.setMessage(e.getMessage());
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), output.getMessage()).build();
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
    @Transactional
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
    @Transactional
    public Response getProducts() {
        try {
            MezoDB.setEman(eman);
            DataManager.setEntityManager(eman);
            ProductLocal p = new CProduct();
            List<CProduct> olist = p.listAll();
            List<String> list = olist.stream().map(CProduct::getItem).collect(Collectors.toList());
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/getPriceList")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getPriceList() {
        try {
            MezoDB.setEman(eman);
            DataManager.setEntityManager(eman);
            ProductLocal p = new CProduct();
            List<CProduct> olist = p.listAll();
            Map<String, Double> pricelist = olist.stream().collect(Collectors.toMap(
                    CProduct::getItem, CProduct::getUnitPrice));
            return Response.ok(pricelist).build();
        } catch (Exception e) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/getServices")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getServices() {
        try {
            MezoDB.setEman(eman);
            ServiceLocal p = new CService();
            List<CService> olist = p.listAll();
            List<ClientService> serlist = new ArrayList<>();
            olist.forEach(s -> {
                ClientService cs = new ClientService();
                cs.setId(s.getId());
                cs.setItem(s.getItem());
                cs.setUnit(s.getUnit());
                cs.setDescribtion(s.getDescription());
                cs.setUnitprice(s.getUnitPrice());
                serlist.add(cs);
            });

            return Response.ok(serlist).build();
        } catch (Exception e) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @Path("/placeOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response placeOrder(ClientOrder order) {
        try {
            ServerMessage output = new ServerMessage();
            if (order == null) {
                return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Order cannot be null!").build();
            }
            MezoDB.setEman(eman);
            System.out.println(">> RECEIVED THIS ORDER .......");
            System.out.println(JsonObject.mapFrom(order));
            DataManager.setEntityManager(eman);
            if (!cman.isAuthentic(order.getClientid()))
                return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "You are not allowed to place orders in this server.").build();
            //save order to our database
            Order inorder = new Order();
            boolean modifying = order.getId() != null;
            System.out.println("ORder id=" + order.getId() + " are we modifying? " + modifying);
            if (order.getId() != null)
                modifying = MezoDB.getInteger("select id from tblorder where id=" + order.getId()) > 0;
            //what to do if order is not in our database
            //TODO: discuss this later to decide but for now we will create new
            if (modifying) {
                inorder.find(order.getId());
                System.out.println("Modifying");
            }
            inorder.fromClientOrder(order);
            boolean r = inorder.save();
            if (r) {
                //This part to inform person who placed order
                //server.confirmOrder(order, enCommMedia.SMS);

                boolean b = server.confirmOrder(order, enCommMedia.EMAIL, cman.getMobile(order.getClientid()));
                //make notifications to staff
                server.notifyStaff();
                output.setMessage("Received order " + (modifying ? "modification" : "") + " from client: " + order.getClientid() + " Notes:" + order.getNotes());
                return Response.ok(output).build();
            } else {
                output.setMessage("Could not save order, an unknown exception is thrown.");
                Utility.ShowError(output.getMessage());
                return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), output.getMessage()).build();
            }
        } catch (Exception e) {
            Utility.ShowError(e);
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/getLookup/{rtype}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
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
        try {
            MezoDB.setEman(eman);
            int clid = MezoDB.getInteger("select id from tblclient where email='" + clientid + "'");
            if (clid <= 0)
                return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "You are not allowed access this service").build();
            Order p = new Order();
            Map<String, Object> map = new HashMap<>();
            map.put("clientid", clid);
            List<Order> olist = p.filter(map);
            List<ClientOrder> list = olist.stream().map(Order::toClientOrder).collect(Collectors.toList());
            list.sort((o1, o2) -> o1.getId() > o2.getId() ? 1 : 0);
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/getOrder/{clientid}/{orderid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrder(@PathParam("clientid") String clientid, @PathParam("orderid") Integer orderid) {
        try {
            MezoDB.setEman(eman);
            if (!cman.isAuthentic(clientid))
                return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "You are not allowed access this service").build();
            Order p = new Order();
            Client client = Client.findByEmail(clientid);
            boolean exists = (MezoDB.getInteger("select id from tblorder where id=" + orderid + " and clientid=" + client.getId())) > 0;
            if (!exists)
                return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "Order Not found").build();
            p.find(orderid);
            if (!p.isEmpty()) {
                return Response.ok(p.toClientOrder()).build();
            } else
                return Response.ok("Order Not Found").build();
        } catch (Exception e) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/getNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getNotifications() {
        MezoDB.setEman(eman);
        List<News> notes = new ArrayList<>();
        List<Tblnews> news = MezoDB.open("select * from tblnews order by id desc  limit 5 ", Tblnews.class);
        if (news != null)
            for (Tblnews n :
                    news) {
                notes.add(new News(n.getItem(), n.getDetails()));
            }
        return Response.ok(notes).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/verifyPin/{pincode}/{clientid}")
    @Transactional
    public Response verifyPin(@PathParam("pincode") String pincode, @PathParam("clientid") String clientid) {
        try {
            MezoDB.setEman(eman);
            ServerMessage output = new ServerMessage();
            if (clientid == null || clientid.isBlank() || pincode == null || pincode.isBlank()) {
                output.setMessage("Parameters of pin verification cannot be null!");
                output.setDetails("Pincode= " + pincode + ", clientid=" + clientid);
                return Response.serverError().entity(output).build();
            }
            Client c = Client.findByEmail(clientid);
            if (c != null) {
                if (c.getPincode().equals(pincode)) {
                    output.setMessage("Pin code verification was successful, Enjoy our app.");
                    c.setVerfied(true);
                    c.save();
                    return Response.ok(output).build();
                } else {
                    output.setMessage("Pin code of this client does not match generated one. Please contact Technical Support.");
                    return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(output).build();
                }
            } else {
                output.setMessage("Could not verify pin code, Client not registered: " + clientid + ". Please contact Technical Support.");
                return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(output).build();
            }

        } catch (Exception exp) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exp.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/regenPin/{clientid}")
    @Transactional
    public Response regenPin(@PathParam("clientid") String clientid) {
        ServerMessage output = new ServerMessage();
        try {
            if (clientid == null || clientid.isBlank()) {
                output.setMessage("Parameters of regenPin cannot be null!");
                output.setDetails("clientid=" + clientid);
                return Response.serverError().entity(output).build();
            }
            Client c = Client.findByEmail(clientid);
            if (c != null) {
                if (c.isVerfied()) {
                    output.setMessage("Client has already been verified, pin regeneration is prohibited.");
                    return Response.serverError().entity(output).build();
                }
                c.setPincode(cman.generatePin(c));
                c.save();
                CommHub.sendSMS(c.getMobile(), c.getPincode());
                output.setMessage("Pin code is sent via SMS please check and verify.");
            }
            return Response.ok(output).build();
        } catch (Exception exp) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exp.getMessage()).build();
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deleteOrder/{clientid}/{orderid}")
    @Transactional
    public Response deleteOrder(@PathParam("clientid") String clientid, @PathParam("orderid") Integer orderid) {
        try {
            MezoDB.setEman(eman);
            ServerMessage output = new ServerMessage("No order was deleted");
            if (!cman.isAuthentic(clientid))
                return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "You are not allowed to use this service").build();
            if (orderid == null || orderid <= 0)
                return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "You are not allowed to use this service").build();
            Order order = new Order();
            boolean delteted = order.delete(orderid);
            output.setMessage(delteted ? "Order " + orderid + " deleted succesfully" : "Order " + orderid + " was not deleted");
            return Response.ok(output).build();
        } catch (Exception e) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("/getPrice/{itemid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getPrice(@PathParam("itemid") Integer itemid) {
        try {
            ServerMessage output = new ServerMessage();
            Object v = MezoDB.getValue("select unitprice from tblproduct where id=" + itemid);
            Double price = v != null ? Double.valueOf(v.toString()) : 0D;
            JsonObject j = new JsonObject();
            j.put("price", price);
            return Response.ok(j).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @Path("/getTemplate/{fpath}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)

    public Response getTemplate(@PathParam("fpath") String fpath) {
        try {
            Template template = Velocity.getTemplate(fpath);
            StringWriter message = new StringWriter();
            VelocityContext context = new VelocityContext();
            template.merge(context, message);
            return Response.ok(message).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }
}