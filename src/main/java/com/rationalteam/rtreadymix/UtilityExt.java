package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.Utility;
import org.checkerframework.checker.regex.qual.Regex;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
}
