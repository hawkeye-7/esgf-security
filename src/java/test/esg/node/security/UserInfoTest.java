/***************************************************************************
 *                                                                          *
 *  Organization: Lawrence Livermore National Lab (LLNL)                    *
 *   Directorate: Computation                                               *
 *    Department: Computing Applications and Research                       *
 *      Division: S&T Global Security                                       *
 *        Matrix: Atmospheric, Earth and Energy Division                    *
 *       Program: PCMDI                                                     *
 *       Project: Earth Systems Grid Federation (ESGF) Data Node Software   *
 *  First Author: Gavin M. Bell (gavin@llnl.gov)                            *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *   Copyright (c) 2009, Lawrence Livermore National Security, LLC.         *
 *   Produced at the Lawrence Livermore National Laboratory                 *
 *   Written by: Gavin M. Bell (gavin@llnl.gov)                             *
 *   LLNL-CODE-420962                                                       *
 *                                                                          *
 *   All rights reserved. This file is part of the:                         *
 *   Earth System Grid Federation (ESGF) Data Node Software Stack           *
 *                                                                          *
 *   For details, see http://esgf.org/esg-node/                             *
 *   Please also read this link                                             *
 *    http://esgf.org/LICENSE                                               *
 *                                                                          *
 *   * Redistribution and use in source and binary forms, with or           *
 *   without modification, are permitted provided that the following        *
 *   conditions are met:                                                    *
 *                                                                          *
 *   * Redistributions of source code must retain the above copyright       *
 *   notice, this list of conditions and the disclaimer below.              *
 *                                                                          *
 *   * Redistributions in binary form must reproduce the above copyright    *
 *   notice, this list of conditions and the disclaimer (as noted below)    *
 *   in the documentation and/or other materials provided with the          *
 *   distribution.                                                          *
 *                                                                          *
 *   Neither the name of the LLNS/LLNL nor the names of its contributors    *
 *   may be used to endorse or promote products derived from this           *
 *   software without specific prior written permission.                    *
 *                                                                          *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
 *   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
 *   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
 *   FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL LAWRENCE    *
 *   LIVERMORE NATIONAL SECURITY, LLC, THE U.S. DEPARTMENT OF ENERGY OR     *
 *   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,           *
 *   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT       *
 *   LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF       *
 *   USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND    *
 *   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,     *
 *   OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT     *
 *   OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF     *
 *   SUCH DAMAGE.                                                           *
 *                                                                          *
 ***************************************************************************/
package esg.node.security;

/**
   Description:

   Testing the UserInfo and UserInfoDAO objects

**/

import static esg.common.Utils.getFQDN;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import esg.security.utils.encryption.MD5HexPasswordEncoder;
import esg.security.utils.encryption.PasswordEncoder;


public class UserInfoTest {

    private static final Log log = LogFactory.getLog(UserInfoTest.class);
    //private static UserInfoDAO userInfoDAO = null;
    private static UserInfoCredentialedDAO userInfoDAO = null;
    private static GroupRoleDAO groupRoleDAO = null;
    private static UserInfo gavin = null;
    
    private static PasswordEncoder encoder;
 
    @BeforeClass
    public static void initTest() {
        System.out.println("UserInfoTest initializing");
        
        //userInfoDAO = new UserInfoDAO(new Properties());
        userInfoDAO = new UserInfoCredentialedDAO("rootAdmin", "esgrocks",new Properties());
        groupRoleDAO = new GroupRoleDAO(new Properties());

        assertNotNull(userInfoDAO);
        assertNotNull(groupRoleDAO);

        assertTrue(groupRoleDAO.addGroup("CMIP5_test"));
        assertTrue(groupRoleDAO.addGroup("ARM_test"));
        assertTrue(groupRoleDAO.addRole("user_test"));
        
        
        gavin = userInfoDAO.getUserById("bell51");
        assertNotNull(gavin);
        if(gavin.isValid()) {
            System.out.println("Apparently gavin is present in the system!");
        }else {
            gavin.setFirstName("Gavin").
                setMiddleName("Max").
                setLastName("Bell").
                setEmail("gavin@llnl.gov").
                setOrganization("LLNL").
                setCity("Livermore").
                setState("California").
                setCountry("USA").
                addPermission("CMIP5_test","admin").
                addPermission("CMIP5_test","user_test").
                addPermission("ARM_test","user_test");
        }
        assertNotNull(gavin);
        System.out.println(gavin);
        
        // retrieve PasswordEncoder directly from UserInfoDAO.
        encoder = userInfoDAO.getEncoder();
    }
    
