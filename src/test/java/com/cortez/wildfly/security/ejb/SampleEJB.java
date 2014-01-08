package com.cortez.wildfly.security.ejb;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;

/**
 * @author Roberto Cortez
 */
@Stateless
public class SampleEJB {
    @Resource
    private EJBContext ejbContext;

    @RolesAllowed("user")
    public String getPrincipalName() {
        return ejbContext.getCallerPrincipal().getName();
    }
}
