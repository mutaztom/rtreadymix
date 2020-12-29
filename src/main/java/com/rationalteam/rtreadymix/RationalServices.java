package com.rationalteam.rtreadymix;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.reaymixcommon.MobileUser;
import com.rationalteam.reaymixcommon.News;
import com.rationalteam.reaymixcommon.ServerMessage;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rtreadymix.data.Tblclient;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
            //check if user already exists
            if (!c.isRegistered()) {
                r = cman.addClient(c);
                if (r) {
                    output.setMessage("User created successfully");
                    output.setDetails(c.getEmail());
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
            List<String> list = olist.stream().map(CService::getItem).collect(Collectors.toList());
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
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
    }

    @GET
    @Path("/getNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications() {
        List<News> notes = new ArrayList<>();
        notes.add(new News(" أسعار الخرسانة تنخفض بقدر كبير بعد رفع الحظر."));
        notes.add(new News(" بشرى سارة، نقدم لكم باقة من الخدمات في مجال فحص المواقع"));
        notes.add(new News("الآن يمكنكم التواصل معنا على الأرقام التالية: +249912352368"));
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
}