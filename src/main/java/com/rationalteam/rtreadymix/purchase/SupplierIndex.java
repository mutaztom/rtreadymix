/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rationalteam.rtreadymix.purchase;

import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.utility.CMezoTools;

/**
 *
 * @author mutaz
 */
public class SupplierIndex {

    private int entityId;
    private double credibility;
//  private double _purchase;
    private double commitment;
    private double payment;
    private double overall;
    //-----------------------
    private double orders;//CNTINVC
    private double purchasevolume;
    private double credit;
    private double debit;
    private int retCheck;//CNTRIF
    private int passCheck;//CNTINVCPD
    private boolean isEmpty;
    private int supplierid;
    private double quality;

    public SupplierIndex(int supid) {
        supplierid = supid;
        calculate();
    }

    /**
     * @param entityId the entityId to set
     */
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    /**
     * @return the credibility
     */
    public double getCredibility() {
        return credibility;
    }

    /**
     * @param credibility the credibility to set
     */
    public void setCredibility(double credibility) {
        this.credibility = credibility;
    }

//    /**
//     * @return the purchase
//     */
//    public double getPurchase() {
//        return purchase;
//    }
//
//    /**
//     * @param purchase the purchase to set
//     */
//    public void setPurchase(double purchase) {
//        this.purchase = purchase;
//    }
    /**
     * @return the commitment to deliver items on expected delivery date
     */
    public double getCommitment() {
        return commitment;
    }

    /**
     * @param commitment the commitment to deliver items on expected delivery
     * date
     */
    public void setCommitment(double commitment) {
        this.commitment = commitment;
    }

    /**
     * @return the orders
     */
    public double getOrders() {
        return orders;
    }

    /**
     * @param orders the orders to set
     */
    public void setOrders(double orders) {
        this.orders = orders;
    }

    /**
     * @return the amount
     */
    public double getPurchaseVolume() {
        return purchasevolume;
    }

    /**
     * @param amount the amount to set
     */
    public void setPurchaseVolume(double amount) {
        this.purchasevolume = amount;
    }

    /**
     * @return the credit
     */
    public double getCredit() {
        return credit;
    }

    /**
     * @param credit the credit to set
     */
    public void setCredit(double credit) {
        this.credit = credit;
    }

    /**
     * @return the debit
     */
    public double getDebit() {
        return debit;
    }

    /**
     * @param debit the debit to set
     */
    public void setDebit(double debit) {
        this.debit = debit;
    }

    /**
     * @return the retCheck
     */
    public int getRetCheck() {
        return retCheck;
    }

    /**
     * @param retCheck the retCheck to set
     */
    public void setRetCheck(int retCheck) {
        this.retCheck = retCheck;
    }

    /**
     * @return the passCheck
     */
    public int getPassCheck() {
        return passCheck;
    }

    /**
     * @param passCheck the passCheck to set
     */
    public void setPassCheck(int passCheck) {
        this.passCheck = passCheck;
    }

    /**
     * @return the payment
     */
    public double getPayment() {
        return payment;
    }

    /**
     * @param payment the payment to set
     */
    public void setPayment(double payment) {
        this.payment = payment;
    }

    /**
     * @return the overall This is to make the maximum scale for each is limited
     * by 25 as they are four key values calculated out of 100
     */
    public double getOverall() {
        overall = this.commitment + this.credibility 
                + this.purchasevolume+ this.payment+orders;
        return overall;
    }

    public void setSupplierIndex(int supid) {
        supplierid = supid;
        calculate();
    }

    public final void calculate() {
        try {
            SupplierIndexConfig conf = new SupplierIndexConfig();
            //calculate purchase volume
            Object pv = MezoDB.getValue("select sum(total) from tblpurorder where supplier=" + supplierid);
            purchasevolume = pv == null ? 0.0 : Double.valueOf(pv.toString());
            purchasevolume /= conf.getPvolumeIndex() * 1000.0;
            //calculate orders
            Object ov = MezoDB.getValue("select count(id) from tblpurorder where supplier=" + supplierid);
            orders = ov == null ? 0.0 : Double.valueOf(ov.toString());
            //calculate credibility: deviation on delivery dates
            Integer cv = MezoDB.getInteger("select count(id) from tblpurorder where supplier=" + supplierid);
            credibility = pv == null ? 0.0 : Double.valueOf(cv.toString());
            credibility = 100;
            //calculate quality
            Object qv = MezoDB.getValue("select sum(rejectedquantity) as rejection from tblPurOrder join "
                    + "tbldeliveryitem on tbldeliveryitem.orderid=tblpurorder.id where supplier=" + supplierid);
            double q = qv == null ? 0.0 : Double.valueOf(qv.toString());
            q /= (orders > 0 ? orders : 1.0);
            quality=100-q;
            //calculate commitment
            Object com = MezoDB.getValue("select sum(datediff(day,ondate,expectedon))"
                    + " from tblPOrderDelivery left join tblpurorder "
                    + "on tblpurorder.id=tblporderdelivery.docid where supplierid=" + supplierid);
            double c = com == null ? 0.0 : Double.valueOf(com.toString());
            c /= (orders > 0 ? orders : 1.0);
            c/=conf.getCommitmentIndex();
            commitment=100-c;
            //calculate payment method index in flexibility
            Integer flex = MezoDB.getInteger("select tblPayment.afterDays from tblsupplier"
                    + " join tblPayment on paymentmethod=tblPayment.id "
                    + "where tblsupplier.id=" + supplierid);
            payment = flex;
            payment /= conf.getPaymentIndex() == 0 ? 1 : conf.getPaymentIndex();
        } catch (NumberFormatException ex) {
            CMezoTools.ShowError(ex);
        }
    }

    public int getSupplierid() {
        return supplierid;
    }

    public double getQuality() {
        return quality;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

}
