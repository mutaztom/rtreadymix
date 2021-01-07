package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.utility.CMezoTools;
import org.checkerframework.checker.regex.qual.Regex;

import javax.persistence.EntityManager;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.rationalteam.rterp.erpcore.MezoDB.PERSNAME;

public class UtilityExt extends Utility {
    public static LocalDateTime toLocalDateTime(String strdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (isDate(strdate))
            return LocalDateTime.of(LocalDate.parse(strdate), LocalTime.now());
        else if (isTime(strdate))
            return LocalDateTime.parse(strdate, formatter);
        else
            return null;
    }

    public static boolean isTime(String dt) {
        return dt.matches("\\d*-\\d*-\\d*\\s\\d*:(\\d*:\\d*)");
    }

    public static boolean isDate(String dt) {
        return dt.matches("\\d*-\\d*-\\d*");
    }

    public static Time toTime(LocalDateTime ltime) {
        return Time.valueOf(ltime.toLocalTime());
    }

    public static SimpleDateFormat dateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf;
    }

    public static boolean isValidEmail(String email) {
        try {
            String pattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            return email.matches(pattern);
        } catch (Exception e) {
            ShowError("Error validating email, ".concat(e.getMessage()));
            return false;
        }
    }

    public static boolean isValidMobile(String mobile) {
        try {
            String pattern = "((\\+|[0]{2})[0-9]{3}|[0-9])([-.\\(]?[0-9]{1}[\\)]|[0-9]?|\\s)[0-9]+";
            return mobile.matches(pattern);
        } catch (Exception e) {
            ShowError("Error validating email, ".concat(e.getMessage()));
            return false;
        }
    }
    public static EntityManager getPersistance(String pername) {
        try {
            Properties p =System.getProperties();
            Map<String, Object> map = new HashMap<>();
            map.put("javax.persistence.jdbc.driver", p.getProperty("driver"));
            map.put("javax.persistence.jdbc.url", p.getProperty("url"));
            map.put("javax.persistence.jdbc.user", p.getProperty("user"));
            map.put("javax.persistence.jdbc.password", p.getProperty("password"));
            javax.persistence.EntityManagerFactory fact = javax.persistence.Persistence.createEntityManagerFactory(pername, map);
            javax.persistence.EntityManager eman = fact.createEntityManager();
            return eman;
        } catch (Exception exp) {
            ShowError(exp);
            return null;
        }
    }



}
