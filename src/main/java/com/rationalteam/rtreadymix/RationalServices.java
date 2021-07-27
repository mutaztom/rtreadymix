package com.rationalteam.rtreadymix;

import com.rationalteam.reaymixcommon.*;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rterp.erpcore.data.TblCurrency;
import com.rationalteam.rtreadymix.data.Tblclient;
import com.rationalteam.rtreadymix.data.Tblnews;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.panache.common.Parameters;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.redis.client.Request;
import org.jboss.resteasy.util.StringContextReplacement;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Path("/rtmix/")
public class RationalServices {
    @Inject
    ClientManager cman;
    @Inject
    EntityManager eman;
    @Inject
    NotificationServer server;
    @Inject
    EventBus bus;
    @Inject
    CommHub commHub;
    @Inject
    Mailer remail;

    @Context
    HttpServerRequest request;


    @PostConstruct
    public void init() {
        System.out.println(">>>> INITIALIZING DATASOURCE....");
        MezoDB.setEman(eman);
        DataManager.setEntityManager(eman);
    }

    private Set<Tblclient> userlist = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));


    @GET
    @Path("/getProfile/{clientid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@PathParam("clientid") String clientid) {
        try {
            Tblclient client = Tblclient.find("email=?1", clientid).firstResult();
            client.setPassword("**********");
            JsonObject jclient = JsonObject.mapFrom(client);
            String job = "None";
            if (client.getOccupation() != null && client.getOccupation() > 0)
                job = MezoDB.getItem(client.getOccupation(), "tbljob");
            jclient.put("occupation", job);
            if (client.getLocale() != null)
                jclient.put("locale", client.getLocale());
            return Response.ok(jclient).build();
        } catch (Exception exp) {
            Utility.ShowError(exp);
            return Response.serverError().entity(exp.getMessage()).build();
        }

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
            if (muser.getItem() == null || muser.getItem().isBlank()) {
                return new ServerMessage("User Name cannot be blank.");
            }
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
                    bus.send(IRationalEvents.RTEVENT_SIGNUP_PINSEND, c);
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
        COption st = new COption("tblstate");
        List<COption> olist = st.listOptions();
        List<String> list = olist.stream().map(COption::getItem).collect(Collectors.toList());
        return Response.ok(list).build();
    }

    @GET
    @Path("/getCities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCities() {
        System.out.println();
        MezoDB.setEman(eman);
        COption st = new COption("tblcity");
        List<COption> olist = st.listOptions();
        List<String> list = olist.stream().map(COption::getItem).collect(Collectors.toList());
        return Response.ok(list).build();
    }

    @GET
    @Path("/getCountries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCountries() {
        MezoDB.setEman(eman);
        COption st = new COption("tblcountry");
        List<COption> olist = st.listOptions();
        List<String> list = olist.stream().map(COption::getItem).collect(Collectors.toList());
        return Response.ok(list).build();
    }

    @GET
    @Path("/getProvencies")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getProvencies() {
        MezoDB.setEman(eman);
        COption st = new COption("tblprovince");
        List<COption> olist = st.listOptions();
        List<String> list = olist.stream().map(COption::getItem).collect(Collectors.toList());
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
            CProduct p = new CProduct();
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
            CProduct p = new CProduct();
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
            CService p = new CService();
            List<CService> olist = p.listAll();
            List<ClientService> serlist = new ArrayList<>();
            olist.forEach(s -> {
                ClientService cs = new ClientService();
                cs.setId(s.getId());
                cs.setItem(s.getItem());
                cs.setAritem(s.getDatasheet());
                cs.setUnit(s.getUnit());
                cs.setDescribtion(s.getDescription());
                cs.setCode(s.getCode());
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
            System.out.println(">> RECEIVED THIS ORDER .......");
            System.out.println(JsonObject.mapFrom(order));
            if (!Order.isComplete(order)) {
                System.out.println("incomplete order, please fill all fields.");
                return Response.serverError().entity(new ServerMessage("incomplete order, please fill all fields.")).build();
            }
            MezoDB.setEman(eman);

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
                bus.send(modifying ? IRationalEvents.RTEVENT_ORDER_MODIFIED : IRationalEvents.RTEVENT_NEWORDER, order);
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
            JsonArray jar = new JsonArray();
            if (!rtype.startsWith("tbl") && !show_tables.contains(rtype)) {
                rtype = "tbl" + rtype;
                if (!show_tables.contains(rtype))
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Table not found in databases: " + rtype).build();
            }
            COption option = new COption(rtype);
            List<COption> oplist = option.listOptions();
            JsonObject emptyOption = new JsonObject();
            emptyOption.put("id", -1).put("item", "None").put("aritem", "غير متوفر");
            jar.add(emptyOption);
            for (COption o :
                    oplist) {
                JsonObject job = new JsonObject();
                job.put("item", o.getItem()).put("aritem", o.getAritem());
                jar.add(job);
            }
            return Response.ok(jar).build();
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

    @POST
    @Path("/updateProfile/{clientid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateProfile(@Body JsonObject profile, @PathParam("clientid") String clientid) {
        try {
            Tblclient c = new Tblclient();
            ServerMessage smsg = new ServerMessage();
            System.out.println(profile.toString());
            boolean result = false;
            Client clnt = Client.findByEmail(clientid);
            if (clnt != null) {
                clnt.setItem(profile.getString("item"));

                System.out.println("old mobile:" + clnt.getMobile() + ",new mobile:" + profile.getString("mobile") + ".");
                if (profile.getString("mobile") != null) {
                    if (!profile.getString("mobile").equals(clnt.getMobile())) {
                        System.out.println("mobile is changed");
                        clnt.setVerfied(false);
                    }
                }
                clnt.setMobile(profile.getString("mobile"));
                String gndr = profile.getString("gender");
                if (gndr != null && gndr.isBlank())
                    clnt.setGender(enGender.valueOf(gndr));
                if (profile.getString("dislike") != null
                        && !profile.getString("dislike").isEmpty())
                    clnt.setDislike(profile.getString("dislike"));
                if (profile.getString("locale") != null && !profile.getString("locale").isBlank())
                    clnt.setLocale(profile.getString("locale"));
                clnt.setEmail(profile.getString("email"));
                String job = profile.getString("occupation");
                Long jobid = MezoDB.getItemID("tbljob", "item", job.stripLeading().stripTrailing());
                clnt.setOccupation(jobid.intValue());
                if (profile.getString("password") != null && !profile.getString("password").isBlank())
                    clnt.setPassword(profile.getString("password"));
                if (clnt.checkEntries()) {
                    result = clnt.save();
                }

                smsg.setMessage("OK");
                smsg.setDetails("CLient profile updated succesfully");
                return Response.ok(smsg).build();
            } else
                return Response.notModified("Could not verify client id").build();
        } catch (Exception ex) {
            Utility.ShowError(ex);
            return Response.notModified(ex.getMessage()).build();
        }
    }

    @GET
    @Path("/getNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getNotifications() {
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
    @Path("/verifyPin/{pincode}/{clientid}/{media}")
    @Transactional
    public Response verifyPin(@PathParam("pincode") String pincode, @PathParam("clientid") String clientid,
                              @PathParam("media") String media) {
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
                if (media != null && !media.isBlank()) {
                    enCommMedia commMedia = enCommMedia.valueOf(media.toUpperCase());
                    c.setVerifyMedia(commMedia);
                }
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
    @Path("/regenPin/{clientid}/{media}")
    @Transactional
    public Response regenPin(@PathParam("clientid") String clientid, @PathParam("media") String media) {
        ServerMessage output = new ServerMessage();
        try {
            if (clientid == null || clientid.isBlank()) {
                output.setMessage("Parameters of regenPin cannot be null!");
                output.setDetails("clientid=" + clientid);
                return Response.ok(output).build();
            }
            Client c = Client.findByEmail(clientid);
            ///Manage Media type for verification
            if (media == null || media.isBlank()) {
                media = enCommMedia.SMS.name();
            }
            enCommMedia commMedia = com.rationalteam.rtreadymix.enCommMedia.valueOf(media.toUpperCase());
            //////
            if (c != null) {
                if (c.isVerfied()) {
                    output.setMessage("Client has already been verified, pin regeneration is prohibited.");
                    return Response.ok(output).build();
                }
                //does client does not have pin code
                if (c.getPincode() == null || c.getPincode().isBlank()) {
                    c.setPincode(cman.generatePin(c));
                    c.save();
                }
                c.setVerifyMedia(commMedia);
                bus.send(IRationalEvents.RTEVENT_SIGNUP_PINSEND, c);
                output.setMessage("Pin code is sent via " + commMedia.name() + " please check and verify.");
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
            output.setMessage(delteted ? "Order " + orderid + " deleted successfully" : "Order " + orderid + " was not deleted");
            bus.send(IRationalEvents.RTEVENT_ORDER_CANCELED, order);
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

    @Path("/getTemplate/{commmedia}/{fpath}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTemplate(@PathParam("fpath") String fpath, @PathParam("commmedia") String cmedia) {
        try {
            enCommMedia cm = com.rationalteam.rtreadymix.enCommMedia.valueOf(cmedia.toUpperCase());
            String subfolder = cm.name().toLowerCase();
            java.nio.file.Path path = Paths.get(SystemConfig.TEMPLATE, subfolder, fpath);
            String message = Files.readString(path);
            return Response.ok(message).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @Path("/deleteTemplate/{commmedia}/{fpath}/")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("admin")
    public Response deleteTemplate(@PathParam("fpath") String fpath, @PathParam("commmedia") String cmedia) {
        try {
            enCommMedia cm = com.rationalteam.rtreadymix.enCommMedia.valueOf(cmedia.toUpperCase());
            String subfolder = cm.name().toLowerCase();
            java.nio.file.Path path = Paths.get(SystemConfig.TEMPLATE, subfolder, fpath);
            if (Files.exists(path))
                Files.delete(path);
            return Response.ok("OK").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "err:" + e.getMessage()).build();
        }
    }

    @Path("/deleteCurrency/{currid}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("admin")
    @Transactional
    public Response deleteCurrency(@PathParam("currid") Integer currid) {
        try {
            //check if is main currency
            Integer main = MezoDB.getInteger("select ismain from tblcurrency where id=" + currid);
            if (main > 0)
                return Response.ok("error:Can't delete system's main currency.").build();

            boolean r = MezoDB.doSqlIn("delete from tblcurrency where id=" + currid);
            return Response.ok(r ? "OK" : "error:could not delete currency").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "err:" + e.getMessage()).build();
        }
    }

    @Path("/saveTemplate/{commmedia}/{fpath}/")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @RolesAllowed("admin")
    public Response saveTemplate(@PathParam("fpath") String fpath, @PathParam("commmedia") String cmedia, String
            body) {
        try {
            enCommMedia cm = com.rationalteam.rtreadymix.enCommMedia.valueOf(cmedia.toUpperCase());
            String subfolder = cm.name().toLowerCase();
            java.nio.file.Path path = Paths.get(SystemConfig.TEMPLATE, subfolder, fpath);
            Files.writeString(path, body, path.toFile().exists() ? StandardOpenOption.WRITE : StandardOpenOption.CREATE_NEW);
            return Response.ok("OK").build();
        } catch (Exception e) {
            Utility.ShowError(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @Path("/saveProp")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response saveProp(Map<String, String> body) {
        try {
            if (body == null || body.isEmpty())
                return Response.ok("Properties cant be null or empty").build();
            Properties prop = new Properties();
            Properties p = new Properties();
            File f = new File(SystemConfig.PROPFILE);
            FileOutputStream fs = new FileOutputStream(f);
            body.forEach(p::putIfAbsent);
            p.store(fs, "Updated By RedyMix on: " + LocalDateTime.now().format(DateTimeFormatter.ISO_ORDINAL_DATE));
            fs.close();
            return Response.ok("OK").build();
        } catch (Exception e) {
            Utility.ShowError(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @Path("/saveCurrency")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    @Transactional
    public Response saveCurrency(CCurrency body) {
        try {
            if (body == null || body.isEmpty())
                return Response.ok("Currency cant be null or empty").build();
            System.out.println(body);
            TblCurrency tcur = (TblCurrency) body.getData();
            if (tcur.getId() == null || tcur.getId() < 0) {
                tcur.setId(null);
                body.getFacade().create(tcur);
            } else
                body.getFacade().edit(tcur);
            return Response.ok("OK").build();
        } catch (Exception e) {
            Utility.ShowError(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @Path("/monthlyOrders")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getMonthlyOrders(@QueryParam("clientid") Integer clientid, @QueryParam("year") Integer year) {
        Map<Integer, Double> data = new HashMap<>();
        try {
            List list = MezoDB.Open("select month(ondate),sum(tblorder.quantity) from tblorder " +
                    "where clientid=" + clientid + " and year(ondate)="
                    + year + " group by month(ondate)");
            if (list != null && !list.isEmpty()) {
                list.forEach(t -> {
                    Object[] rec = (Object[]) t;
                    data.put(Integer.valueOf(rec[0].toString()), Double.valueOf(rec[1].toString()));
                });
            }
            return Response.ok(data).build();
        } catch (
                Exception e) {
            Utility.ShowError(e);
            Map.Entry<Integer, Double> empty = new AbstractMap.SimpleEntry<Integer, Double>(-1, -1D);
            return Response.ok("Error:" + e.getMessage()).build();
        }
    }

    @Path("/addprop/{keyval}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProp(@PathParam("keyval") String keyval) {
        try {
            System.out.println("Property obtained as param: " + keyval);
            UtilityExt.addProperty(keyval);
            ServerMessage sm = new ServerMessage("OK:property added successfully");
            return Response.ok(sm).build();
        } catch (Exception e) {
            Utility.ShowError(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Error:" + e.getMessage()).build();
        }
    }

    @Path("/sendalert")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response sendAlert(@Body JsonObject body) {
        try {
            System.out.println("received message: " + body);
            server.pushNews(Integer.parseInt(body.getString("clientid")), "Admin Message", body.getString("message"));
            return Response.ok("success").build();
        } catch (Exception exception) {
            System.out.println(exception);
            ServerMessage sm = new ServerMessage("error", exception.getMessage());
            return Response.ok(sm).status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("/sendMail")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response sendMail(@Body JsonObject json) {
        try {
            System.out.println(json);
            String msg = json.getString("message");
            String mailto = json.getString("mailto");
            remail.send(Mail.withText(mailto, "Admin Message", msg));
            System.out.println("Email sent successfully");
            return Response.ok(new ServerMessage("Mail sent successfully")).build();
        } catch (Exception exp) {
            System.out.println("Error Sending Mail: " + exp);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exp.getMessage()).build();
        }
    }

    @GET
    @Path("/getclientnotes/{clientid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientNotes(@PathParam("clientid") String clntemail) {
        List<News> notes = new ArrayList<>();
        Integer clientid = cman.getClientid(clntemail);
        List<Tblnews> news = MezoDB.open("select * from tblnews where clientid=" + clientid + " order by id desc  limit 5", Tblnews.class);
        if (news != null)
            for (Tblnews n :
                    news) {
                News nws = new News(n.getItem(), n.getDetails());
                nws.setClientid(n.getClientid());
                notes.add(nws);
            }
        return Response.ok(notes).build();
    }

    @POST
    @Path("/pwdreset/{clientid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPassword(@PathParam("clientid") String clientid) {
        try {
            boolean a = cman.isAuthentic(clientid);
            if (!a)
                return Response.status(Response.Status.FORBIDDEN.getStatusCode()).build();
            Client client = Client.findByEmail(clientid);
            bus.publish(IRationalEvents.RTEVENT_PASSWORD_RESET, client);
            return Response.ok().build();
        } catch (Exception exp) {
            Utility.ShowError(exp);
            return Response.serverError().entity(exp.getMessage()).build();
        }
    }
}