package com.rationalteam.rtreadymix.security;

import com.rationalteam.reaymixcommon.ServerMessage;
import com.rationalteam.rterp.erpcore.Utility;
import com.rationalteam.rtreadymix.data.Tblusers;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.awt.*;

@Path("/readymix")
public class UserManagerRes {
    @Inject
    @ResourcePath(("userman"))
    Template tmpuserman;

    @GET
    @Path("/usermanager")
    @RolesAllowed("admin")
    public TemplateInstance userman(){
        return tmpuserman.data("title","Users Manager")
                .data("icon","userman.png").data("users", UserManager.getUsers());
    }
    @Path("/userman")
    @POST
    @RolesAllowed("admin")
    @Transactional
    public void userman(@FormParam("username") String username,
                        @FormParam("curuserid") String curuserid,
                        @FormParam("userrole") String userrole,
                        @FormParam("userpassword") String password,
                        @FormParam("usermobile") String usermobile,
                        @FormParam("command") String command,
                        @FormParam("useremail") String email
    ) {
        ServerMessage smsg = new ServerMessage();
        try {
            boolean b;
            if (command != null) {
                if (command.equals("saveuser")) {
                    int userid = Integer.parseInt(curuserid);
                    Tblusers tuser = new Tblusers();
                    tuser.setId(userid);
                    tuser.setUsername(username);
                    tuser.setEmail(email);
                    tuser.setPhone(usermobile);
                    tuser.setRoles(userrole);
                    tuser.setPassword(password);
                    tuser.setUsertype(userrole);
                    if (userid > 0) {
                        b = UserManager.updateUser(tuser);
                    } else {
                        b = UserManager.add(tuser);
                    }
                    smsg.setMessage("User created/updated successfully For user id:" + userid);
                } else if (command.startsWith("removeuser")) {
                    if (command.contains("_")) {
                        String delid = command.split("_")[1];
                        b = UserManager.delete(Integer.parseInt(delid));
                        smsg.setMessage(b ? "User deleted successfully" : "There was an error while deleting user.");
                        smsg.setMessage(smsg.getMessage().concat(" For user id:" + delid));
                    }
                }
            }
            System.out.println(smsg.getMessage());
        } catch (Exception exp) {
            Utility.ShowError(exp);
        }
    }
}
