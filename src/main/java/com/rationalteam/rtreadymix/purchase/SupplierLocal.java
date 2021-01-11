package com.rationalteam.rtreadymix.purchase;

import com.rationalteam.core.license.DongleException;
import com.rationalteam.rterp.erpcore.*;


import javax.ejb.Local;
import javax.validation.ValidationException;
import java.util.List;

@Local

public interface SupplierLocal extends PersonLocal {

    static String getPersonName(Integer sid) {
        try {
            Object val = MezoDB.getValue("select longname from " + TblSupplier.class.getSimpleName() + " where id=" + sid);
            if (val != null) {
                return val.toString();
            }
        } catch (Exception exp) {
            Utility.ShowError(exp);
        }
        return "<Not Set>";
    }

    String getWebSite();

    String getAddress();

    boolean ISServiceProvider();

    boolean ISProductProvider();

    boolean isActive();

    Integer getType();

    void setType(Integer t);

    String getSupplierType();

    List<ContactLocal> getSupplierContacts();

    void setLongName(String name);

    void setWebSite(String webSite);

    void setAsServiceProvider(boolean value);

    void setServiceProvider(boolean value);

    void setAsProductsProvider(boolean value);

    void setProductsProvider(boolean value);

    void setActive(boolean value);

    void setSupplierContacts(List contacts);

    boolean isServiceProvider();

    boolean isProductProvider();

    void setProductProvider(boolean productProvider);

    List<SupplierMap> getSupplierMap();

    void setSupplierMap(List<SupplierMap> supplierMap);

    boolean ValidateEnteries();

    CurrencyLocal getCurrency();

    Integer getCurrencyid();

    void setCurrencyid(Integer currencyid);

    void setCurrency(CurrencyLocal currency);

    boolean isSupplyerOf(Integer itemid, boolean service, boolean iscat);

    boolean isSupplyerOf(Integer itemid, boolean service);

    void setPaymentMethod(Integer pm);

    Integer getPaymentMethod();

    abstract List<CBankInfo> getBankAccounts();

    void setBankAccounts(List<CBankInfo> bankAccounts);

    void addBankAccount(CBankInfo b);

    void addMap(SupplierMap m);

    void removeMap(SupplierMap m);

    boolean saveMap(Boolean update) throws DongleException, ValidationException;

    Boolean isMapped();

    List<CProduct> getMappedItems();

    List<ProductCategory> getMappedCategories();

    List<ProductCategory> getUnmappedCategories();

    List<CProduct> getUnmappedItems();

    ContactLocal getContact();

    void AddContact(ContactLocal cont);

    void deleteContact(ContactLocal cont);

    ContactLocal getContact(int cid);

    @Override
    boolean checkEntries() throws ValidationException;

    String getAritem();

    void setAritem(String aritem);

    String getRegno();

    void setRegno(String regno);

    String getAraddress();

    void setAraddress(String araddress);

    @Override
    String getLocation();

    @Override
    void setLocation(String location);
}
