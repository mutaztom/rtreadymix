package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.DataManager;
import com.rationalteam.rterp.erpcore.HDataManager;
import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rtreadymix.data.Tblclients;
import com.rationalteam.utility.CMezoTools;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
    public List<Tblclients> getCLients() {
        DataManager.setEntityManager(eman);
        Client c = new Client();
        List<Client> lst = c.listItems();
        List<Tblclients> clist = new ArrayList<>();
        for (Client client : lst) {
            Object data = client.getData();
            clist.add((Tblclients) data);
        }
        return clist;
    }

    @Transactional
    public boolean addClient(Tblclients client) {
        Client c = new Client();
        if (client.getPassword() == null || client.getPassword().isEmpty())
            client.setPassword("123");
        c.setData(client);
        c.setItem(getneratePin(c));
        return c.save();
    }

    public String getneratePin(Client c) {
        Random random = new Random();
        int i = random.nextInt();
        return String.valueOf(i);
    }

    @Transactional
    public boolean isRegistered(Client client) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", client.getUsername());
        List<Object> filter = client.getFacade().filter(map);
        return !filter.isEmpty();
    }

    public boolean authenticate(Client client) {
        boolean r = isRegistered(client);
        if (r) {
            Client original = new Client();
            Map<String, Object> map = new HashMap<>();
            map.put("username", client.getUsername());
            List<Client> filter = original.filter(map);
            r = filter.get(0).equals(client);
        }
        return r;
    }
}
