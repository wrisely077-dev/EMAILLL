package controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LoginVerifyOTPServlet")
public class LoginVerifyOTPServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("=== LoginVerifyOTPServlet Started ===");

        String enteredOtp = request.getParameter("otp");
        HttpSession session = request.getSession();

        String realOtp = (String) session.getAttribute("loginOtp");
        Long expiry = (Long) session.getAttribute("loginOtpExpiry");

        System.out.println("Entered OTP: " + enteredOtp);
        System.out.println("Real OTP: " + realOtp);

        if(realOtp == null || expiry == null){
            System.out.println("ERROR: OTP session missing");
            response.sendRedirect("login.jsp");
            return;
        }

        if(System.currentTimeMillis() > expiry){
            System.out.println("ERROR: OTP expired");
            response.sendRedirect("login_otp_verification.jsp?error=expired");
            return;
        }

        if(!realOtp.equals(enteredOtp)){
            System.out.println("ERROR: OTP mismatch");
            response.sendRedirect("login_otp_verification.jsp?error=invalid");
            return;
        }

        System.out.println("SUCCESS: Login OTP verified");

        // OTP verified - set user session and redirect to dashboard
        int userId = (Integer) session.getAttribute("loginOtpUserId");
        int roleId = (Integer) session.getAttribute("loginOtpRoleId");
        String email = (String) session.getAttribute("loginOtpEmail");

        session.setAttribute("user_id", userId);
        session.setAttribute("role_id", roleId);
        session.setAttribute("email", email);

        // Remove OTP session attributes
        session.removeAttribute("loginOtp");
        session.removeAttribute("loginOtpExpiry");

        System.out.println("Redirecting to dashboard. Role ID: " + roleId);

        if (roleId == 1) {
            response.sendRedirect("student_dashboard.jsp");
        } else if (roleId == 2) {
            response.sendRedirect("staff_dashboard.jsp");
        } else {
            response.sendRedirect("admin_dashboard.jsp");
        }
        
        System.out.println("=== LoginVerifyOTPServlet Ended ===");
    }
}