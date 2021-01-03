package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.DataManager;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rtreadymix.data.Tblclient;
import com.rationalteam.utility.CMezoTools;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.*;

@ApplicationScoped
public class ClientManager implements Serializable {
    @Inject
    EntityManager eman;

    @PostConstruct
    private void init() {
        DataManager.setEntityManager(eman);
        DataManager.USINGHIBERNATE = false;

        CMezoTools.useWebMode = true;
        MezoDB.setEman(eman);
    }

    public ClientManager() {

    }

    @Transactional
    public List<Tblclient> getCLients() {
        DataManager.setEntityManager(eman);
        Client c = new Client();
        List<Client> lst = c.listItems();
        List<Tblclient> clist = new ArrayList<>();
        for (Client client : lst) {
            Object data = client.getData();
            clist.add((Tblclient) data);
        }
        return clist;
    }

    @Transactional
    public boolean addClient(Tblclient client) {
        Client c = new Client();
        if (client.getPassword() == null || client.getPassword().isEmpty())
            client.setPassword("123");
        c.setData(client);
        c.setItem(generatePin(c));
        return c.save();
    }

    @Transactional
    public boolean addClient(Client client) {
        if (client.getPassword() == null || client.getPassword().isEmpty())
            client.setPassword("123");
        //generate client pin
        client.setPincode(generatePin(client));
        return client.save();
    }

    public String generatePin(Client c) {
        Random random = new Random();
        int i = random.nextInt();
        return String.valueOf(i);
    }

    public boolean authenticate(Client client) {
        boolean r = client.isRegistered();
        if (r) {
            Client original = new Client();
            Map<String, Object> map = new HashMap<>();
            map.put("email", client.getEmail());
//            List<Client> filter = original.filter(map);
//            r = filter.get(0).equals(client);
            MezoDB.setEman(eman);
            int cid = MezoDB.getInteger("select id from tblclient where email='" + client.getEmail() + "' and password='" + client.getPassword() + "'");
            System.out.println("Login log: is user " + client.getEmail() + " auth? " + Boolean.toString(r));
            return cid > 0;
        }
        return r;
    }

    public boolean isAuthentic(String clientid) {
        boolean result = false;
        Client c = Client.findByEmail(clientid);
        result = (c != null) && c.isVerfied();
        return result;
    }

    public String getMobile(String clientid) {
        MezoDB.setEman(eman);
        Object mobile=MezoDB.getValue("select mobile from tblclient where email='"+clientid+"'");
        return mobile==null?"None":mobile.toString();
    }

}
