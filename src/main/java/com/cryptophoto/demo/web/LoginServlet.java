package com.cryptophoto.demo.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * A few implementation options for authentication:
 * <ol>
 * <li>Use a {@link javax.servlet.Filter} to authenticate every request.</li>
 * <li>Implement the <a href="http://en.wikipedia.org/wiki/Front_Controller_pattern">Front Controller pattern</a> as
 * a Servlet.</li>
 * <li>Use a <a href="http://zeroturnaround.com/rebellabs/the-curious-coders-java-web-frameworks-comparison-spring
 * -mvc-grails-vaadin-gwt-wicket-play-struts-and-jsf/">Java web framework</a></li>
 * </ol>
 */
@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private static final Map<String, String> DB = new Hashtable<String, String>();

    static {
        DB.put("Moria", "Mellon");
        DB.put("Erebor", "Durin's Day");
        DB.put("Redhorn", "Caradhras");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession();

        if (request.getParameter("logout") != null) {
            session.invalidate();
            response.sendRedirect("/login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        if (userId != null && userId.trim().length() > 0) {
            response.sendRedirect("/internal.jsp");
            return;
        }

        request.setAttribute("loginFailed", false);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession();

        String userId = (String) session.getAttribute("userId");
        if (userId != null && userId.trim().length() > 0) {
            response.sendRedirect("/internal.jsp");
            return;
        }

        userId = request.getParameter("userId");

        String passWd = request.getParameter("passWd");
        passWd = passWd == null ? "" : passWd.trim();

        if (userId == null || !passWd.equals(DB.get(userId = userId.trim()))) {
            request.setAttribute("loginFailed", true);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            session.setAttribute("userId", userId);
            request.changeSessionId();
            response.sendRedirect("/internal.jsp");
        }
    }
}
