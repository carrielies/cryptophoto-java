package com.cryptophoto.demo.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "login", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession();

        if (request.getParameter("logout") != null) {
            session.invalidate();
            response.sendRedirect("login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        if (userId != null && userId.trim().length() > 0) {
            response.sendRedirect("internal.jsp");
            return;
        }

        request.setAttribute("loginFailed", false);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession();

        String sUserId = (String) session.getAttribute("userId");
        if (sUserId != null && sUserId.trim().length() > 0) {
            response.sendRedirect("internal.jsp");
            return;
        }

        String userId = request.getParameter("userId");
        if (userId == null || userId.trim().length() == 0) {
            request.setAttribute("loginFailed", true);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            session.setAttribute("userId", userId);
            request.changeSessionId();
            response.sendRedirect("internal.jsp");
        }
    }
}
