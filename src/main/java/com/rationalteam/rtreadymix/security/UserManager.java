package com.rationalteam.rtreadymix.security;

import com.rationalteam.rtreadymix.data.Tblusers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.transaction.Transactional;

import static io.quarkus.elytron.security.common.BcryptUtil.bcryptHash;

public class UserManager {
    /**
     * Adds a new user in the database
     * @param username the user name
     * @param password the unencrypted password (it will be encrypted with bcrypt)
     * @param role the comma-separated roles
     */
    @Transactional
    public static void add(String username, String password, String role) {
        Tblusers user = new Tblusers();
        user.setUsername(username);
        user.setPassword(bcryptHash(password));
        user.setRoles(role);
        user.persist();
    }
}
