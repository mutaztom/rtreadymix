package com.rationalteam.rtreadymix;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.reaymixcommon.News;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rterp.sales.Subscription;
import com.rationalteam.rtreadymix.data.Tblnews;
import io.quarkus.vertx.ConsumeEvent;
import io.reactivex.Completable;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@ApplicationScoped
public class NotificationServer {
    Subscription subs;
    @Inject
    CommHub commHub;

    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    private String DEFAULTEMAIL = "mutaztom@gmail.com";
    private static String DEFAULTMOBILE = "+249912352368";
    private boolean NotifyEmail;
    private boolean NotifySMS;
    private String ADMINEMAIL;
    private String ADMINMOBILE;


    public NotificationServer() {
        initTemplatePath(enCommMedia.EMAIL);
    }

    public void initTemplatePath(enCommMedia media) {
        Path path = Paths.get(SystemConfig.TEMPLATE, media.name().toLowerCase());
        Properties p = new Properties();
        p.setProperty("resource.loader.file.path", path.toString());
        Velocity.init(p);
        ADMINEMAIL = UtilityExt.getProperty("adminmail") != null ? UtilityExt.getProperty("adminmail") : DEFAULTEMAIL;
        ADMINMOBILE = UtilityExt.getProperty("adminmobile") != null ? UtilityExt.getProperty("adminmobile") : DEFAULTMOBILE;
        NotifyEmail = Boolean.parseBoolean(Utility.getProperty("notify.email"));
        NotifySMS = Boolean.parseBoolean(Utility.getProperty("notify.sms"));
    }

    public List<Subscription> getDue() {
        try {
            Map<Integer, Object> map = new HashMap<>();
            Date date = new Date();
            String sql = "select * from tblsubscribtion where datediff(enddate,current_date) between 1 and notifybefore and mobile is not null and mobile <>''";
            subs = new Subscription();
            List expired = subs.listNative(sql, map);
            return expired;
        } catch (Exception exp) {
            Utility.ShowError(exp);
            return new ArrayList<>();
        }
    }

    public List<Subscription> getNew() {
        try {
            Map<Integer, Object> map = new HashMap<>();
            Date date = new Date();
            String sql = "select * from tblsubscribtion where datediff(ondate,current_date) between 1 and 3 and mobile is not null and mobile <>''";
            subs = new Subscription();
            List expired = subs.listNative(sql, map);
            return expired;
        } catch (Exception exp) {
            Utility.ShowError(exp);
            return new ArrayList<>();
        }
    }

    public void notifyCustomers(enCommMedia media) {
        if (media == null)
            throw new RuntimeException("Media parameter must be specified sms or email");
        if (media.equals(enCommMedia.SMS)) {
            List<Subscription> due = getDue();
//            //filter those with mobile number associated to it
            List<Subscription> hasmobile = due.stream().filter(f -> f.getMobile() != null).collect(Collectors.toList());
            //build sms content, and due to the difference in content of the sms it is difficult to send as bulk
            VelocityContext context = new VelocityContext();
            initTemplatePath(media);
            Template template = Velocity.getTemplate("subsnotify.txt");
            for (Subscription s :
                    due) {
                context.put("service", s.getItem());
                context.put("days", s.getDaysToExpire());
                StringWriter message = new StringWriter();
                template.merge(context, message);
                System.out.println(s.getMobile() + " " + message);
                System.out.println(">>>>>>>>>>>>>>>>>>>>");
                if (s.getMobile() != null && !s.getMobile().isEmpty()) {
                    boolean b = commHub.sendSMS(s.getMobile(), message.toString());
                    if (b)
                        Utility.ShowSuccess("Message Sent succesfully");
                    else
                        Utility.ShowError("Could not send message");
                }
            }
        } else if (media.equals(enCommMedia.EMAIL)) {
            initTemplatePath(media);
            VelocityContext context = new VelocityContext();
            Template template = Velocity.getTemplate("subsnotify.txt");
            List<Subscription> hasemail = getDue().stream().filter(f -> f.getEmail() != null).collect(Collectors.toList());
            int c = 0;
            for (Subscription s :
                    hasemail) {
                context.put("customer", MezoDB.getItem(s.getCustomer(), "tblcustomer"));
                context.put("days", s.getDaysToExpire());
                context.put("service", s.getItem());
                context.put("sender", "RationalTeam Robot");
                StringWriter message = new StringWriter();
                template.merge(context, message);
                System.out.println(message.toString());
                if (c == 0)
                    commHub.sendEMail(message.toString(), "mutaztom@gmail.com");
                c++;

            }
        }

    }

