package com.cryptophoto.demo.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI().substring(req.getContextPath().length()).toLowerCase();
        HttpSession session = req.getSession(false);

        if (!path.startsWith("/css/") &&
            !path.startsWith("/login") &&
            (session == null || session.getAttribute("userId") == null)) {
            ((HttpServletResponse) response).sendRedirect("/login");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {}
}