    @AfterClass
    public static void testCleanup() {
        System.out.println("------------------------");
        System.out.println("UserInfoTest Cleanup....");
        System.out.println("------------------------");

        log.info("\nDeleting Gavin user object...");
        if(userInfoDAO.deleteUserInfo(gavin)) System.out.println("[OK]"); else System.out.println("[FAIL]");
        
        groupRoleDAO.deleteRole("user_test_renamed"); //changed name from "user_test"
        groupRoleDAO.deleteGroup("ARM_test");
        groupRoleDAO.deleteGroup("CMIP_NOW"); //changed name from "CMIP5_test"

        groupRoleDAO.deleteGroup("CMIP6_test");
        groupRoleDAO.deleteGroup("CMIP7_test");
        groupRoleDAO.deleteRole("lord");
        groupRoleDAO.deleteRole("king");
        
        assertFalse(userInfoDAO.deleteUserInfo(userInfoDAO.getUserById("rootAdmin")));
        assertFalse(userInfoDAO.deleteUserInfo(userInfoDAO.getUserById("root<Admin>")));
    }

    @Test
    public void testPassword() {
        System.out.print("Adding user "+gavin.getUserName()+" id="+gavin.getid()+" openid="+gavin.getOpenid()+": ");
        if(userInfoDAO.addUserInfo(gavin)) {
            System.out.println("[OK]");
            
            String origPassword = "foobar";
            String newPassword = "foobaralpha";

            String origPasswordMd5Hex = encoder.encrypt(origPassword);
            String newPasswordMd5Hex  = encoder.encrypt(newPassword);
            
            System.out.print("Setting password: ["+origPassword+" -> "+origPasswordMd5Hex+"]");
            if(userInfoDAO.setPassword(gavin.getOpenid(),origPassword)) {
                System.out.println("[OK]");
            }else {
                System.out.println("[FAIL]");
            }

            System.out.print("Checking password: ");
            if(userInfoDAO.checkPassword(gavin.getOpenid(),origPassword)) {
                System.out.println("[OK]");
            }else {
                System.out.println("[FAIL]");
                fail();
            }

            System.out.print("Able to change password: ");
            System.out.print("["+origPassword+" -> "+origPasswordMd5Hex+"]");
            System.out.print("["+newPassword+" -> "+newPasswordMd5Hex+"]");
            if(userInfoDAO.changePassword(gavin.getOpenid(),origPassword,newPassword)) {
                System.out.println("[OK]");
            }else{
                System.out.println("[FAIL]");
                fail("Problem with changing password!");
            }

            System.out.print("Checking password mismatch: ["+origPassword+" != "+newPassword+"]");
            if(userInfoDAO.checkPassword(gavin.getOpenid(),origPassword)) {
                System.out.println("[FAIL]");
                fail("Sorry, Passwords Should Not Match for this case");
            }else {
                //This should fail! since the password is now foobaralpha! (right!?)
                System.out.println("[OK]");
            }
              
            System.out.print("Checking admin's password against default: ");
            if(userInfoDAO.checkPassword(userInfoDAO.getUserById("rootAdmin"),"esgrocks")) {
                System.out.println("[MATCH]");
            }else {
                System.out.println("[NO MATCH]");
            }

            System.out.print("Checking admin's password against mismatch: ");
            if(userInfoDAO.checkPassword(userInfoDAO.getUserById("rootAdmin"),"esgrockS")) {
                System.out.println("[MATCH]");
            }else {
                System.out.println("[NO MATCH]");
            }

        }else{
            System.out.println("[FAIL]");
            fail();
        }
    }
    
