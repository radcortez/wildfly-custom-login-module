package com.cortez.wildfly.security;

import org.jboss.security.SimplePrincipal;

/**
 * @author Roberto Cortez
 */
public class CustomPrincipal extends SimplePrincipal {
    private String description;

    public CustomPrincipal(String name, String description) {
        super(name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
