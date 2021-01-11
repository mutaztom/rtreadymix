package com.rationalteam.rtreadymix;

import com.rationalteam.core.security.CUmanager;
import com.rationalteam.core.security.CUser;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rterp.erpcore.data.TblCurrency;
import com.rationalteam.utility.CMezoTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemConfig {
    public static String CONFIGPATH = System.getenv("OPENSHIFT_DATA_DIR");
    public static CUmanager USERMANAGER = new CUmanager(CONFIGPATH + "/users.txt");

    public static String WEBINFPATH = CONFIGPATH;
    private static CCurrency defaultCurrency;
    private static Integer defcurrencyid;
    private static CCurrency compcur;

    static {
        CMezoTools.useWebMode = true;
        CMezoTools.PropFile = CONFIGPATH + "/rtprop.properties";
        WEBINFPATH = CONFIGPATH;
        MezoDB.REUSE_EMAN = true;
        DataManager.USINGHIBERNATE = false;
        Utility.PropFile = CONFIGPATH + "/rtprop.properties";
        CUser.setWorkingDirectory(CONFIGPATH);
    }

    public static CurrencyLocal getDefaultCurrency() {
        if (defaultCurrency == null || defaultCurrency.isEmpty()) {
            Integer defid = MezoDB.getInteger("select id from " + TblCurrency.class.getSimpleName() + " where ismain=true");
            defaultCurrency = Utility.lookUp(CCurrency.class);
            defaultCurrency.find(defid);
        }
        return defaultCurrency;
    }

    public static int getDefaultCurrencyId() {
        if (defcurrencyid == null || defcurrencyid < 0) {
            defcurrencyid = MezoDB.getInteger("select id from " + TblCurrency.class.getSimpleName() + " where ismain=true");
        }
        return defcurrencyid;
    }

    public static double getRate() {

        ExchangeLocal e = (ExchangeLocal) Utility.lookUp(CExchange.class);
        double rate = e.getRate(getCompCurrency());
        return rate;
    }

    public static CurrencyLocal getCompCurrency() {

        if (compcur == null || compcur.isEmpty()) {
            compcur = Utility.lookUp(CCurrency.class);
            Map<String, Object> map = new HashMap<>();
            map.put("symbol", "USD");
            List<TblCurrency> clist = MezoDB.openNamed("TblCurrency.findBySymbol", TblCurrency.class, map);
            if (clist != null && !clist.isEmpty()) {
                int id = clist.get(0).getId();
                compcur.find(id);
            }
        }
        return compcur;
    }
}
