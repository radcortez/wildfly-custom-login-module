package com.cortez.wildfly.security.arquillian;

import org.jboss.arquillian.container.spi.event.container.AfterUnDeploy;
import org.jboss.arquillian.container.spi.event.container.BeforeDeploy;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.TestClass;

/**
 * @author Roberto Cortez
 */
public class DeployExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.observer(Handler.class);
    }

    public static class Handler {
        public void executeAfterUnDeploy(@Observes BeforeDeploy event, TestClass testClass) {
            try {
                testClass.getMethod(com.cortez.wildfly.security.arquillian.BeforeDeploy.class).invoke(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void executeAfterUnDeploy(@Observes AfterUnDeploy event, TestClass testClass) {
            try {
                testClass.getMethod(com.cortez.wildfly.security.arquillian.AfterUnDeploy.class).invoke(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
