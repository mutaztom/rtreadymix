package com.rationalteam.rtreadymix;

import com.rationalteam.core.CUmanager;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;

public class SystemConfig {
    public static String CONFIGPATH = System.getenv("OPENSHIFT_DATA_DIR");
    public static CUmanager USERMANAGER = new CUmanager(CONFIGPATH + "/users.txt");
    public static String WEBINFPATH = CONFIGPATH;

    static {
        WEBINFPATH = CONFIGPATH;
        MezoDB.REUSE_EMAN = true;
        Utility.PropFile = CONFIGPATH + "/rtprop.properties";
    }
}
