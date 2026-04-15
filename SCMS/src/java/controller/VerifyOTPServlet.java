package controller;

import util.DBConnection;
import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.util.UUID;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import util.EmailUtil;

@WebServlet("/VerifyOTPServlet")
public class VerifyOTPServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("=== VerifyOTPServlet Started ===");
        
        String enteredOtp = request.getParameter("otp");
        HttpSession session = request.getSession();

        String realOtp = (String) session.getAttribute("otp");
        Long expiry = (Long) session.getAttribute("otpExpiry");
        String email = (String) session.getAttribute("otpEmail");

        System.out.println("Entered OTP: " + enteredOtp);
        System.out.println("Real OTP: " + realOtp);
        System.out.println("Email: " + email);

        if(realOtp == null || expiry == null){
            System.out.println("ERROR: OTP session attributes missing");
            response.sendRedirect("register.jsp");
            return;
        }

        if(System.currentTimeMillis() > expiry){
            System.out.println("ERROR: OTP expired");
            response.sendRedirect("otp_verification.jsp?error=expired");
            return;
        }

        if(!realOtp.equals(enteredOtp)){
            System.out.println("ERROR: OTP mismatch");
            response.sendRedirect("otp_verification.jsp?error=invalid");
            return;
        }

        // OTP is correct! Update is_verified to 'Y'
        System.out.println("SUCCESS: OTP verified");
        
        try(Connection conn = DBConnection.getConnection()){
            
            System.out.println("Updating user verification status...");
            String updateSql = "UPDATE users SET is_verified = 'Y' WHERE email = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setString(1, email);
            int rows = updatePs.executeUpdate();
            
            System.out.println("Rows updated: " + rows);
            
            if(rows > 0) {
                System.out.println("User verified successfully");
                
                // Clear OTP from session
                session.removeAttribute("otp");
                session.removeAttribute("otpExpiry");
                session.removeAttribute("otpVerified");
                
                System.out.println("Redirecting to registration success page");
                response.sendRedirect("registration_success.jsp");
            } else {
                System.out.println("ERROR: Failed to update verification status");
                response.sendRedirect("otp_verification.jsp?error=failed");
            }
            
        } catch(Exception e){
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("otp_verification.jsp?error=system");
        }
        
        System.out.println("=== VerifyOTPServlet Ended ===");
    }
}