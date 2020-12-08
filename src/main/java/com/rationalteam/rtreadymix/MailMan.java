package com.rationalteam.rtreadymix;

import com.rationalteam.core.communication.EmailValidator;
import com.rationalteam.core.communication.enMailProtocol;
import com.rationalteam.rterp.erpcore.Utility;
import com.sun.mail.pop3.POP3Message;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.xml.bind.ValidationException;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MailMan {
    protected static String SMTP_HOST_NAME;
    protected static String SMTP_AUTH_USER;
    protected static String SMTP_AUTH_PWD;
    protected static String SMTP_AUTH_PORT;
    public static final String PROP_PROGRESS = "PROP_PROGRESS";
    protected static String SMTP_SENDER;
    protected final String secname = "RationalTeam";
    protected static enMailProtocol PROTOCOL;
    protected static int PORT;
    protected String emailFromAddress;
    protected HashMap<String, String> recipients;
    protected List<String> attachments;
    protected String subject;
    protected String message;
    protected final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    protected StringBuilder sendReport = new StringBuilder();

    static {
        SMTP_HOST_NAME = Utility.getProperty("smtpHost");
        SMTP_AUTH_PORT = Utility.getProperty("smtpPort");
        SMTP_AUTH_USER = Utility.getProperty("mailUser");
        SMTP_AUTH_PWD = Utility.getProperty("mailPassword");
        SMTP_SENDER = Utility.getProperty("mailSender");
    }

    public static String getSMTP_HOST_NAME() {
        return SMTP_HOST_NAME;
    }

    public static void setSMTP_HOST_NAME(String hname) {
        SMTP_HOST_NAME = hname;
    }

    public static String getSMTP_AUTH_USER() {
        return SMTP_AUTH_USER;
    }

    public static void setSMTP_AUTH_USER(String u) {
        SMTP_AUTH_USER = u;
    }

    public static String getSMTP_AUTH_PWD() {
        return SMTP_AUTH_PWD;
    }

    public static void setSMTP_AUTH_PWD(String pwd) {
        SMTP_AUTH_PWD = pwd;
    }

    public static String getSMTP_AUTH_PORT() {
        return SMTP_AUTH_PORT;
    }

    public static void setSMTP_AUTH_PORT(String p) {
        SMTP_AUTH_PORT = p;
    }

    public static enMailProtocol getPROTOCOL() {
        return PROTOCOL;
    }

    public static void setPROTOCOL(enMailProtocol protocol) {
        PROTOCOL = protocol;
    }

    public static int getPORT() {
        return PORT;
    }

    public static void setPORT(int port) {
        PORT = port;
    }

    public void loadConfig() {
        sendReport = new StringBuilder();
        attachments = new ArrayList<>();
        SMTP_HOST_NAME = Utility.getProperty("smtpHost");
        SMTP_AUTH_PORT = Utility.getProperty("smtpPort");
        SMTP_AUTH_USER = Utility.getProperty("mailLogin");
        SMTP_AUTH_PWD = Utility.getProperty("mailPassword");
        SMTP_SENDER = Utility.getProperty("mailSender");
    }

    public boolean postMail() throws MessagingException {
        try {
            boolean debug = false;
            Properties props = new Properties();
            props.put("mail.smtp.host", getSMTP_HOST_NAME());
            props.put("mail.smtp.auth", "true");
            if (SMTP_AUTH_PORT != null && !SMTP_AUTH_PORT.isEmpty()) {
                props.put("mail.smtp.port", SMTP_AUTH_PORT);
            }

            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);
            session.setDebug(debug);
            Message msg = new MimeMessage(session);
            InternetAddress addressFrom = new InternetAddress(this.getEmailFromAddress());
            msg.setFrom(addressFrom);
            InternetAddress[] addressTo = new InternetAddress[this.recipients.size()];
            int i = 0;
            EmailValidator eval = new EmailValidator();
            for (String k : recipients.keySet()) {
                if (eval.validate(recipients.get(k))) {
                    addressTo[i] = new InternetAddress((String) recipients.get(k));
                    ++i;
                }
            }

            msg.setRecipients(Message.RecipientType.TO, addressTo);
            String word = "";
            msg.setSubject(this.subject);
            msg.setContent(this.message, "text/plain");
            BodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            if (this.attachments != null) {
                this.addAtachments(this.attachments, multipart);
            }

            msg.setContent(multipart);
            messageBodyPart.setText(this.message);
            Transport.send(msg);
            this.sendReport.append("Send command executed.");
            return true;
        } catch (Exception exp) {
            Utility.ShowError(exp);
            throw new RuntimeException(exp);
        }
    }

    protected void addAtachments(List<String> attachments, Multipart multipart) throws MessagingException, AddressException {
        for (String at : attachments) {
            String displayname = at;
            Path p = Paths.get(at);
            displayname = p.getFileName().toString();
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(at);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(displayname);
            multipart.addBodyPart(attachmentBodyPart);
        }

    }

    public void addAttachment(String atmnt) {
        attachments.add(atmnt);
    }

    public String getEmailFromAddress() {
        return this.emailFromAddress;
    }

    public void setEmailFromAddress(String emailFromAddress) {
        this.emailFromAddress = emailFromAddress;
    }

    public HashMap<String, String> getRecipients() {
        return this.recipients;
    }

    public void setRecipients(HashMap<String, String> recipients) {
        this.recipients = recipients;
    }

    public List<String> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean checkEntries() throws ValidationException {
        if (this.recipients != null && !this.recipients.isEmpty()) {
            if (this.message != null && !this.message.isEmpty()) {
                if (this.subject != null && !this.subject.isEmpty()) {
                    if (this.emailFromAddress != null && !this.emailFromAddress.isEmpty()) {
                        if (SMTP_HOST_NAME != null && !SMTP_HOST_NAME.isEmpty()) {
                            if (SMTP_AUTH_USER != null && !SMTP_AUTH_USER.isEmpty()) {
                                if (SMTP_AUTH_PWD != null && !SMTP_AUTH_PWD.isEmpty()) {
                                    return true;
                                } else {
                                    throw new ValidationException("SMTP Server is not configured. Please configure login password first.");
                                }
                            } else {
                                throw new ValidationException("SMTP Server is not configured. Please configure login name first.");
                            }
                        } else {
                            throw new ValidationException("SMTP Server is not configured. Please configure host name first");
                        }
                    } else {
                        throw new ValidationException("From email value must be set, (you email as a sender).");
                    }
                } else {
                    throw new ValidationException("Subject is empty,please type a subject.");
                }
            } else {
                throw new ValidationException("Message body is empty,please type a message.");
            }
        } else {
            throw new ValidationException("Recepients must be set first.");
        }
    }

    public String getSendReport() {
        return this.sendReport.toString();
    }

    public boolean checkMail() throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.store.protocol", "com.sun.mail.pop3");
        prop.put("mail.pop3.host", SMTP_HOST_NAME);
        prop.put("mail.pop3.auth", "true");
        prop.put("mail.pop3.port", PORT);
        prop.put("mail.pop3.starttls.enable", "true");
        SMTPAuthenticator auth = new SMTPAuthenticator(SMTP_AUTH_USER, SMTP_AUTH_PWD);
        Session session = Session.getInstance(prop, auth);
        Store store = session.getStore("pop3");
        store.connect(SMTP_HOST_NAME, PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
        Folder inbox = store.getFolder("Inbox");
        Message msg = new POP3Message(inbox, 0);
        msg.saveChanges();
        Transport transport = session.getTransport("pop3");
        transport.connect(SMTP_HOST_NAME, PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
        Address m = new InternetAddress("mutaztom@gmail.com");
        Address[] rec = new Address[]{m};
        msg.setFrom(new InternetAddress("mutaz@rational-team.com"));
        msg.setRecipients(Message.RecipientType.TO, rec);
        msg.setSubject(this.subject);
        msg.setContent(this.message, "text/plain");
        BodyPart messageBodyPart = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        this.addAtachments(this.attachments, multipart);
        msg.setContent(multipart);
        messageBodyPart.setText(this.message);
        transport.sendMessage(msg, rec);
        transport.close();
        return false;
    }

    public void addCC(String ccmail) {
        recipients.putIfAbsent(ccmail, ccmail);
    }

    public void addCC(String ccname, String ccmail) {
        recipients.putIfAbsent(ccname, ccmail);
    }

    public void addBC(String bcmail) {
        recipients.putIfAbsent(bcmail, bcmail);
    }

    public void addBC(String bcname, String bcmail) {
        recipients.putIfAbsent(bcname, bcmail);
    }

    private class SMTPAuthenticator extends Authenticator {
        String username;
        String password;

        public SMTPAuthenticator() {
            this.username = SMTP_AUTH_USER;
            this.password = SMTP_AUTH_PWD;
        }

        public SMTPAuthenticator(String un, String pwd) {
            this.username = SMTP_AUTH_USER;
            this.password = SMTP_AUTH_PWD;
            this.username = un;
            this.password = pwd;
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.username, this.password);
        }


    }
}
