package com.rationalteam.rtreadymix;

import com.rationalteam.reaymixcommon.ClientOrder;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rterp.sales.Subscribtion;
import com.rationalteam.rterp.sales.SubscribtionLocal;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import javax.ejb.EJB;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SubscriptionServer {
    @EJB
    SubscribtionLocal subs;
    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    private String ADMINEMAIL = "mutaztom@gmail.com";
    private static final String ADMINMOBILE = "+249912352368";

    public SubscriptionServer() {
        initTemplatePath(enCommMedia.EMAIL);
    }

    public void initTemplatePath(enCommMedia media) {
        Path path = Paths.get(SystemConfig.WEBINFPATH, "templates", media.name().toLowerCase());
        Properties p = new Properties();
        p.setProperty("resource.loader.file.path", path.toString());
        Velocity.init(p);
    }

    public List<SubscribtionLocal> getDue() {
        try {
            Map<Integer, Object> map = new HashMap<>();
            Date date = new Date();
            String sql = "select * from tblsubscribtion where datediff(enddate,current_date) between 1 and notifybefore and mobile is not null and mobile <>''";
            subs = Utility.lookUp(Subscribtion.class);
            List expired = subs.listNative(sql, map);
            return expired;
        } catch (Exception exp) {
            Utility.ShowError(exp);
            return new ArrayList<>();
        }
    }

    public List<SubscribtionLocal> getNew() {
        try {
            Map<Integer, Object> map = new HashMap<>();
            Date date = new Date();
            String sql = "select * from tblsubscribtion where datediff(ondate,current_date) between 1 and 3 and mobile is not null and mobile <>''";
            subs = Utility.lookUp(Subscribtion.class);
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
            List<SubscribtionLocal> due = getDue();
//            //filter those with mobile number associated to it
            List<SubscribtionLocal> hasmobile = due.stream().filter(f -> f.getMobile() != null).collect(Collectors.toList());
            //build sms content, and due to the difference in content of the sms it is difficult to send as bulk
            VelocityContext context = new VelocityContext();
            initTemplatePath(media);
            Template template = Velocity.getTemplate("subsnotify.txt");
            for (SubscribtionLocal s :
                    due) {
                context.put("service", s.getItem());
                context.put("days", s.getDaysToExpire());
                StringWriter message = new StringWriter();
                template.merge(context, message);
                System.out.println(s.getMobile() + " " + message);
                System.out.println(">>>>>>>>>>>>>>>>>>>>");
                if (s.getMobile() != null && !s.getMobile().isEmpty()) {
                    boolean b = CommHub.sendSMS(s.getMobile(), message.toString());
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
            List<SubscribtionLocal> hasemail = getDue().stream().filter(f -> f.getEmail() != null).collect(Collectors.toList());
            int c = 0;
            for (SubscribtionLocal s :
                    hasemail) {
                context.put("customer", MezoDB.getItem(s.getCustomer(), "tblcustomer"));
                context.put("days", s.getDaysToExpire());
                context.put("service", s.getItem());
                context.put("sender", "RationalTeam Robot");
                StringWriter message = new StringWriter();
                template.merge(context, message);
                System.out.println(message.toString());
                if (c == 0)
                    CommHub.sendEMail(message.toString(), "mutaztom@gmail.com");
                c++;

            }
        }

    }

    public void sendBulkWlecome(enCommMedia media) {
        if (media == null)
            throw new RuntimeException("Media parameter must be specified sms or email");

        if (media.equals(enCommMedia.SMS)) {
            List<SubscribtionLocal> due = getNew();
//            //filter those with mobile number associated to it
            List<SubscribtionLocal> hasmobile = due.stream().filter(f -> f.getMobile() != null).collect(Collectors.toList());
            //build sms content, and due to the difference in content of the sms it is difficult to send as bulk
            VelocityContext context = new VelocityContext();
            initTemplatePath(media);

            Template template = Velocity.getTemplate("subswelcome.txt");
            for (SubscribtionLocal s :
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
                    boolean b = CommHub.sendSMS(s.getMobile(), message.toString());
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
            List<SubscribtionLocal> hasemail = getDue().stream().filter(f -> f.getEmail() != null).collect(Collectors.toList());
            int c = 0;
            for (SubscribtionLocal s :
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
                    CommHub.sendEMail(message.toString(), "mutaztom@gmail.com");
                c++;

            }
        }

    }

    public boolean sendWelcome(SubscribtionLocal s, enCommMedia media) {
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
                    b = CommHub.sendSMS(s.getMobile().concat(",+249912352368"), message.toString());
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

    public SubscribtionLocal getSub() {
        return subs;
    }

    public void setSub(SubscribtionLocal sub) {
        this.subs = sub;
    }

    public boolean confirmOrder(ClientOrder s, enCommMedia media,String mobile) {
        boolean b = false;
        try {
            VelocityContext context = new VelocityContext();
            initTemplatePath(media);
            Template template = Velocity.getTemplate("placeorder.txt");
            StringBuilder message = new StringBuilder();
            JsonObject jorder=JsonObject.mapFrom(s);
            message.append(s.getId() > 0 ? "Order modified on " : "New order was placed").append(" ON ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_ORDINAL_DATE));
            message.append("\n").append("from: ").append(s.getClientid()).append("\n")
                    .append(jorder.toString())
                    .append(" onmap: ").append("https://www.google.com/maps/@")
                    .append("Mobile: ").append(mobile);
            if (media == enCommMedia.SMS) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>");
//                if (s.getMobile() != null && !s.getMobile().isEmpty()) {
                b = CommHub.sendSMS(ADMINMOBILE, message.toString());
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
                b=CommHub.sendEMail(message.toString(), ADMINEMAIL);
                System.out.println(mailmsg.toString());
            }
        } catch (
                Exception exp) {
            throw new RuntimeException(exp);
        }
        return b;
    }

    public void notifyStaff() {

    }
}