    public void sendBulkWlecome(enCommMedia media) {
        if (media == null)
            throw new RuntimeException("Media parameter must be specified sms or email");

        if (media.equals(enCommMedia.SMS)) {
            List<Subscription> due = getNew();
//            //filter those with mobile number associated to it
            List<Subscription> hasmobile = due.stream().filter(f -> f.getMobile() != null).collect(Collectors.toList());
            //build sms content, and due to the difference in content of the sms it is difficult to send as bulk
            VelocityContext context = new VelocityContext();
            initTemplatePath(media);

            Template template = Velocity.getTemplate("subswelcome.txt");
            for (Subscription s :
                    due) {
                context.put("service", s.getItem());
                context.put("days", s.getDaysToExpire());
                context.put("startdate", df.format(s.getPeriod().getStart()));
                context.put("enddate", df.format(s.getPeriod().getEnd()));
                StringWriter message = new StringWriter();
                template.merge(context, message);
                System.out.println(s.getMobile() + " " + message);
                System.out.println(">>>>>>>>>>>>>>>>>>>>");
                if (s.getMobile() != null && !s.getMobile().isEmpty()) {
                    boolean b = commHub.sendSMS(s.getMobile(), message.toString());
                    if (b)
                        Utility.ShowSuccess("Message Sent succesfully");
                    else
                        Utility.ShowError("Could not send message");
                }
            }
        } else if (media.equals(enCommMedia.EMAIL)) {
            initTemplatePath(media);
            VelocityContext context = new VelocityContext();
            Template template = Velocity.getTemplate("subswelcome.txt");
            List<Subscription> hasemail = getDue().stream().filter(f -> f.getEmail() != null).collect(Collectors.toList());
            int c = 0;
            for (Subscription s :
                    hasemail) {
                context.put("customer", MezoDB.getItem(s.getCustomer(), "tblcustomer"));
                context.put("days", s.getDaysToExpire());
                context.put("service", s.getItem());
                context.put("startdate", df.format(s.getPeriod().getStart()));
                context.put("enddate", df.format(s.getPeriod().getEnd()));
                context.put("sender", "RationalTeam Robot");
                StringWriter message = new StringWriter();
                template.merge(context, message);
                System.out.println(message.toString());
                if (c == 0)
                    commHub.sendEMail(message.toString(), "mutaztom@gmail.com");
                c++;

            }
        }

    }

