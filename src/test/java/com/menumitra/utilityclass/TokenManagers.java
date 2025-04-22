package com.menumitra.utilityclass;

import java.net.URL;

import org.json.JSONObject;

import com.menumitra.apiRequest.LogoutRequest;
import com.menumitra.apiRequest.loginRequest;
import com.menumitra.apiRequest.logoutRequest;
import com.menumitra.superclass.APIBase;

import io.restassured.response.Response;

public class TokenManagers
{
    private static String jwtToken; // JWT token for authentication
    private static String deviceToken; // Device token for device identification
    private static String userId; // User ID for user identification
    private static String role; // User role for access control
    private static String app; // App type for app-specific logic

    /**
     * Sets tokens and user information from a given response.
     * 
     * @param response The response from which to extract tokens and user information.
     */
    public static void setTokens(JSONObject response)
    {
        try {
            LogUtil.info("Starting to set tokens from response");
            JSONObject ownerData = response.getJSONObject("owner_data");
            
            LogUtil.info("Extracting JWT token from response");
            jwtToken = ownerData.getString("access"); // Setting JWT token
            
            LogUtil.info("Extracting device token from response");
            deviceToken = ownerData.getString("device_token"); // Setting device token
            
            LogUtil.info("Extracting user information from response");
            userId = String.valueOf(ownerData.getInt("user_id")); // Setting user ID
            role = ownerData.getString("role"); // Setting user role
            app = ownerData.getString("role"); // Setting app type based on role
            
            LogUtil.info("Token setup completed successfully - User ID: " + userId + ", Role: " + role);
        } catch (Exception e) {
            LogUtil.error("CRITICAL: Failed to set tokens - " + e.getMessage());
            LogUtil.error("Stack trace: " + e.getStackTrace());
            throw new RuntimeException("Failed to set tokens", e);
        }
    }

    /**
     * Clears all tokens and user information.
     */
    public static void clearTokens() {
        LogUtil.info("Starting token cleanup process");
        jwtToken = null; // Clearing JWT token
        deviceToken = null; // Clearing device token
        userId = null; // Clearing user ID
        role = null; // Clearing user role
        app = null; // Clearing app type
        LogUtil.info("All tokens and user information cleared successfully");
    }

    /**
     * Returns the JWT token.
     * 
     * @return The JWT token.
     */
    public static String getJwtToken() {
        if (jwtToken == null) {
            LogUtil.warn("JWT token is null - Authentication may fail");
        }
        return jwtToken;
    }

    /**
     * Returns the device token.
     * 
     * @return The device token.
     */
    public static String getDeviceToken() {
        if (deviceToken == null) {
            LogUtil.warn("Device token is null - Device identification may fail");
        }
        return deviceToken;
    }

    /**
     * Returns the user ID.
     * 
     * @return The user ID.
     */
    public static String getUserId() {
        if (userId == null) {
            LogUtil.warn("User ID is null - User identification may fail");
        }
        return userId;
    }

    /**
     * Returns the user role.
     * 
     * @return The user role.
     */
    public static String getRole() {
        if (role == null) {
            LogUtil.warn("Role is null - Role-based access may fail");
        }
        return role;
    }

    /**
     * Returns the app type.
     * 
     * @return The app type.
     */
    public static String getApp() {
        if (app == null) {
            LogUtil.warn("App type is null - App-specific logic may fail");
        }
        return app;
    }

    /**
     * Initiates the logout process by sending a request to the logout endpoint.
     */
    public static void logout()
    {
        try 
        {
            LogUtil.info("Starting logout process");
            
            if (jwtToken == null || deviceToken == null || userId == null) {
                LogUtil.warn("Missing required tokens for logout - JWT: " + (jwtToken != null) + 
                           ", Device: " + (deviceToken != null) + 
                           ", UserID: " + (userId != null));
                return;
            }

            LogUtil.info("Building logout request");
            String baseURI = EnviromentChange.getBaseUrl();
            String logoutEndpoint = APIBase.property.getProperty("logoutEndpoint");
            URL url = new URL(logoutEndpoint);
            baseURI = baseURI + "" + url.getPath();

            LogUtil.info("Preparing logout request parameters");
            LogoutRequest logoutRequest=new LogoutRequest();
            logoutRequest.setUser_id(userId);
            logoutRequest.setRole(role);
            logoutRequest.setApp(app);
            logoutRequest.setDevice_token(deviceToken);

            LogUtil.info("Sending logout request to: " + baseURI);
            Response response =ResponseUtil.getResponse(baseURI, logoutRequest, "post");

            if(response.getStatusCode() == 200) {
                LogUtil.info("Logout successful - Status: " + response.getStatusCode());
                clearTokens();
            } else {
                LogUtil.error("Logout failed - Status: " + response.getStatusCode() + 
                            ", Response: " + response.getBody().asString());
            }
            
        } catch (Exception e) {
            LogUtil.error("CRITICAL: Logout process failed - " + e.getMessage());
            LogUtil.error("Stack trace: " + e.getStackTrace());
            throw new RuntimeException("Logout process failed", e);
        }
    }

    /**
     * Initiates the login process by sending a request to the login endpoint.
     * @throws customException 
     */
    public static void login() throws customException
    {
        try 
        {
            LogUtil.info("Starting login process");
            
            LogUtil.info("Building login request");
            String baseURI = EnviromentChange.getBaseUrl();
            String loginEndpoint = APIBase.property.getProperty("loginEndpoint");
            URL url = new URL(loginEndpoint);
            baseURI = baseURI + "" + url.getPath();

            LogUtil.info("Preparing login request parameters");
            loginRequest login = new loginRequest();
            login.setMobile(APIBase.property.getProperty("mobile"));

            LogUtil.info("Sending login request to: " + baseURI);
            Response response = ResponseUtil.getResponse(baseURI, login, "post");
            if(response.getStatusCode()==200)
            {
            	LogUtil.info("Success: login in user");
            }
            else
            {
            	LogUtil.error("Error: login user");
            }
           
            
        } 
        catch (Exception e) {
            LogUtil.error("Error: Login process failed - " + e.getMessage());
            
            throw new customException("Login process failed"+ e.getMessage());
        }
    }
    
    /**
     * Verifies the OTP sent to the user's mobile number.
     * @param otp The OTP to be verified.
     * @throws customException If the OTP verification fails.
     */
    public static void verifyOtp(String otp) throws customException {
        try {
            LogUtil.info("Starting OTP verification process");
            
            LogUtil.info("Building OTP verification request");
            String baseURI = EnviromentChange.getBaseUrl();
            String otpVerificationEndpoint = APIBase.property.getProperty("otpVerificationEndpoint");
            URL url = new URL(otpVerificationEndpoint);
            baseURI = baseURI + "" + url.getPath();

            LogUtil.info("Preparing OTP verification request parameters");
            otpVerificationRequest otpVerification = new otpVerificationRequest();
            otpVerification.setOtp(otp);

            LogUtil.info("Sending OTP verification request to: " + baseURI);
            Response response = ResponseUtil.getResponse(baseURI, otpVerification, "post");
            if(response.getStatusCode()==200)
            {
                LogUtil.info("Success: OTP verified");
            }
            else
            {
                LogUtil.error("Error: OTP verification failed");
                throw new customException("OTP verification failed");
            }
        } catch (Exception e) {
            LogUtil.error("Error: OTP verification process failed - " + e.getMessage());
            throw new customException("OTP verification process failed" + e.getMessage());
        }
    }
}
