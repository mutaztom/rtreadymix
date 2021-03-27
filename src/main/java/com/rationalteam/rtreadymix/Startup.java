package com.rationalteam.rtreadymix;

import com.rationalteam.core.security.CUser;
import com.rationalteam.rterp.erpcore.DataManager;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rtreadymix.data.Tblusers;
import com.rationalteam.rtreadymix.security.UserManager;
import com.rationalteam.utility.CMezoTools;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static com.rationalteam.rtreadymix.SystemConfig.CONFIGPATH;

@Singleton
@io.quarkus.runtime.Startup
public class Startup {
    @Inject
    EntityManager eman;

    public void loadConfig(@Observes StartupEvent ev) {
        DataManager.USINGHIBERNATE = false;
        Utility.PropFile = CONFIGPATH + "/rtprop.properties";
    }
    @Transactional
    public void loadUsers(@Observes StartupEvent evt) {
        MezoDB.setEman(eman);
        DataManager.setEntityManager(eman);
        Integer usercount = MezoDB.getInteger("select count(id) from tblusers");
        if (usercount == 0) {
            UserManager.add("admin", "admin", "admin");
            UserManager.add("user", "user", "user");
        }
    }
}
