/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rationalteam.rtreadymix.purchase;

import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.utility.CMezoTools;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author mutaz
 */
public class SupplierMap extends CRtDataObject {

    public static List<SupplierMap> load(Integer sid) {
        List<SupplierMap> lst = new ArrayList<>();
        try {
            SupplierMap smap=new SupplierMap();
            List<SupplierMap> loaded = smap.listAll(SupplierMap.class, TblSupplierMap.class.getSimpleName().concat(".findBySuppid"), "suppid", sid);
            lst = loaded;
        } catch (Exception e) {
            CMezoTools.ShowError(e);
        }
        return lst;
    }
    Integer suppid;
    Integer maincat;
    Integer itemid;
    boolean service;
    TblSupplierMap data;
    private Boolean mappedToMain;

    public SupplierMap() {
        mappedToMain = false;
    }


    public <T> Class<T> getDataType() {
        return (Class<T>) TblSupplierMap.class;
    }


    public void setData(Object d) {
        data = (TblSupplierMap) d;
        this.maincat = data.getMaincat();
        this.itemid = data.getItemid();
        this.id = data.getId();
        this.suppid = data.getSuppid();
        this.service = data.getIsService();
        mappedToMain = data.getMappedToMain();
    }


    public TblSupplierMap getData() {
        data = new TblSupplierMap();
        data.setMaincat(this.getMaincat());
        data.setItemid(this.getItemid());
        data.setId(this.getId());
        data.setSuppid(this.getSuppid());
        data.setIsService(this.isService());
        data.setMappedToMain(mappedToMain);
        return data;
    }

    public Integer getItemid() {
        return itemid;
    }

    public void setItemid(Integer itemid) {
        this.itemid = itemid;
    }

    public Integer getMaincat() {
        return maincat;
    }

    public void setMaincat(Integer maincat) {
        this.maincat = maincat;
    }

    public Integer getSuppid() {
        return suppid;
    }

    public void setSuppid(Integer suppid) {
        this.suppid = suppid;
    }


    public String toString() {
        String itemname = "<Not Set>";
        if (mappedToMain) {
            CCategory cat = new CCategory();
            cat.find(this.maincat);
            itemname = cat.getItem();
        } else {
            CProduct p= new CProduct();
            p.find(itemid);
            itemname = p.getItem();
        }
        return itemname;
    }


    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.suppid);
        hash = 53 * hash + Objects.hashCode(this.maincat);
        hash = 53 * hash + Objects.hashCode(this.itemid);
        return hash;
    }


    public boolean equals(Object comp) {
        if (comp instanceof SupplierMap ) {
            SupplierMap s = (SupplierMap) comp;
            
            if (s.isMappedToMain()) {
                if (s.getSuppid().equals(suppid)
                        && s.getMaincat().equals(maincat) && s.isService() == service) {
                    return true;
                }
            }
            if (s.isMappedToItem()) {
                if (s.getSuppid().equals(suppid)
                        && s.getItemid().equals(itemid) && s.isService() == service) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setService(boolean service) {
        this.service = service;
    }

    public boolean isService() {
        return service;
    }

    public boolean isMappedToMain() {
        return mappedToMain;
    }

    public void setMappedToMain(Boolean mm) {
        mappedToMain = mm;
    }

    public boolean isMappedToItem() {
        return !mappedToMain;
    }


    public boolean checkEntries() throws ValidationException {
        if (suppid <= 0) {
            throw new ValidationException("Supplier must be saved first, then mapped.");
        }
        if (isMappedToItem() && itemid <= 0) {
            throw new ValidationException("Items must be selected first. Cant save empty map.");
        }
        if (isMappedToMain() && this.maincat <= 0) {
            throw new ValidationException("Categories must be selected first. Cant save empty map.");
        }
        return true;
    }

    public static boolean resetMap(Integer suppid) {
        boolean r = MezoDB.doSqlIn("delete from " + TblSupplierMap.class.getSimpleName() + " where suppid=" + suppid);
        return r;
    }
}
