package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rterp.erpcore.data.TblCurrency;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemConfig {
    public static String CONFIGPATH = System.getenv("OPENSHIFT_DATA_DIR");
    public static String WEBINFPATH = CONFIGPATH;
    public static final String TEMPLATE = WEBINFPATH + "/templates/";
    public static final String PROPFILE = Paths.get(WEBINFPATH, "rtprop.properties").toString();
    private static CCurrency defaultCurrency;
    private static Integer defcurrencyid;
    private static CCurrency compcur;

    @Transactional
    public static CCurrency getDefaultCurrency() {
        if (defaultCurrency == null || defaultCurrency.isEmpty()) {
            Integer defid = MezoDB.getInteger("select id from " + TblCurrency.class.getSimpleName() + " where ismain=true");
            defaultCurrency = new CCurrency();
            defaultCurrency.find(defid);
        }
        return defaultCurrency;
    }

    public static int getDefaultCurrencyId() {
        try {
            if (defcurrencyid == null || defcurrencyid < 0) {
                defcurrencyid = MezoDB.getInteger("select id from " + TblCurrency.class.getSimpleName() + " where ismain=true");
            }
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return defcurrencyid;
    }

    @Transactional
    public static double getRate() {
        double rate = 1;
        try {
            CExchange e = new CExchange();
            rate = e.getRate(getCompCurrency());
        } catch (Exception exception) {
            Utility.ShowError(exception);
        }
        return rate;
    }

    @Transactional
    public static CCurrency getCompCurrency() {
        try {
            if (compcur == null || compcur.isEmpty() || compcur.getId() <= 0) {
                compcur = new CCurrency();
                int id = MezoDB.getInteger("select id from TblCurrency where symbol='USD'");
                if (id > 0)
                    compcur.find(id);
            }
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return compcur;
    }


}
