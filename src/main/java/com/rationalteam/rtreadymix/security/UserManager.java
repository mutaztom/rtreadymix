package com.rationalteam.rtreadymix.security;

import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rtreadymix.data.Tblusers;

import javax.transaction.Transactional;

import java.util.List;

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
    public static boolean add(Tblusers user) {
        try {
            user.persist();
            return true;
        } catch (Exception exp) {
            Utility.ShowError(exp);
            return false;
        }
    }

    @Transactional
    public static boolean updateUser(Tblusers user) {
        int c = 0;
        //if password is set then validate and update
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            c = Tblusers.update("username=?1, email=?2,phone=?3,roles=?4,usertype=?5, password=?6 where id=" + user.getId(),
                    user.getUsername(), user.getEmail(), user.getPhone(), user.getRoles(), user.getUsertype(), bcryptHash(user.getPassword()));
        } else {
            c = Tblusers.update("username=?1, email=?2,phone=?3,roles=?4,usertype=?5  where id=" + user.getId(),
                    user.getUsername(), user.getEmail(), user.getPhone(), user.getRoles(), user.getUsertype());
        }
        return c > 0;
    }

    @Transactional
    public static List<Tblusers> getUsers() {
        return Tblusers.listAll();
    }

    @Transactional
    public static boolean delete(int uid) {
        Tblusers user = Tblusers.findById(uid);
        if (user != null) {
            user.delete();
            return true;
        }
        return false;
    }

    @Transactional
    public static boolean changePassword(int uid, String password) {
        Tblusers user = Tblusers.findById(uid);
        int c = 0;
        if (user != null) {
            c = Tblusers.update("password =?1 where id=" + uid, bcryptHash(password));
        }
        return c > 0;
    }
}
