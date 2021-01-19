package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rterp.erpcore.Utility;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Objects;

@ApplicationScoped
@ManagedBean("rtutil")
@Named("rtutil")
public class RtUtility {

    public String getItem(Integer itsid, String tbl) {
        try {
            if(Objects.isNull(itsid))
                return "NOTFOUND";
            if (itsid <= 0)
                return "Not Found";
            return MezoDB.getItem(itsid, tbl);
        } catch (Exception e) {
            Utility.ShowError(e);
            return "Not Found";
        }
    }
}
