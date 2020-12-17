package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.Utility;

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
}
