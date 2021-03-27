/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rationalteam.rtreadymix;

import com.rationalteam.core.enPeriodRange;
import com.rationalteam.core.enShortPeriodRange;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rterp.erpcore.data.TblOption;
import com.rationalteam.utility.CReadNumber;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

/**
 * @author mutaz
 */
@SessionScoped
public class Rtutil implements Serializable {

    private CPeriod period;
    private String plantype;
    private String activeOptionTbl;
    private List<CRtDataObject> dependancy;
    private boolean deleted;
    private double rate;
    private String input;
    private Locale locale;
    private IRtDataObject selected;
    private int viewcount;
    IRtDataObject target;
    private int itemid;


    /**
     * Creates a new instance of Rtutil
     */
    public Rtutil() {
        period = new CPeriod();
        deleted = false;
        locale = Locale.getDefault();
        viewcount = 5;
        itemid = -1;

    }


    public String getMessage(String msg) {
        return ResourceBundle.getBundle("com/rationalteam/ar_resources").getString(msg);
    }

    public List<String> fromEnum(String cname) {
        List<String> names = new ArrayList<>();
        try {
            Class<?> c;
            c = Class.forName(cname);
            if (c == null)
                return names;
            Object[] r = c.getEnumConstants();
            if (r != null) {
                for (Object o : r) {
                    names.add(o.toString());
                }
            }
        } catch (ClassNotFoundException | SecurityException ex) {
            Utility.ShowError(ex);
        } catch (IllegalArgumentException ex) {
            Utility.ShowError(ex);
        }
        return names;
    }


    public List<CCurrency> listCurrency() {
        List<CCurrency> lst = (new CCurrency()).listAll();
        return lst;
    }

    public List<COption> fromOptions(String tbl) {
        try {
            COption option = new COption(tbl);
            option.setDbTable("tbl");
            List lst = option.listAll();
            return lst;
        } catch (Exception e) {
            Utility.ShowError(e);
            return new ArrayList<>();
        }
    }

    public List<COption> fromOptions(String tbl, Locale loc) {
        try {
            COption option = new COption(tbl);
            List<COption> lst = option.listAllOptions(tbl, loc);
            return lst;
        } catch (Exception e) {
            Utility.ShowError(e);
            return new ArrayList<>();
        }
    }

    public List<COption> filterOption(String tbl, Integer filter) {
        try {
            List<COption> lst = new ArrayList<>();
            if (filter == null) {
                return lst;
            }
            List<TblOption> oplist = MezoDB.open("select * from " + tbl + " where mainid=" + filter, TblOption.class);
            if (oplist == null || oplist.isEmpty()) {
                return lst;
            }
            for (TblOption o : oplist) {
                COption option = new COption(tbl);
                option.setData(o);
                lst.add(option);
            }
            return lst;
        } catch (Exception e) {
            Utility.ShowError(e);
            return new ArrayList<>();
        }
    }

       public List<COption> listCities() {
        try {
            COption option = new COption("tbiclity");
            List<COption> lst = option.listAllOptions("tblcity");
            return lst;
        } catch (Exception e) {
            Utility.ShowError(e);
            return new ArrayList<>();
        }
    }

