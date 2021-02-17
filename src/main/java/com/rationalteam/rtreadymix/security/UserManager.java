package com.rationalteam.rtreadymix.security;

import com.rationalteam.rterp.erpcore.MezoDB;
import com.rationalteam.rtreadymix.data.Tblusers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static io.quarkus.elytron.security.common.BcryptUtil.bcryptHash;

public class UserManager {
    /**
     * Adds a new user in the database
     *
     * @param username the user name
     * @param password the unencrypted password (it will be encrypted with bcrypt)
     * @param role     the comma-separated roles
     */
    @Transactional
    public static void add(String username, String password, String role) {
        Tblusers user = new Tblusers();
        user.setUsername(username);
        user.setPassword(bcryptHash(password));
        user.setRoles(role);
        user.persist();
    }

    @Transactional
    public static void add(Tblusers user) {
        user.persist();
    }

    @Transactional
    public static void updateUser(Tblusers user) {
        EntityManager eman = user.getEntityManager();
        MezoDB.setEman(eman);
        MezoDB.persistTable(user,true);
    }

    @Transactional
    public static List<Tblusers> getUsers() {
        List<Tblusers> users = MezoDB.open("select * from tblusers", Tblusers.class);
        return users;
    }

    @Transactional
    public static boolean delete(int uid) {
        boolean r = MezoDB.doSqlIn("delete from tblusers where username!='admin' and id=" + uid);
        return r;
    }
}
