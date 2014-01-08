package com.cortez.wildfly.security.servlet;

import com.cortez.wildfly.security.CustomPrincipal;
import com.cortez.wildfly.security.ejb.SampleEJB;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Roberto Cortez
 */
@WebServlet(urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
    @Inject
    private SampleEJB sampleEJB;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            if (username != null && password != null) {
                request.login(username, password);
            }

            CustomPrincipal principal = (CustomPrincipal) request.getUserPrincipal();
            response.getWriter().println("principal=" + request.getUserPrincipal().getClass().getSimpleName());
            response.getWriter().println("username=" + sampleEJB.getPrincipalName());
            response.getWriter().println("description=" + principal.getDescription());

        } catch (ServletException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
