package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.DataManager;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rtreadymix.data.Tblcomlog;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/*
Communication Hub For RationalTeam
 */
@ApplicationScoped
public class CommHub {
    @Inject
    ReactiveMailer remail;

    public boolean sendSMS(String mobiles, String message) {
        boolean r = false;
        try {
            StringBuilder sms = new StringBuilder();
            if (mobiles.isEmpty())
                throw new RuntimeException("You must provide mobile number");
            if (message.isEmpty())
                throw new RuntimeException("you must provide sms message");
            mobiles = fixNumbers(mobiles);
            message = message.replace(" ", "%20");
            sms.append(Utility.getProperty("smshost"));
            sms.append("webacc.aspx?user=").append(Utility.getProperty("smsuser"))
                    .append("&pwd=").append(Utility.getProperty("smspassword"))
                    .append("&smstext=").append(message)
                    .append("&Sender=").append(Utility.getProperty("smssender"))
                    .append("&Nums=").append(mobiles);
            Request request = new Request.Builder()
                    .url(sms.toString())
                    .build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();
            Call call = client.newCall(request);
            call.timeout().timeout(60, TimeUnit.SECONDS);
            try (Response response = call.execute()) {
                String resp = response.body().string();
                Utility.ShowError(resp);
                if (resp.contains("Reject"))
                    r = false;
                else if (resp.contains("Ok")) {
                    Tblcomlog clog = new Tblcomlog();
                    clog.setAddress(mobiles);
                    clog.setByuser("rtmixadmin");
                    clog.setMessage(message);
                    clog.setSmstime(Timestamp.valueOf(LocalDateTime.now()));
                    CommHub.log(clog);
                    return true;
                }
            }
        } catch (Exception exp) {
            Utility.ShowError(exp);
        }
        return r;
    }

    private String fixNumbers(String mobiles) {
        mobiles = mobiles.startsWith("0") ? mobiles.substring(1) : mobiles;
        mobiles = mobiles.contains("+") ? mobiles.replace("+", "") : mobiles;
        mobiles = mobiles.contains(",") ? mobiles.replace(",", ";") : mobiles;
        mobiles = mobiles.startsWith("249") ? mobiles : "249" + mobiles;
        mobiles = mobiles.replace("%3b", ";");
        return mobiles;
    }

    public boolean sendEMail(String message, String email) {
        try {
            String subject = ("RationalTeam Service Hub");
            remail.send(Mail.withText(email, subject, message));
            Tblcomlog clog = new Tblcomlog();
            clog.setAddress(email);
            clog.setByuser("rtmixadmin");
            clog.setMessage(message);
            clog.setSmstime(Timestamp.valueOf(LocalDateTime.now()));
            CommHub.log(clog);
        } catch (Exception ex) {
            Utility.ShowError(ex);
        }
        return false;
    }

    public static boolean log(Tblcomlog tbl) {
        boolean r = false;
        try {
            DataManager<Tblcomlog> dmlog = new DataManager<>(Tblcomlog.class);
            dmlog.create(tbl);
            r = true;
        } catch (Exception exp) {
            Utility.ShowError(exp);
        }
        return r;
    }

    public static SMSBuilder getSMSBuilder() {
        return new SMSBuilder();
    }

    public static EmailBuilder getEmailBuilder() {
        return new EmailBuilder();
    }

    //BUILD ENGINE FOR EMAILS
    static class EmailBuilder {
        StringWriter swriter = new StringWriter();
        String message;
        Velocity v;
        Template template;
        String item;
        String mailto;
        MailMan engine;

        private void init() {
            Properties p = new Properties();
            p.setProperty("resource.loader.file.path", SystemConfig.WEBINFPATH + "/templates/email/");
            Velocity.init(p);
            v = new Velocity();
        }

        EmailBuilder() {
            init();
            engine = new MailMan();
            engine.loadConfig();
        }

        public EmailBuilder message(String msg) {
            message = msg;
            return this;
        }

        public EmailBuilder withItem(String witem) {
            item = witem;
            return this;
        }

        public EmailBuilder fromEmail(String femail) {
            engine.setEmailFromAddress(femail);
            return this;
        }

        public EmailBuilder subject(String subj) {
            engine.setSubject(subj);
            return this;
        }

        public EmailBuilder fromTemplate(String temp) {
            template = v.getTemplate(temp);
            return this;
        }

        public EmailBuilder recepient(String rec) {
            mailto = rec;
            Map recmap = new HashMap<String, String>();
            recmap.put(item != null ? item : rec, rec);
            engine.setRecipients((HashMap<String, String>) recmap);
            return this;
        }

        public EmailBuilder attach(String atfile) {
            if (atfile != null)
                if (Files.exists(Paths.get(atfile)))
                    engine.addAttachment(atfile);
            return this;
        }


        public void buildQuotation() {
            VelocityContext context = new VelocityContext();
            context.put("customer", item);
            context.put("title", engine.getSubject());
            template.merge(context, swriter);
            engine.setMessage(swriter.toString());
            engine.setEmailFromAddress(Utility.getProperty("mailSender"));
            //if cc or bc is required
            engine.addBC("Mutaz", "mutaz@rationalteam.net");
            try {
                boolean r = engine.postMail();
                if (r) {
                    Tblcomlog lfile = new Tblcomlog();
                    lfile.setMessage(message);
                    lfile.setByuser("rtmixadmin");
                    lfile.setSmstime(Timestamp.valueOf(LocalDateTime.now()));
                    lfile.setEmail(mailto);
                    log(lfile);
                    System.out.println("Email sent succesfully");
                    //delete attached file when finished
                    for (String af : engine.getAttachments()) {
                        File f = new File(af);
//                        if (f.exists())
//                            f.delete();
                    }
                }
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }

        public void build() {

        }
    }

    //BUILD ENGINE FOR SMS
    static class SMSBuilder {
        //http://212.0.129.229/bulksms/webacc.aspx?user=XXXXX&pwd=XXXX&smstext=helloHTTP &Sender=http&Nums=249912333212;249123699000
        StringBuilder sbuilder;
        String message;
        String template;

        public SMSBuilder() {
            sbuilder = new StringBuilder();
        }

        public SMSBuilder withUrl(String url) {
            sbuilder.append(url);
            return this;
        }

        public SMSBuilder withUser(String user) {
            sbuilder.append("?user=").append(user);
            return this;
        }

        public SMSBuilder withSender(String sender) {
            sbuilder.append("&Sender=").append(sender);
            return this;
        }

        public SMSBuilder withPwd(String pwd) {
            sbuilder.append("&pwd=").append(pwd);
            return this;
        }

        public SMSBuilder message(String msg) {
            message = msg.replace(" ", "%20");
            sbuilder.append("&smstext=").append(message);
            return this;
        }

        public SMSBuilder fromTemplate(String tmplate) {
            template = tmplate;
            return this;
        }


        public SMSBuilder mobiles(String mobiles) {
            mobiles = mobiles.startsWith("0") ? mobiles.substring(1) : mobiles;
            mobiles = mobiles.startsWith("+") ? mobiles.replace("+", "") : mobiles;
            mobiles = mobiles.startsWith("249") ? mobiles : "249" + mobiles;
            sbuilder.append("&Nums=").append(mobiles);
            return this;
        }

        public String build() {
            sbuilder.append("&smstext=").append(message);
            return sbuilder.toString();
        }

    }
}