    @Test
    public void testGetUser() {
        UserInfo dean = userInfoDAO.getUserById("williams13");
        assertNotNull(dean);
        if(dean.isValid()) {
            System.out.println("Apparently dean is present in the system!");
        }else {
            dean.setFirstName("Dean").
                setMiddleName("N").
                setLastName("Williams").
                setUserName("williams13").
                setEmail("dean@llnl.gov").
                setDn("O=LLNL/OU=ESGF").
                setOrganization("LLNL").
                setOrgType("Research").
                setCity("Livermore").
                setState("California").
                setCountry("USA").
                addPermission("CMIP5_test","admin").
                addPermission("ARM_test","user_test");
        }
        System.out.println(dean);

        boolean success = false;
        System.out.print("\nAdding Fresh Dean User To Database...");
        if(userInfoDAO.addUserInfo(dean)) System.out.println("[OK]"); else { System.out.println("[FAIL]"); fail(); }
        System.out.print(dean);
        
        System.out.println("\nPulling Out Dean User From Database...");
        dean = userInfoDAO.getUserById("https://"+getFQDN()+"/esgf-idp/openid/williams13");

        System.out.println("\nModifying Dean user object...(middle name and email)");
        dean.setMiddleName("Neill");
        dean.setEmail("williams13@llnl.gov");
        System.out.println(dean);

        System.out.print("\nResubmitting Dean User To Database...");
        if(userInfoDAO.addUserInfo(dean)) System.out.println("[OK]"); else { System.out.println("[FAIL]"); fail(); }

        //Hint: we only support openid URLs using httpS protocol (among other things)
        System.out.println("\nIntentionally making a BAD call to getUserByID...");
        dean = userInfoDAO.getUserById("http://"+getFQDN()+"/esgf-idp/openid/williams13");
        assertNull(dean);
        if(dean == null) System.out.println("[OK]"); else { System.out.println("[FAIL]"); fail(); }


        System.out.println("\nPulling Out Dean User From Database After Modifications, Using Openid...");
        dean = userInfoDAO.getUserById("https://"+getFQDN()+"/esgf-idp/openid/williams13");
        if(dean != null) System.out.println("[OK]"); else { System.out.println("[FAIL]"); fail(); }
        System.out.println(dean);

        System.out.print("\nRe-Adding SAME Dean user object to database...");
        if(userInfoDAO.addUserInfo(dean)) System.out.println("[OK]"); else { System.out.println("[FAIL]"); fail(); }

        System.out.println("\nDeleting Dean user object...");
        if(userInfoDAO.deleteUserInfo(dean)) System.out.println("[OK]"); else { System.out.println("[FAIL]"); }
        
    }

