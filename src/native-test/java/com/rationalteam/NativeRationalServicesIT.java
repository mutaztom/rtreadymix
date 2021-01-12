package com.rationalteam;

import com.rationalteam.rtreadymix.security.UserManager;
import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeRationalServicesIT extends RationalServicesTest {
    // Execute the same tests but in native mode.
    public void addUser(){
        UserManager uman=new UserManager();
        uman.add("admin","admin","admin");
        uman.add("user","user","user");
    }
}