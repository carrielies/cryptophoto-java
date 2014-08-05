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
 * A few authentication implementation options:
 * <ol>
 * <li>Use a {@link javax.servlet.Filter} to authenticate every request (like in this example).</li>
 * <li>Implement the <a href="http://en.wikipedia.org/wiki/Front_Controller_pattern">Front Controller pattern</a> as
 * a Servlet.</li>
 * <li>Use a <a href="http://zeroturnaround.com/rebellabs/the-curious-coders-java-web-frameworks-comparison-spring
 * -mvc-grails-vaadin-gwt-wicket-play-struts-and-jsf/">Java web framework</a></li>
 * </ol>
 */
@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private static final Map<String, String> DB = new Hashtable<String, String>(); // simulate a database; synchronized

    private static final CryptoPhotoUtils cryptoPhoto; // immutable, hence thread-safe

    static {
        DB.put("Root", "Root");          // has no generated tokens
        DB.put("Admin", "Admin");        // has no generated tokens
        DB.put("Moria", "Mellon");       // has already a generated token
        DB.put("Erebor", "Durin's Day"); // has already a generated token
        DB.put("Redhorn", "Caradhras");  // has already a generated token

        try {
            cryptoPhoto = new CryptoPhotoUtils("efe925bda3bc2b5cd6fe3ad3661075a7", "384b1bda2dafcd909f607083da22fef0");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e); // should not happen with correct keys
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {        // when requested directly, using a URL
        HttpSession session = request.getSession();

        if (request.getParameter("logout") != null) { // allow sign out functionality upon '/login?logout'
            session.invalidate();
            response.sendRedirect("/login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        if (userId != null && userId.trim().length() > 0 && !TRUE.equals(session.getAttribute("authPending"))) {
            response.sendRedirect("/internal.jsp");   // here, the user has already authenticated
            return;
        }

        request.setAttribute("errorMessage", null);   // here, we simply display the login page
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {        // when requested through form submission
        HttpSession session = request.getSession();
        boolean authPending = TRUE.equals(session.getAttribute("authPending"));
        CryptoPhotoResponse cryptoPhotoSession, cryptoPhotoVerification;

        // Check if the user hasn't already logged in:
        String userId = (String) session.getAttribute("userId");
        if (userId != null && (userId = userId.trim()).length() > 0 && !authPending) {
            response.sendRedirect("/internal.jsp");
            return;
        }

        String ip = CryptoPhotoUtils.getVisibleIp();

        if (!authPending) { // at this moment, for sure we don't have a user id yet...

            // Check user id and password:
            String passWd = request.getParameter("passWd");
            passWd = passWd == null ? "" : passWd.trim();
            if ((userId = request.getParameter("userId")) == null || !passWd.equals(DB.get(userId = userId.trim()))) {
                loginFailed("The username or password you entered is not correct! ( Try Moria/Mellon ;) )", request,
                            response);
                return;
            }

            // Establish a valid CryptoPhoto session:
            try {
                cryptoPhotoSession = cryptoPhoto.getSession(userId, ip);
                if (!cryptoPhotoSession.is("valid")) {
                    loginFailed("CryptoPhoto session is not valid (" + cryptoPhotoSession.get("error") + ")!", request,
                                response);
                    return;
                }
            } catch (CryptoPhotoResponseParseException e) {
                loginFailed("Cannot create a CryptoPhoto session (" + e.getMessage() + ")!", request, response);
                return;
            }

            session.setAttribute("userId", userId);
            session.setAttribute("authPending", TRUE);

            // Display CryptoPhoto widget (either token generation or challenge):
            String cryptoPhotoWidget;
            try {
                if (cryptoPhotoSession.has("token")) {
                    cryptoPhotoWidget = cryptoPhoto.getChallengeWidget(cryptoPhotoSession);
                } else {
                    cryptoPhotoWidget = cryptoPhoto.getTokenGenerationWidget(cryptoPhotoSession);
                    session.invalidate(); // simplify a bit the flow... after token generation, the user must re-log in
                }
            } catch (CryptoPhotoInvalidSession e) {
                session.invalidate();
                loginFailed("CryptoPhoto session is not valid (" + e.getMessage() + ")!", request, response);
                return;
            }

            request.setAttribute("cryptoPhotoWidget", cryptoPhotoWidget);
            request.getRequestDispatcher("login.jsp").forward(request, response);

        } else { // the user has just responded to the challenge:

            String responseRow = request.getParameter("token_response_field_row");
            String responseCol = request.getParameter("token_response_field_col");
            String selector = request.getParameter("token_selector");
            String cph = request.getParameter("cp_phc");

            try {
                cryptoPhotoVerification = cryptoPhoto.verify(selector, responseRow, responseCol, cph, userId, ip);
            } catch (Throwable e) {
                session.invalidate();
                loginFailed("CryptoPhoto verification failed (" + e.getMessage() + ")! Please try to login again.",
                            request, response);
                return;
            }

            if (cryptoPhotoVerification.is("valid")) {
                session.setAttribute("authPending", FALSE);
                request.changeSessionId();
                response.sendRedirect("/internal.jsp");
            } else {
                session.invalidate();
                loginFailed("CryptoPhoto verification failed (" + cryptoPhotoVerification.get("error") + ")!", request,
                            response);
            }
        }
    }

    protected void loginFailed(String errorMessage, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setAttribute("userId", null);
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("cryptoPhotoWidget", null);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
