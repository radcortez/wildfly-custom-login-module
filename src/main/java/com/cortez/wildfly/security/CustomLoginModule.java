package com.cortez.wildfly.security;

import org.jboss.security.auth.spi.UsersRolesLoginModule;

import javax.security.auth.login.LoginException;
import java.security.Principal;

/**
 * @author Roberto Cortez
 */
public class CustomLoginModule extends UsersRolesLoginModule {
    private CustomPrincipal principal;

    @Override
    public boolean login() throws LoginException {
        boolean login = super.login();
        if (login) {
            principal = new CustomPrincipal(getUsername(), "An user description!");
        }
        return login;
    }

    @Override
    protected Principal getIdentity() {
        return principal != null ? principal : super.getIdentity();
    }
}
