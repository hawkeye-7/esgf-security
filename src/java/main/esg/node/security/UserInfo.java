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
   Container object to hold node user information
   (uses "fluent" mutator functions to make life easier when populating)

   Just from a lexicon standpoint:
   In SAML Parlance ---------------------  In User/Security Parlance
   (Attributes Type -> Attribute Value) => Permission (Group -> Role)
   
   The touple of (userid, group, role) = a "permission"

**/

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.*;

public class UserInfo {

    private static final Log log = LogFactory.getLog(UserInfo.class);

    private int id = -1;
    private String openid = null;
    private String firstName = null;
    private String middleName = "";
    private String lastName = null;
    private String userName = null;
    private String email = null;
    private String dn = "";
    private String organization = "";
    private String orgType = "";
    private String city = "";
    private String state = "";
    private String country = "";   
    private Map<String,Set<String>> permissions = null;    
    private Set<String> roleSet = null;

    //At package level visibility - on purpose :-)
    UserInfo() {}

    final public int getid() { return id;}
    final UserInfo setid(int id) {
        this.id = id;
        return this;
    }
    
    public final String getOpenid() { return openid; }
    final UserInfo setOpenid(String openid) {
        this.openid = openid;
        return this;
    }

    //--------------------------------------
    //Open, public accessors and mutators
    //--------------------------------------

    public String getFirstName() { return firstName; }
    public UserInfo setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getMiddleName() { return middleName; }
    public UserInfo setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }
    
    public String getLastName() { return lastName; }
    public UserInfo setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getUserName() { return userName; }
    public UserInfo setUserName(String userName) {
        this.userName = userName;
        return this;
    }
        
    public final String getEmail() { return email; }
    public final UserInfo setEmail(String email) {
        this.email = email;
        return this;
    }
    
    public String getDn() { return dn; }
    public UserInfo setDn(String dn) {
        this.dn = dn;
        return this;
    }
    
    public String getOrganization() { return organization; }
    public UserInfo setOrganization(String organization) {
        this.organization = organization;
        return this;
    }

    public String getOrgType() { return orgType; }
    public UserInfo setOrgType(String orgType) {
        this.orgType = orgType;
        return this;
    }

    public String getCity() { return city; }
    public UserInfo setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() { return state; }
    public UserInfo setState(String state) {
        this.state = state;
        return this;
    }

    public String getCountry() { return country; }
    public UserInfo setCountry(String country) {
        this.country = country;
        return this;
    }


    //--------------------------------------
    // Permissions (Groups and Roles) collecting
    //--------------------------------------

    public Map<String,Set<String>> getPermissions() {
        return permissions;
    }
    
    //At package level visibility on purpose
    UserInfo setPermissions(Map<String,Set<String>> permissions) {
        this.permissions = permissions;
        return this;
    }
    
    UserInfo addPermission(String group, String role) {
        //lazily instantiate permissions map
        if(permissions == null) {
            permissions = new HashMap<String,Set<String>>();
        }
        
        //lazily instantiate the set of values for group if not
        //there
        if((roleSet = permissions.get(group)) == null) {
            roleSet = new HashSet<String>();
        }
        
        //enter group associated with group value set
        roleSet.add(role);
        permissions.put(group, roleSet);
        return this;
    }
    
    //--------------------------------------
    
    //Check if this object is a "valid" object.  Valid objects must
    //have fulfill these conditions.
    public final boolean isValid() {
        return ((this.id > 0) && (this.openid != null) && (this.userName != null));
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n----------\nUserInfo...\n")
            .append("ID:\t"+id+"\n")
            .append("Open ID:\t"+openid+"\n")
            .append("First Name:\t"+firstName+"\n")
            .append("Middle Name:\t"+middleName+"\n")
            .append("Last Name:\t"+lastName+"\n")
            .append("User Name:\t"+userName+"\n")
            .append("Email:\t"+email+"\n")
            .append("Dn:\t"+dn+"\n")
            .append("Organization:\t"+organization+"\n")
            .append("Org Type:\t"+orgType+"\n")
            .append("City:\t"+city+"\n")
            .append("State:\t"+state+"\n")
            .append("Country:\t"+country+"\n")
            .append("Permissions (Groups and Roles):\n");
        
        if(this.permissions != null) {
            for(String groupName : permissions.keySet()) {
                sb.append("\t"+groupName+"\t");
                for(String role : permissions.get(groupName)) {
                    sb.append("\t"+role);
                }
                sb.append("\n");
            }
        }else {
            sb.append("-- no permissions -- :-(\n");
        }
        sb.append("\n----------\n");
        return sb.toString();
    }
    
}