    @Test
    public void testSetPermissions() {
        
        groupRoleDAO.addGroup("CMIP6_test");
        groupRoleDAO.addGroup("CMIP7_test");

        groupRoleDAO.addRole("god");
        groupRoleDAO.addRole("king");

        UserInfo bob = userInfoDAO.getUserById("drach1");
        if(bob.isValid()) {
            System.out.println("\nApparently drach1 is present in the system!");
        }else{
            System.out.println("\nCreating Fresh Bob User");
            bob.setFirstName("Bob").
                setLastName("Drach").
                setUserName("drach1").
                setEmail("bob@llnl.gov").
                setDn("O=LLNL/OU=ESGF").
                setOrganization("LLNL").
                setOrgType("Research").
                setCity("Livermore").
                setState("California").
                setCountry("USA").
                addPermission("CMIP5_test","admin").
                addPermission("CMIP5_test","user_test").
                addPermission("CMIP6_test","god").
                addPermission("CMIP6_test","king").
                addPermission("CMIP6_test","admin").
                addPermission("CMIP7_test","admin");
        }
        
        System.out.println(bob);
        
        System.out.print("Adding Bob into database... ");
        if(userInfoDAO.addUserInfo(bob)) System.out.println("[OK]"); else System.out.println("[FAIL]");

        System.out.print("Renaming group CMIP5_test -> CMIP_NOW...");
        if(groupRoleDAO.renameGroup("CMIP5_test","CMIP_NOW")) 
            System.out.println("[OK]"); else { System.out.println("[FAIL]"); fail(); }

        System.out.print("Renaming role god -> lord...");
        if(groupRoleDAO.renameRole("god","lord")) 
            System.out.println("[OK]"); else { System.out.println("[FAIL]"); fail(); }

        System.out.print("Renaming role user_test -> user_test_renamed...");
        if(groupRoleDAO.renameRole("user_test","user_test_renamed"))
            System.out.println("[OK]"); else { System.out.println("[FAIL]"); fail(); }

        System.out.println("Refreshing Bob user object... ");
        bob = userInfoDAO.refresh(bob);
        System.out.println(bob);
        
        System.out.println("\nDeleting Bob user object...");
        if(userInfoDAO.deleteUserInfo(bob)) System.out.println("[OK]"); else System.out.println("[FAIL]");
    }

    @Test
    public void testStatus() {
        log.info("Status testing...");
        log.info("Before set status for gavin (status should be 1)");
        log.info(gavin);
        assertTrue(userInfoDAO.setStatusCode(gavin,UserInfo.DISABLED)); //set status code in database not in user info object
        log.info("*gavin.getStatusCode() should be "+UserInfo.ACTIVE+" = "+gavin.getStatusCode());
        assertTrue((UserInfo.ACTIVE == gavin.getStatusCode()));
        userInfoDAO.refresh(gavin); //reload state directly from database so now see accurate database state including status code
        log.info("After set status for gavin (status should be "+UserInfo.DISABLED+")");
        log.info(gavin);
        log.info("Setting verification token for gavin");
        String generatedVerificationToken = userInfoDAO.genVerificationToken(gavin);
        String currentVerificationToken = userInfoDAO.getVerificationToken(gavin);
        log.info("Generated Verification Token = "+generatedVerificationToken);
        log.info("Current Verification Token   = "+currentVerificationToken);
        assertTrue(generatedVerificationToken.equals(currentVerificationToken));
        log.info("Changed gavin's status (from "+UserInfo.DISABLED+" to "+UserInfo.ACTIVE+") using BAD token (should still be "+UserInfo.DISABLED+")");
        log.info("gavin.getStatusCode() = "+gavin.getStatusCode());
        assertFalse(userInfoDAO.changeStatus(gavin, UserInfo.ACTIVE, "bad_token_value"));
        log.info(gavin);
        log.info("Changed gavin's status (from "+UserInfo.DISABLED+" to "+UserInfo.ACTIVE+") using GOOD token");
        assertTrue(userInfoDAO.changeStatus(gavin, UserInfo.ACTIVE, currentVerificationToken));
        userInfoDAO.refresh(gavin); //reload state directly from database        
        log.info(gavin);
        log.info("Create new blank userInfo...");
        UserInfo lola = userInfoDAO.getNewUserInfo();
        log.info(lola);
        lola.setUserName("lola");
        lola.setLastName("Bell");
        lola.setFirstName("Lola");
        lola.setEmail("lola@6thcolumn.org");
        lola.setOpenid("https://sixthcolumn.org/esgf-idp/openid/lola");
        log.info("Now has username (lola), right?)");
        log.info(lola);
        assertTrue(userInfoDAO.addUser(lola));
        log.info("New Verification Token for Lola: "+userInfoDAO.genVerificationToken(lola));
        log.info(lola);
        assertTrue(userInfoDAO.deleteUserInfo(lola));
    }
    
}