    public boolean sendWelcome(Subscription s, enCommMedia media) {
        boolean b = false;
        try {
            VelocityContext context = new VelocityContext();
            initTemplatePath(media);
            Template template = Velocity.getTemplate("subswelcome.txt");
            if (media == enCommMedia.SMS) {
                context.put("service", s.getItem());
                context.put("days", s.getDaysToExpire());
                context.put("startdate", df.format(s.getPeriod().getStart().getTime()));
                context.put("enddate", df.format(s.getPeriod().getEnd().getTime()));
                StringWriter message = new StringWriter();
                template.merge(context, message);
                System.out.println(s.getMobile() + " " + message);
                System.out.println(">>>>>>>>>>>>>>>>>>>>");
                if (s.getMobile() != null && !s.getMobile().isEmpty()) {
                    b = commHub.sendSMS(s.getMobile(), message.toString());
                    if (b)
                        Utility.ShowSuccess("Message Sent succesfully");
                    else
                        Utility.ShowError("Could not send message");
                }
            } else {
                if (s.getEmail() == null || s.getEmail().isEmpty())
                    throw new RuntimeException();
                context.put("customer", MezoDB.getItem(s.getCustomer(), "tblcustomer"));
                context.put("days", s.getDaysToExpire());
                context.put("service", s.getItem());
                context.put("startdate", df.format(s.getPeriod().getStart()));
                context.put("enddate", df.format(s.getPeriod().getEnd()));
                context.put("sender", "RationalTeam Robot");
                StringWriter message = new StringWriter();
                template.merge(context, message);
                System.out.println(message.toString());
            }
        } catch (
                Exception exp) {
            throw new RuntimeException(exp);
        }
        return b;
    }

    public Subscription getSub() {
        return subs;
    }

    public void setSub(Subscription sub) {
        this.subs = sub;
    }

    public boolean confirmOrder(ClientOrder s, enCommMedia media, String mobile) {
        boolean b = false;
        try {
            VelocityContext context = new VelocityContext();
            initTemplatePath(media);
            Template template = Velocity.getTemplate("placeorder.txt");
            StringBuilder message = new StringBuilder();
            JsonObject jorder = JsonObject.mapFrom(s);
            boolean modifying = s.getId() != null && s.getId() > 0;
            message.append(modifying ? "Order modified on " : "New order was placed").append(" ON ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_ORDINAL_DATE));
            message.append("\n").append("from: ").append(s.getClientid()).append("\n")
                    .append(jorder.toString())
                    .append(" onmap: ").append("https://www.google.com/maps/@").append(s.getLocation())
                    .append("Mobile: ").append(mobile);
            if (media == enCommMedia.SMS) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>");
//                if (s.getMobile() != null && !s.getMobile().isEmpty()) {
                b = commHub.sendSMS(ADMINMOBILE, "New Order was received");
                if (b)
                    Utility.ShowSuccess("Message Sent successfully");
                else
                    Utility.ShowError("Could not send message");
            } else {
                if (ADMINEMAIL == null || ADMINEMAIL.isEmpty())
                    throw new RuntimeException("No destination email to send to");
                String encode = Json.encode(s);
                context.put("order", encode);
                StringWriter mailmsg = new StringWriter();
                template.merge(context, mailmsg);
                if (ADMINEMAIL.contains(";")) {
                    String[] mailto = ADMINEMAIL.split(";");
                    for (String value : mailto) {
                        b = commHub.sendEMail(message.toString(), value);
                    }
                } else
                    b = commHub.sendEMail(message.toString(), ADMINEMAIL);
            }
        } catch (
                Exception exp) {
            throw new RuntimeException(exp);
        }
        return b;
    }

    @ConsumeEvent("rtorderevent")
    @Transactional
    public void notifyStaff(ClientOrder order) {
        try {
            if (NotifyEmail) {
                confirmOrder(order, enCommMedia.EMAIL, order.getMobile());
            }
            if (NotifySMS) {
                confirmOrder(order, enCommMedia.SMS, order.getMobile());
            }
            //then update client notes
            int cid = MezoDB.getInteger("select id from tblclient where email='" + order.getClientid() + "'");
            pushNews(cid, "Order Number: " + order.getId(),
                    "Your order status was updated, " + order.getNotes());

        } catch (Exception exp) {
            Utility.ShowError(exp);
        }
    }

