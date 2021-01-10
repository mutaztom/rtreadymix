package com.rationalteam.rtreadymix;

import com.rationalteam.core.security.CUmanager;
import com.rationalteam.core.security.CUser;
import com.rationalteam.rterp.erpcore.DataManager;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.utility.CMezoTools;

public class SystemConfig {
    public static String CONFIGPATH = System.getenv("OPENSHIFT_DATA_DIR");
    public static CUmanager USERMANAGER = new CUmanager(CONFIGPATH + "/users.txt");

    public static String WEBINFPATH = CONFIGPATH;

    static {
        CMezoTools.useWebMode=true;
        WEBINFPATH = CONFIGPATH;
        MezoDB.REUSE_EMAN = true;
        DataManager.USINGHIBERNATE=false;
        Utility.PropFile = CONFIGPATH + "/rtprop.properties";
        CUser.setWorkingDirectory(CONFIGPATH);
    }
}