    public Integer getDefault(String tbl) {
        try {
            Object r = MezoDB.getValue("select id from " + tbl + " where isdefault=1");
            if (r != null) {
                return (Integer.valueOf(r.toString()));
            }
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return -1;
    }

    public COption getDefOption(String tbl) {
        try {
            Object r = MezoDB.getValue("select id from " + tbl
                    + " where id=(select defoption from tblsystemoption where tblname='" + tbl + "')");
            if (r != null) {
                Integer id = Integer.valueOf(r.toString());
                COption op = new COption(tbl);
                op.find(id);
                return op;
            }
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return null;
    }

    public List<COption> listMonths() {
        DateFormatSymbols df = new DateFormatSymbols();
        String[] mon = df.getMonths();
        List<COption> op = new ArrayList<>();
        for (int i = 0; i < mon.length; i++) {
            if (!mon[i].toString().isEmpty()) {
                COption o = new COption();
                o.setId(i);
                o.setItem(mon[i]);
                op.add(o);
            }
        }
        return op;
    }

    public static String fordate(Date d) {
        try {
            DateFormat f = DateFormat.getDateInstance();
            return f.format(d);
        } catch (Exception e) {
            Utility.ShowError(e);
            return d.toString();
        }
    }

    public List<String> getKeyAsList(Map<Date, Double> map) {
        List<String> rlst = new ArrayList<>();
        SimpleDateFormat f = (SimpleDateFormat) SimpleDateFormat.getInstance();
        for (Date d : map.keySet()) {
            rlst.add(f.format(d));
        }
        return rlst;
    }

    public List<String> getMapKeys(Map map) {
        List<String> rlst = new ArrayList<>();
        for (Object d : map.keySet()) {
            rlst.add(d.toString());
        }
        return rlst;
    }

    public String getMapValue(Map map, Object key) {
        if (map.containsKey(key)) {
            return map.get(key).toString();
        } else {
            return "";
        }
    }

    public List<String> getMapAsString(Map<Date, Double> map) {
        List<String> rlst = new ArrayList<>();
        int size = map.keySet().size();
        Date[] ard = map.keySet().toArray(new Date[size]);
        Arrays.sort(ard);
        for (Date d : ard) {
            rlst.add(SimpleDateFormat.getInstance().format(d) + " :" + NumberFormat.getInstance().format(map.get(d)));
        }

        return rlst;
    }

    public String getIcon(String pname) {
        if (pname != null) {
            return "./resources/images/" + pname.toLowerCase() + ".png";
        } else {
            return "./resources/images/rationalteam.png";
        }

    }


    public String getJobTitle(Integer empid) {
        Object v = MezoDB
                .getValue("select item from tbljobs where id in (select job from tblstaff where id=" + empid + ")");
        if (v != null) {
            return v.toString();
        } else {
            return "[Not Set]";
        }
    }

    public String getEmpNo(Integer empid) {
        Object v = MezoDB.getValue("select empno from tblstaff where id=" + empid);
        if (v != null) {
            return v.toString();
        } else {
            return "[Not Set]";
        }
    }

    public Date getCurrDate() {
        return GregorianCalendar.getInstance().getTime();
    }

    public Integer getYear() {
        Integer y = GregorianCalendar.getInstance().get(Calendar.YEAR);
        return y;
    }

    public Integer getLastYear() {
        Integer y = GregorianCalendar.getInstance().get(Calendar.YEAR);
        return y - 1;
    }


    public String getCompanyName() {
        String loc = Utility.getProperty("locale");
        String c = "en".equals(loc) ? "company" : "arcompany";
        String company = Utility.getProperty(c);
        if (company == null || company.isEmpty()) {
            company = ResourceBundle.getBundle("rationalPurchase").getString("rational");
        }
        return company;
    }

    // public Date toDate(LocalDate ldate) {
    // Date d = Date.from(Instant.from(ldate));
    // return d;
    // }
    public String getMonthName(int m) {
        DateFormatSymbols df = new DateFormatSymbols();
        return df.getMonths()[m - 1];

    }

    public enPeriodRange[] listRanges() {
        return enPeriodRange.values();
    }

    public enShortPeriodRange[] listShortRanges() {
        return enShortPeriodRange.values();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale l) {
        locale = l;
    }

    public String getDir() {
        Locale loc = getLocale();
        return "en".equals(loc.getLanguage()) ? "ltr" : "rtl";
    }

    public CPeriod getPeriod() {
        return period;
    }

    public void setPeriod(CPeriod period) {
        this.period = period;
    }

    public String format(Object o) {
        if (o == null)
            return "<Null>";
        if (o instanceof Date)
            return formatDate((Date) o);
        else if (o instanceof Number)
            return formatNo((Number) o);
        return o.toString();
    }

    public String formatDate(Date d) {
        if (d == null) {
            return "Not set!";
        }
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/YYYY");
        return f.format(d);
    }

    public String formatNo(Number n) {
        if (n != null && n.doubleValue() >= 0)
            return NumberFormat.getInstance().format(n);
        else
            return "-";
    }

    public String getStatusStyle(enRequestStatus state) {
        if (state == null) {
            return "color: blue; font-weight:bold;font-size:small;";
        }
        switch (state) {
            case Requested:
                return "color: orange; font-weight:bold;font-size:small;";
            case Rejected:
                return "color: red; font-weight:bold;font-size:small;";
            case Delivered:
                return "color: green; font-weight:bold;font-size:small;";
            case Expired:
                return "color: gray; font-weight:bold;font-size:small;";
            default:
                return "color: blue; font-weight:bold;font-size:small;";
        }
    }

    public String getStatusStyle(String st) {
        try {
            if (st == null) {
                return "color: blue; font-weight:bold;font-size:small;";
            }
            enRequestStatus state = enRequestStatus.valueOf(st);
            return getStatusStyle(state);
        } catch (IllegalArgumentException e) {
            return "color: blue; font-weight:bold;font-size:small;";
        }
    }

    // public String getPriorityStyle(String prior) {
    // if (prior == null) {
    //// return "color: gray; font-weight:bold;font-size:small;";
    // }
    // enPriority vp = enPriority.valueOf(prior);
    // if (vp == null) {
    // return "color: gray; font-weight:bold;font-size:small;";
    // }
    // switch (vp) {
    // case Normal:
    // return "color: blue; font-weight:bold;font-size:small;";
    // case BetterSoon:
    // return "color: orange; font-weight:bold;font-size:small;";
    // case Urgent:
    // return "color: red; font-weight:bold;font-size:small;";
    // case WheneverAvailable:
    // return "color: gray; font-weight:bold;font-size:small;";
    // default:
    // return "color: blue; font-weight:bold;font-size:small;";
    // }
    // }
    public void deleteItem(CRtDataObject item) {
        item.delete();
    }

    // public void deleteTableRecord(Object item) {
    // MezoDB.delete(item);
    // }
    public Set<Currency> currencySymbols() {
        Set<Currency> avcur = Currency.getAvailableCurrencies();
        return avcur;
    }

    public String getPlantype() {
        return plantype;
    }

    public void setPlantype(String plantype) {
        this.plantype = plantype;
    }

    public String getPlanStyle(Double val) {
        if (val >= 0) {
            return "font-weight: bold;color: green;";
        } else {
            return "font-weight: bold;color: red;";
        }
    }

    public String getType(Object o) {
        return o.getClass().getSimpleName();
    }

    public String getOptionItem(Integer opid, String tbl) {
        if (opid == null) {
            return "(Not Set!)";
        }
        COption o = new COption(tbl);
        o.find(opid);
        if (!o.isEmpty()) {
            return o.getItem();
        } else {
            return "(Not Set!)";
        }
    }

    public String getActiveOptionTbl() {
        return activeOptionTbl;
    }


    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }


    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public IRtDataObject getSelected() {
        return selected;
    }

    public void setSelected(IRtDataObject selected) {
        this.selected = selected;
    }

    public int getViewcount() {
        return viewcount;
    }

    public void setViewcount(int viewcount) {
        this.viewcount = viewcount;
    }

    public IRtDataObject getTarget() {
        return target;
    }

    public void setTarget(IRtDataObject r) {
        target = r;
    }

    public String getItemName(String tbl, int itemid) {
        try {
            Object v = MezoDB.getValue("select item from " + tbl + " where id=" + itemid);
            if (v != null)
                return v.toString();
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return "!NotSet!";
    }

    public String readNumber(Double no, CCurrency c) {

        String numread = CReadNumber.readNumber(no, c.getItem(), c.getFraction());
        return numread;
    }

    public int getItemid() {
        return itemid;
    }

    public DayOfWeek[] getWeekdays() {
        return DayOfWeek.values();
    }

    public String getDayName(DayOfWeek day) {
        String locale = Utility.getProperty("locale");
        return day.getDisplayName(java.time.format.TextStyle.FULL, Locale.forLanguageTag(locale));
    }


    public List<COption> fromTable(String tbl) {
        List open = MezoDB.Open("select id,item from " + tbl);
        List<COption> result = new ArrayList<>();
        COption o = new COption(tbl);
        o.setDbTable(tbl);
        if (open != null) {
            for (Object r : open) {
                Object[] rec = (Object[]) r;
                o = new COption(tbl);
                o.setId(Integer.valueOf(rec[0].toString()));
                o.setItem(rec[1].toString());
                result.add(o);
            }
        }
        return result;
    }

}
