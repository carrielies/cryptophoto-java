package com.cryptophoto.demo.web;

import com.cryptophoto.CryptoPhotoInvalidSession;
import com.cryptophoto.CryptoPhotoResponseParseException;
import com.cryptophoto.CryptoPhotoUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Hashtable;
import java.util.Map;

import static com.cryptophoto.CryptoPhotoUtils.CryptoPhotoResponse;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

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

    private static final Map<String, String> DB = new Hashtable<String, String>(); // simulate a database

    private static final CryptoPhotoUtils cryptoPhoto;

    static {
        DB.put("Moria", "Mellon");
        DB.put("Erebor", "Durin's Day");
        DB.put("Redhorn", "Caradhras");

        try {
            cryptoPhoto = new CryptoPhotoUtils("efe925bda3bc2b5cd6fe3ad3661075a7", "384b1bda2dafcd909f607083da22fef0");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession();

        if (request.getParameter("logout") != null) { // allow sign out functionality upon '/login?logout'
            session.invalidate();
            response.sendRedirect("/login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        if (userId != null && userId.trim().length() > 0 && !TRUE.equals(session.getAttribute("authPending"))) {
            response.sendRedirect("/internal.jsp");
            return;
        }

        request.setAttribute("errorMessage", null);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession();
        CryptoPhotoResponse cryptoPhotoSession = null;
        boolean authPending = TRUE.equals(session.getAttribute("authPending"));

        // Check if user not already logged in:
        String userId = (String) session.getAttribute("userId");
        if (userId != null && userId.trim().length() > 0 && !authPending) {
            response.sendRedirect("/internal.jsp");
            return;
        }

        if (!authPending) { // surely we don't have a user id yet!

            // Check user id and password:
            String passWd = request.getParameter("passWd");
            passWd = passWd == null ? "" : passWd.trim();
            if ((userId = request.getParameter("userId")) == null || !passWd.equals(DB.get(userId = userId.trim()))) {
                loginFailed("The username or password you entered is not correct!", request, response);
                return;
            }

            // Establish a valid CryptoPhoto session:
            try {
                cryptoPhotoSession = cryptoPhoto.getSession(userId, CryptoPhotoUtils.getVisibleIp());
                if (!cryptoPhotoSession.is("valid")) {
                    loginFailed("Cannot obtain a valid CryptoPhoto session (" + cryptoPhotoSession.get("error") + ")!",
                                request, response);
                    return;
                }
            } catch (CryptoPhotoResponseParseException e) {
                loginFailed("Cannot create a CryptoPhoto session (" + e.getMessage() + ")!", request, response);
                return;
            }

            session.setAttribute("userId", userId);
            session.setAttribute("authPending", TRUE);
        }

        // Display CryptoPhoto widget (either token generation or challenge):
        String cryptoPhotoWidget = null;
        try {
            cryptoPhotoWidget = cryptoPhotoSession.has("token") ? cryptoPhoto.getChallengeWidget(cryptoPhotoSession)
                                                                : cryptoPhoto
                                    .getTokenGenerationWidget(cryptoPhotoSession);
        } catch (CryptoPhotoInvalidSession cryptoPhotoInvalidSession) {
            session.invalidate();
            loginFailed("CryptoPhoto session is not valid", request, response);
            return;
        }

        request.setAttribute("cryptoPhotoWidget", cryptoPhotoWidget);
        request.getRequestDispatcher("login.jsp").forward(request, response);

        // if (verify) {
        //     request.changeSessionId();
        //     response.sendRedirect("/internal.jsp");
        // }
    }

    protected void loginFailed(String errorMessage, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
