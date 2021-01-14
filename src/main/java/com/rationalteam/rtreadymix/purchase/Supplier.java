package com.rationalteam.rtreadymix.purchase;

import com.rationalteam.core.license.DongleException;
import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rterp.erpcore.data.TblCity;
import com.rationalteam.rterp.erpcore.data.TblCountry;
import com.rationalteam.rterp.erpcore.data.TblProdcat;
import com.rationalteam.rterp.erpcore.data.TblProduct;
import com.rationalteam.rtreadymix.SystemConfig;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.persistence.NonUniqueResultException;
import javax.validation.ValidationException;
import java.util.*;

@Stateful
@Local(SupplierLocal.class)
public class Supplier extends Person implements SupplierLocal {

    private String address;
    private String website;
    private boolean serviceProvider;
    private boolean productProvider;
    private boolean active;
    @Browsable
    @Filterable
    protected String aritem;
    @Filterable
    protected String regno;
    protected String araddress;
    protected String location;

    @SpecialFilterable(dataField = "item", dataTable = "tblsuppliertype")
    private Integer type;
    private List<ContactLocal> m_contacts = new ArrayList<>();
    private List<SupplierMap> supplierMap;
    private Integer paymentMethod;
    TblSupplier data;
    protected Integer currencyid;
    protected Boolean mapped;
    @EJB
    private CurrencyLocal currency;
    @EJB
    ContactLocal contact;
    private List<CBankInfo> bankAccounts;
//Getters

    public String getLongName() {
        return item;
    }