    @Transactional
    public void pushNews(int clientid, String title, String det) {
        try {
            Tblnews n = new Tblnews();
            n.setClientid(clientid);
            n.setItem(title);
            n.setDetails(det);
            n.persist();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @ConsumeEvent(value = IRationalEvents.RTEVENT_NEWORDER, blocking = true)
    public void sendPushNote(Order order) {
        try {
            System.out.println("Recieved event New Order Recieved");
            notifyStaff(order.toClientOrder());
            pushNews(order.getClientid(), "Order Activity for ".concat(order.getItem()), "Order status : " + order.getStatus().name()
                    + (order.getTotal() > 0 ? " Price:".concat(order.getTotal().toString()) : ""));
        } catch (Exception exp) {
            //just show error but proceed
            Utility.ShowError(exp);
        }
    }

    @ConsumeEvent(value = IRationalEvents.RTEVENT_ORDER_MODIFIED, blocking = true)
    public void sendOrderModified(Order order) {
        try {
            System.out.println("Recieved event New Order Recieved");
            notifyStaff(order.toClientOrder());
            pushNews(order.getClientid(), "Order Activity for ".concat(order.getItem()), "Order status : " + order.getStatus().name()
                    + (order.getTotal() > 0 ? " Price:".concat(order.getTotal().toString()) : ""));
        } catch (Exception exp) {
            //just show error but proceed
            Utility.ShowError(exp);
        }
    }

    @ConsumeEvent(value = IRationalEvents.RTEVENT_ORDER_CANCELED, blocking = true)
    public void sendOrderCanceled(Order order) {
        try {
            System.out.println("Recieved event New Order canceled");
            notifyStaff(order.toClientOrder());
            pushNews(order.getClientid(), "Order Activity for ".concat(order.getItem()), "Order status : " + order.getStatus().name()
                    + (order.getTotal() > 0 ? " Price:".concat(order.getTotal().toString()) : ""));
        } catch (Exception exp) {
            //just show error but proceed
            Utility.ShowError(exp);
        }
    }
    @ConsumeEvent(value = IRationalEvents.RTEVENT_ORDER_PRICED, blocking = true)
    public void sendOrderPriced(Order order) {
        try {
            System.out.println("Recieved event RTEVENT_ORDER_PRICED");
            notifyCustomers(enCommMedia.EMAIL);
            notifyCustomers(enCommMedia.SMS);
            pushNews(order.getClientid(), "Order Activity for ".concat(order.getItem()), "Order status : " + order.getStatus().name()
                    + (order.getTotal() > 0 ? " Price:".concat(order.getTotal().toString()) : ""));
        } catch (Exception exp) {
            //just show error but proceed
            Utility.ShowError(exp);
        }
    }
    @ConsumeEvent(value = IRationalEvents.RTEVENT_PASSWORD_RESET, blocking = true)
    public void sendPasswordRest(Client client) {
        try {
            System.out.println("Recieved event RTEVENT_PASSWORD_RESET");
//            commHub.sendSMS(s.getMobile(), message.toString());
//            pushNews(order.getClientid(), "Order Activity for ".concat(order.getItem()), "Order status : " + order.getStatus().name()
//                    + (order.getTotal() > 0 ? " Price:".concat(order.getTotal().toString()) : ""));
        } catch (Exception exp) {
            //just show error but proceed
            Utility.ShowError(exp);
        }
    }

    @ConsumeEvent(value = IRationalEvents.RTEVENT_PASSWORD_RESET, blocking = true)
    public void sendPasswordReset(Client clnt) {
        try {
            VelocityContext context = new VelocityContext();
            initTemplatePath(enCommMedia.EMAIL);
            Template template = Velocity.getTemplate("pwreset.txt");
            StringBuilder msg = new StringBuilder();
            StringWriter writer = new StringWriter();
            context.put("password", clnt.getPassword());
            context.put("name", clnt.getItem());
            context.put("email", clnt.getEmail());
            context.put("phone", clnt.getMobile());
            template.merge(context, writer);
            commHub.sendEMail(writer.toString(), clnt.getEmail());
            pushNews(clnt.getId(), "Password Reset", "This is to inform you that we have sent you password reset email upon a request from you.");
        } catch (Exception exp) {
            Utility.ShowError(exp);
        }
    }
}
