package com.rationalteam.rtreadymix;

import com.rationalteam.core.CUmanager;
import com.rationalteam.rterp.erpcore.Utility;

public class SystemConfig {
    public static String CONFIGPATH = System.getenv("OPENSHIFT_DATA_DIR");
    public static CUmanager USERMANAGER = new CUmanager(CONFIGPATH + "/users.txt");
    public static String WEBINFPATH = CONFIGPATH;

    static {
        if (!CONFIGPATH.endsWith("rtmixserver"))
        {
            CONFIGPATH += "/rtmixserver";
            WEBINFPATH=CONFIGPATH;
            Utility.PropFile=CONFIGPATH+"/rtprop.properties";
        }
    }
}
