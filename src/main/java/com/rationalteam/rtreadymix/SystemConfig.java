package com.rationalteam.rtreadymix;

import com.rationalteam.core.CUmanager;

public class SystemConfig {
    public static String CONFIGPATH = System.getenv("OPENSHIFT_DATA_DIR");
    public static CUmanager USERMANAGER = new CUmanager(CONFIGPATH + "/users.txt");
    public static String WEBINFPATH = CONFIGPATH;

    static {
        if (!CONFIGPATH.endsWith("rtmixserver"))
        {
            CONFIGPATH += "/rtmixserver";
            WEBINFPATH=CONFIGPATH;
        }
    }
}
