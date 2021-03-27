/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rationalteam.rtreadymix.purchase;

import com.rationalteam.rterp.erpcore.CRtDataObject;
import com.rationalteam.rterp.erpcore.MezoDB;

import java.util.List;

/**
 *
 * @author mutaz
 */
public class SupplierIndexConfig extends CRtDataObject {

    private int pvolumeIndex;
    private int deliveryIndex;
    private int rejectIndex;
    private int paymentIndex;
    private int qualityIndex;
    private int commitmentIndex;
    TblSupplierIndexConfig data;
/*
    To insure singletone of config we load last config
    */
    public SupplierIndexConfig() {
        Integer lastId = MezoDB.getLastId(TblSupplierIndexConfig.class.getSimpleName());
        if(lastId>0)
            find(lastId);
    }


    public <T> Class<T> getDataType() {
        return (Class<T>) TblSupplierIndexConfig.class;
    }

    public int getPvolumeIndex() {
        return pvolumeIndex;
    }

    public void setPvolumeIndex(int pvolumeIndex) {
        this.pvolumeIndex = pvolumeIndex;
    }

    public int getDeliveryIndex() {
        return deliveryIndex;
    }

    public void setDeliveryIndex(int deliveryIndex) {
        this.deliveryIndex = deliveryIndex;
    }

    public int getRejectIndex() {
        return rejectIndex;
    }

    public void setRejectIndex(int rejectIndex) {
        this.rejectIndex = rejectIndex;
    }

    public int getPaymentIndex() {
        return paymentIndex;
    }

    public void setPaymentIndex(int paymentIndex) {
        this.paymentIndex = paymentIndex;
    }

    public int getQualityIndex() {
        return qualityIndex;
    }

    public void setQualityIndex(int qualityIndex) {
        this.qualityIndex = qualityIndex;
    }


    public Object getData() {
        data = new TblSupplierIndexConfig();
        data.setId(id);
        data.setDeliveryIndex(deliveryIndex);
        data.setItem(item);
        data.setPaymentIndex(paymentIndex);
        data.setPvolumeIndex(pvolumeIndex);
        data.setQualityIndex(qualityIndex);
        data.setRejectIndex(rejectIndex);
        data.setCommitmentindex(commitmentIndex);
        return data;
    }


    public void setData(Object _data) {
        data = (TblSupplierIndexConfig) _data;
        id = data.getId();
        item = data.getItem();
        paymentIndex = data.getPaymentIndex();
        pvolumeIndex = data.getPvolumeIndex();
        qualityIndex = data.getQualityIndex();
        rejectIndex = data.getRejectIndex();
        commitmentIndex=data.getCommitmentindex();
    }

    public void reset() {
        List<CRtDataObject> lst = listAll(SupplierIndexConfig.class, TblSupplierIndexConfig.class.getSimpleName().concat(".findByItem"), "item", "default config");
        if(lst!=null && !lst.isEmpty())
            setData(lst.get(0));
    }

    public int getCommitmentIndex() {
        return commitmentIndex;
    }

    public void setCommitmentIndex(int commitmentIndex) {
        this.commitmentIndex = commitmentIndex;
    }

}
