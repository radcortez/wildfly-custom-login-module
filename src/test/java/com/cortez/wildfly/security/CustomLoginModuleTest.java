package com.cortez.wildfly.security;

import com.cortez.wildfly.security.ejb.SampleEJB;
import com.cortez.wildfly.security.servlet.LoginServlet;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.scriptsupport.CLI;
import org.jboss.as.protocol.StreamUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class CustomLoginModuleTest {
    @ArquillianResource
    private URL deployUrl;

    @AfterClass
    public static void removeSecurityDomain() {
        processCliFile(new File("src/test/resources/jboss-remove-login-module.cli"));
    }

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        processCliFile(new File("src/test/resources/jboss-add-login-module.cli"));

        WebArchive war = ShrinkWrap.create(WebArchive.class)
                                   .addClass(CustomPrincipal.class)
                                   .addClass(CustomLoginModule.class)
                                   .addClass(SampleEJB.class)
                                   .addClass(LoginServlet.class)
                                   .addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-web.xml"), "jboss-web.xml")
                                   .addAsWebInfResource("jboss-ejb3.xml")
                                   .addAsResource("user.properties")
                                   .addAsResource("roles.properties");
        System.out.println(war.toString(true));
        return war;
    }

    @Test
    public void testLogin() throws IOException, SAXException {
        WebConversation webConversation = new WebConversation();
        GetMethodWebRequest request = new GetMethodWebRequest(deployUrl + "LoginServlet");
        request.setParameter("username", "username");
        request.setParameter("password", "password");
        WebResponse response = webConversation.getResponse(request);
        assertTrue(response.getText().contains("principal=" + CustomPrincipal.class.getSimpleName()));
        assertTrue(response.getText().contains("username=username"));
        assertTrue(response.getText().contains("description=An user description!"));
        System.out.println(response.getText());
    }

    @Test
    public void testInvalidLogin() throws IOException, SAXException {
        try {
            WebConversation webConversation = new WebConversation();
            GetMethodWebRequest request = new GetMethodWebRequest(deployUrl + "LoginServlet");
            request.setParameter("username", "invalid");
            request.setParameter("password", "invalid");
            webConversation.getResponse(request);
            assertTrue(false);
        } catch (HttpException e) {
            assertEquals(403, e.getResponseCode());
        }
    }

    private static void processCliFile(File file) {
        CLI cli = CLI.newInstance();
        cli.connect("localhost", 9990, null, null);
        CommandContext commandContext = cli.getCommandContext();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (commandContext.getExitCode() == 0 && !commandContext.isTerminated() && line != null) {
                commandContext.handleSafe(line.trim());
                line = reader.readLine();
            }
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to process file '" + file.getAbsolutePath() + "'", e);
        } finally {
            StreamUtils.safeClose(reader);
        }
    }
}
