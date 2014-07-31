package com.cryptophoto.demo.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "login", urlPatterns = {"/", "/login"})
public class LoginServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession();

        if (request.getParameter("logout") != null) {
            session.invalidate();
            response.sendRedirect("login");
            return;
        }

        if (session.getAttribute("user-id") != null) {
            response.sendRedirect("internal");
            return;
        }

        request.setAttribute("login-failed", false);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession();

        if (session.getAttribute("user-id") != null) {
            response.sendRedirect("internal");
            return;
        }

        String userId = request.getParameter("user-id");
        if (userId == null) {
            request.setAttribute("login-failed", true);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            session.setAttribute("user-id", userId);
            request.changeSessionId();
            response.sendRedirect("internal");
        }
    }
}