    @Override
    public String getWebSite() {
        return website;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public boolean ISServiceProvider() {
        return serviceProvider;
    }

    @Override
    public boolean ISProductProvider() {
        return productProvider;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Integer getType() {
        return type;
    }

    @Override
    public void setType(Integer t) {
        type = t;
    }

    @Override
    public String getSupplierType() {
        if (type != null && type > 0) {
            COption o = new COption("tblsuppliertype");
            o.find(type);
            if (!o.isEmpty()) {
                return o.getItem();
            }
        }
        return "Not Set";
    }

    @Override
    public List<ContactLocal> getSupplierContacts() {
        List<ContactLocal> contlist = contact.find(this.getId(), "Supplier");
        if (contlist == null || contlist.isEmpty())
            contlist = Collections.EMPTY_LIST;
        return contlist;
    }
//Setters

    @Override
    public void setLongName(String name) {
        this.item = name;
    }

    @Override
    public void setWebSite(String webSite) {
        this.website = webSite;
    }

    @Override
    public void setAsServiceProvider(boolean value) {
        this.serviceProvider = value;
    }

    @Override
    public void setServiceProvider(boolean value) {
        this.serviceProvider = value;
    }

    @Override
    public void setAsProductsProvider(boolean value) {
        this.productProvider = value;
    }

    @Override
    public void setProductsProvider(boolean value) {
        this.productProvider = value;
    }

    @Override
    public void setActive(boolean value) {
        this.active = value;
    }

    @Override
    public void setSupplierContacts(List contacts) {
        this.m_contacts = contacts;
    }

    @Override
    public boolean isServiceProvider() {
        return serviceProvider;
    }

    @Override
    public boolean isProductProvider() {
        return productProvider;
    }

    @Override
    public void setProductProvider(boolean productProvider) {
        this.productProvider = productProvider;
    }

    @Override
    public List<SupplierMap> getSupplierMap() {
        supplierMap = new ArrayList<>();
        List<CRtDataObject> d = listAll(SupplierMap.class, "TblSupplierMap.findBySuppid", "suppid", id);
        for (CRtDataObject dob : d) {
            SupplierMap smap = (SupplierMap) dob;
            supplierMap.add(smap);
        }
        return supplierMap;
    }

    @Override
    public void setSupplierMap(List<SupplierMap> supplierMap) {
        this.supplierMap = supplierMap;
    }

    public Supplier() {
        item = "New Supplier";
        dbTable = "tblSupplier";
        supplierMap = new ArrayList<>();
        contact=new CContact();
        initSearch();
    }

    @PostConstruct
    private void init() {
        this.currency = SystemConfig.getDefaultCurrency();
        currencyid = SystemConfig.getDefaultCurrencyId();
        contact=new CContact();
    }


    @Override
    public void find(int findid) {
        super.find(findid);
        setSupplierMap(SupplierMap.load(findid));
        contacts = contact.find(findid, "Supplier");
    }

    @Override
    public boolean ValidateEnteries() {
        // This will check that the enteries for the supplier is true
        //check the email
        boolean valid = false;
        valid = true;
        return valid;
    }

    @Override
    public String toString() {
        return this.getLongName();
    }

    @Override
    public boolean hasContacts() {
        return this.getSupplierContacts().size() > 0;
    }

    @Override
    public boolean save() {
        boolean r = super.save();
        for (SupplierMap sm : supplierMap) {
            r = r && sm.save();
        }
        return r;
    }


    @Override
    public boolean delete() {
        boolean r = super.delete();
        for (ContactLocal c : contacts) {
            r = r && c.delete();
        }
        contacts.clear();
        //also clear material map
        r = SupplierMap.resetMap(id);
        supplierMap.clear();
        id = -1;
        return r;
    }


    @Override
    public void setData(Object d) {
        data = (TblSupplier) d;
        this.setId(data.getId());
        //TODO: REVISE THIS ACCOUNT SETTING OF SUPPLIER
        //this.accountNo=this.getAccount().setCode(null)
        this.setAddress(data.getAddress());
        this.setCountry(data.getCountry());
        this.setActive(data.getISActive());
        this.setAsProductsProvider(data.getISProductProv());
        this.setAsServiceProvider(data.getISServiceProv());
        this.setLongName(data.getLongName());
        this.setItem(data.getItem());
        this.setShortName(data.getShortName());
        this.setType(data.getType());
        this.setWebSite(data.getWebSite());
        this.currencyid = data.getCurrency();
        city = data.getCity();
        phone = data.getPhone();
        email = data.getEmail();
        fax = data.getFax();
        this.paymentMethod = data.getPaymentMethod();
        if (data.getPaymenttype() != null) {
            this.paymethod = enPaymentType.valueOf(data.getPaymenttype());
        }
        this.regno = data.getRegno();
        this.aritem = data.getAritem();
        this.araddress = data.getAraddress();
        this._owner = data.getOwner();
        this.location = data.getLocation();
    }

    @Override
    public TblSupplier getData() {
        data = new TblSupplier();
        data.setId(this.getId());
        data.setAddress(this.getAddress());
        data.setCountry(this.getCountry());
        data.setISActive(this.isActive());
        data.setISProductProv(this.ISProductProvider());
        data.setISServiceProv(this.ISServiceProvider());
        data.setLongName(this.getLongName());
        data.setItem(this.getItem());
        data.setShortName(this.getShortName());
        data.setType(this.getType());
        data.setWebSite(this.getWebSite());
        data.setCurrency(currencyid);
        data.setCity(city);
        data.setEmail(email);
        data.setFax(fax);
        data.setPhone(phone);
        if (paymethod != null) {
            data.setPaymenttype(this.getPaymentType().name());
        }
        data.setPaymentMethod(paymentMethod);
        data.setAritem(aritem);
        data.setAraddress(araddress);
        data.setRegno(regno);
        data.setOwner(_owner);
        data.setLocation(location);
        return data;
    }

    /**
     * @return the currency
     */
    @Override
    public CurrencyLocal getCurrency() {
        currency.find(currencyid);
        return currency;
    }

    @Override
    public Integer getCurrencyid() {
        return currencyid;
    }

    @Override
    public void setCurrencyid(Integer currencyid) {
        this.currencyid = currencyid;
    }

    /**
     * @param currency the currency to set
     */
    @Override
    public void setCurrency(CurrencyLocal currency) {
        this.currency = currency;
    }

    @Override
    public boolean isSupplyerOf(Integer itemid, boolean service, boolean iscat) {
        Map<Integer, Object> param = new HashMap<>();
        param.put(1, id);//suppid
        param.put(2, itemid);//mainid or itemid
        param.put(3, service);//isservice
        param.put(4, iscat);//mappedtomain
        Object open = MezoDB.getValue("select count(id) from tblsuppliermap"
                + " where suppid=? and " + (iscat ? "maincat=" : "itemid=") + "? and isservice=? and mappedtomain=?", param);
        if (open != null) {
            int r = (int) open;
            return r > 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean isSupplyerOf(Integer itemid, boolean service) {
        Map<Integer, Object> param = new HashMap<>();
        param.put(1, id);//suppid
        param.put(2, itemid);//mainid or itemid
        param.put(3, service);//isservice
        param.put(4, false);//mappedtomain
        //first check if directly bound to material
        Object open = MezoDB.getValue("select suppid from tblsuppliermap"
                + " where suppid=? and itemid=? and isservice=? and mappedtomain=?", param);
        if (open != null) {
            return true;
        } else {
            param.put(4, true);
            open = MezoDB.getValue("select suppid from tblsuppliermap"
                    + " where suppid=? and maincat=? and isservice=? and mappedtomain=?", param);
            return (open != null);
        }
    }

    @Override
    public void setPaymentMethod(Integer pm) {
        this.paymentMethod = pm;
    }

    @Override
    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public List<CBankInfo> getBankAccounts() {
        bankAccounts = new ArrayList<>();
        CBankInfo info = new CBankInfo();
        List<CRtDataObject> binf = info.getBankInfo(this);
        for (CRtDataObject ob : binf) {
            CBankInfo b = (CBankInfo) ob;
            bankAccounts.add(b);
        }
        return bankAccounts;
    }

    @Override
    public void setBankAccounts(List<CBankInfo> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    @Override
    public void addBankAccount(CBankInfo b) {
        bankAccounts.add(b);
    }

    @Override
    protected void initSearch() {
        searchOptions = new ArrayList<>();
        addSearchOption("id", "id");
        addSearchOption("Name", "item");
        CSearchOption scity = new CSearchOption("City", "city");
        scity.setRelational(true);
        scity.setRealtblfield("item");
        scity.setReltblClass(TblCity.class);
        addSearchOption(scity);
        CSearchOption scont = new CSearchOption("Country", "country");
        scont.setRelational(true);
        scont.setRealtblfield("item");
        scont.setReltblClass(TblCountry.class);
        addSearchOption(scont);
    }

    @Override
    public List filter(String fld, Object val) {
        return super.filter(fld, val); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addMap(SupplierMap m) {
        if (supplierMap.contains(m)) {
            Utility.ShowError("This Mapped material already exisits!");
            return;
        }
        m.setSuppid(id);
        supplierMap.add(m);
    }

    @Override
    public void removeMap(SupplierMap m) {
        supplierMap.remove(m);
    }

    /**
     * This save method lacks the cabablity to mix mapping mode, it can either
     * map in Category Level or Item Level Only.
     */
    @Override
    public boolean saveMap(Boolean update) throws DongleException, ValidationException {
        Boolean r = false;
        if (update) {
            MezoDB.doSqlIn("delete from " + TblSupplierMap.class.getSimpleName() + " where suppid=" + id);
        }
        for (SupplierMap m : supplierMap) {
            try {
                if (m.checkEntries()) {
                    r = update ? m.edit() : m.save();
                }
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        }
        return r;
    }

    @Override
    public Boolean isMapped() {
        Integer m = MezoDB.getInteger("select count(id) from tblsuppliermap where suppid=" + id);
        mapped = (m > 0);
        return mapped;

    }

    @Override
    public List<CProduct> getMappedItems() {
        List<CProduct> mappedItems = new ArrayList<>();
        for (SupplierMap map : supplierMap) {
            if (map.isMappedToItem()) {
                CProduct mapped = new CProduct();
                mapped.find(map.getItemid());
                mappedItems.add(mapped);
            }
        }
        return mappedItems;
    }

    @Override
    public List<ProductCategory> getMappedCategories() {
        List<ProductCategory> mappedCat = new ArrayList<>();
        for (SupplierMap map : supplierMap) {
            if (map.isMappedToMain()) {
                ProductCategory cat = new ProductCategory();
                cat.find(map.getMaincat());
                mappedCat.add(cat);
            }
        }
        return mappedCat;
    }

    @Override
    public List<ProductCategory> getUnmappedCategories() {
        List<ProductCategory> unMappedCat = new ArrayList<>();
        try {
            List<TblProdcat> open = MezoDB.open("select * from tblprodcat where id not in(select maincat from tblsuppliermap where suppid="
                    + id + " and maincat is not null)", TblProdcat.class);
            for (TblProdcat dat : open) {
                ProductCategory c = new ProductCategory();
                c.setData(dat);
                unMappedCat.add(c);
            }
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return unMappedCat;
    }

    /**
     * @return list of unmapped items. It will also check if main cat of the
     * item is not included in previous map.
     */
    @Override
    public List<CProduct> getUnmappedItems() {
        List<CProduct> unMappedItems = new ArrayList<>();
        try {
            List<TblProduct> itemlst = MezoDB.open("select * from tblproduct where id not in(select itemid from tblsuppliermap where suppid="
                    + id + " and itemid is not null) and tblproduct.maincat not in(select maincat from tblsuppliermap where suppid="
                    + id + " and maincat is not null)", TblProduct.class);
            for (TblProduct dat : itemlst) {
                CProduct c = new CProduct();
                c.setData(dat);
                unMappedItems.add(c);
            }
        } catch (Exception e) {
            Utility.ShowError(e);
        }
        return unMappedItems;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.id);
        hash = 23 * hash + Objects.hashCode(this.item);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SupplierLocal other = (SupplierLocal) obj;
        if (other.getId() == id && other.getItem().equals(item)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ContactLocal getContact() {
        contact.setAddress(address);
        contact.setEmail(email);
        contact.setPhone(phone);
        contact.setFax(fax);
        if (country != null) {
            contact.setCountry(getCountry());
        }
        contact.setWebsite(website);
        contact.setName(item);
        contact.setOwnertype(enOwnerType.Supplier);
        return contact;
    }

    @Override
    public void AddContact(ContactLocal cont) {
        cont.setOwnerid(id);
        contacts.add(cont);
    }

    @Override
    public void deleteContact(ContactLocal cont) {
        contacts.remove(cont);
        cont.delete();
    }

    @Override
    public ContactLocal getContact(int cid) {
        for (ContactLocal cont : contacts) {
            if (cont.getId().equals(cid)) {
                return cont;
            }
        }
        return null;
    }

    @Override
    public boolean checkEntries() throws ValidationException {
        if (item == null || item.isEmpty()) {
            throw new ValidationException("Suplier name must be set first.");
        }
        if (country == null || country < 0) {
            throw new ValidationException("Please select supplier country");
        }
        if (country == null || country < 0) {
            throw new ValidationException("Please select supplier country");
        }
        if (id < 0) {
            try {
                if (exists()) {
                    throw new ValidationException("Supplier with the same name exists, please type a new name.");
                }
            } catch (NonUniqueResultException nonu) {
                throw new ValidationException("Supplier with the same name exists, please type a new name.");
            }
        }
        return true;
    }

    @Override
    public boolean exists() {
        if (item != null && !item.isEmpty()) {
            return getIdFromItem(item) > 0;
        }
        return false;
    }

    @Override
    public ArrayList<Document> getDocuments() {
        return null;
    }

    @Override
    public <T> Class<T> getDataType() {
        return (Class<T>) TblSupplier.class;
    }

    @Override
    public String getAritem() {
        return aritem;
    }

    @Override
    public void setAritem(String aritem) {
        this.aritem = aritem;
    }

    @Override
    public String getRegno() {
        return regno;
    }

    @Override
    public void setRegno(String regno) {
        this.regno = regno;
    }

    @Override
    public String getAraddress() {
        return araddress;
    }

    @Override
    public void setAraddress(String araddress) {
        this.araddress = araddress;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }
}
