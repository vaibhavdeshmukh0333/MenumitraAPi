package com.menumitra.utilityclass;
import java.net.URL;
import org.json.JSONObject;
import com.google.gson.JsonObject;
import com.menumitra.apiRequest.LogoutRequest;
import com.menumitra.apiRequest.loginRequest;
import com.menumitra.apiRequest.verifyOTPRequest;
import com.menumitra.superclass.APIBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class TokenManagers
{
    private static String jwtToken; // JWT token for authentication
    private static String deviceToken; // Device token for device identification
    private static String userId; // User ID for user identification
    private static String role; // User role for access control
    private static String app; // App type for app-specific logic
    static JSONObject acutalresponse;
    
    

   

    public static void setDeviceToken(String deviceToken) {
		TokenManagers.deviceToken = deviceToken;
	}

	public static void setUserId(String userId) {
		TokenManagers.userId = userId;
	}

	public static void setRole(String role) {
		TokenManagers.role = role;
	}

	public static void setApp(String app) {
		TokenManagers.app = app;
	}
    /**
     * Returns the JWT token.
     * 
     * @return The JWT token.
     */
    public static String getJwtToken() 
    {
        if (jwtToken == null)
        {
            LogUtils.warn("JWT token is null - Authentication may fail");
        }
        return jwtToken;
    }

    /**
     * Returns the device token.
     * 
     * @return The device token.
     */
    public static String getDeviceToken()
    {
        if (deviceToken == null)
        {
            LogUtils.warn("Device token is null - Device identification may fail");
        }
        return deviceToken;
    }

    /**
     * Returns the user ID.
     * 
     * @return The user ID.
     */
    public static String getUserId() 
    {
        if (userId == null)
        {
            LogUtils.warn("User ID is null - User identification may fail");
        }
        return userId;
    }

    /**
     * Returns the user role.
     * 
     * @return The user role.
     */
    public static String getRole() 
    {
        if (role == null)
        {
            LogUtils.warn("Role is null - Role-based access may fail");
        }
        return role;
    }

    /**
     * Returns the app type.
     * 
     * @return The app type.
     */
    public static String getApp() 
    {
        if (app == null) 
        {
            LogUtils.warn("App type is null - App-specific logic may fail");
        }
        return app;
    }
    /**
     * Clears all tokens and user information.
     */
    public static void clearTokens() {
        LogUtils.info("Starting token cleanup process");
        jwtToken = null; // Clearing JWT token
        deviceToken = null; // Clearing device token
        userId = null; // Clearing user ID
        role = null; // Clearing user role
        app = null; // Clearing app type
        LogUtils.info("All tokens and user information cleared successfully");
    }
    /**
     * Initiates the logout process by sending a request to the logout endpoint.
     */
    public static void logout()
    {
        try 
        {
            LogUtils.info("Starting logout process");
            LogoutRequest logoutRequest=new LogoutRequest();
            if(getRole().contains("manager"))
            {
            	logoutRequest.setApp("pos");
            }
            else
            {
            	logoutRequest.setApp(getApp());
            }
            logoutRequest.setDevice_token(getDeviceToken());
            logoutRequest.setRole(getRole());
            logoutRequest.setUser_id(getUserId());
            
            String baseURI=EnviromentChanges.getBaseUrl()+APIBase.property.getProperty("logoutEndpoint");
            Response response=ResponseUtil.getResponse(baseURI,logoutRequest,"post");
            if(response.getStatusCode()==200)
            {
            	LogUtils.info("Suucess: Logout user");
            }
            
           
        } catch (Exception e) {
            LogUtils.error("CRITICAL: Logout process failed - " + e.getMessage());
            LogUtils.error("Stack trace: " + e.getStackTrace());
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
            LogUtils.info("Starting login process");
            
            LogUtils.info("Building login request");
            String baseURI = EnviromentChanges.getBaseUrl();
            String loginEndpoint = APIBase.property.getProperty("loginEndpoint");
            
            baseURI = baseURI + ""+loginEndpoint;

            LogUtils.info("Preparing login request parameters");
            loginRequest login = new loginRequest();
            login.setMobile(APIBase.property.getProperty("mobile"));

            LogUtils.info("Sending login request to: " + baseURI);
            Response response = ResponseUtil.getResponse(baseURI, login, "post");
            System.out.println(response.getStatusCode());
            if(response.getStatusCode()==200)
            {
            	LogUtils.info("Success: login in user");
            }
            else
            {
            	LogUtils.error("Error: login user");
            }
           
            
        } 
        catch (Exception e) {
            LogUtils.error("Error: Login process failed - " + e.getMessage());
            
            throw new customException("Login process failed"+ e.getMessage());
        }
    }
    
    /**
     * Verifies the OTP sent to the user's mobile number.
     * @param otp The OTP to be verified.
     * @throws customException If the OTP verification fails.
     */
    public static void verifyOtp() throws customException {
        try {
            LogUtils.info("Starting OTP verification process");
            
            LogUtils.info("Building OTP verification request");
            String baseURI = EnviromentChanges.getBaseUrl();
            String otpVerificationEndpoint = APIBase.property.getProperty("verifyOtpPathParameter_owner");
            baseURI = baseURI + otpVerificationEndpoint;

            LogUtils.info("Preparing OTP verification request parameters");
                
                verifyOTPRequest verifyOTPRequest = new verifyOTPRequest();
                verifyOTPRequest.setMobile("9869869869");
                verifyOTPRequest.setOtp("1234");
                verifyOTPRequest.setFcm_token("8998998998");
                verifyOTPRequest.setDevice_id("h0ol74eq6rom9i826wa");
                verifyOTPRequest.setDevice_model("samsung");


            Response response = ResponseUtil.getResponse(baseURI, verifyOTPRequest, "post");
            
            if (response.getStatusCode() == 200) {
                JSONObject actualResponse = new JSONObject(response.getBody().asString()); // FIXED
                if(actualResponse.has("owner_data"))
                {
                	JSONObject data = actualResponse.has("owner_data")
                            ? actualResponse.getJSONObject("owner_data")
                            : actualResponse;
                	setJwtToken(data.getString("access"));
                	setDeviceToken(data.getString("device_token")); 
                    setRole(data.getString("role"));
                    setApp(data.getString("role"));
                    setUserId(String.valueOf(data.getInt("user_id")));
                                    }
                                    else
                                    {
                                        setJwtToken(actualResponse.getString("access"));
                                        setDeviceToken(actualResponse.getString("device_token")); 
                                        setRole(actualResponse.getString("role"));
                                        setApp(actualResponse.getString("role"));
                                        setUserId(actualResponse.getString("user_id"));
                                    }
                                } 
                                if (response.getStatusCode()==400) 
                                {
                                    JSONObject js=new JSONObject(response.toString());
                                    LogUtils.error("Error: OTP verification failed with response " + response.getBody().asString());
                                    LogUtils.error("Error: OTP verification failed with status code " + response.getStatusCode());
                                    LogUtils.error("Response: " + response.getBody().asString()); // helpful for debugging
                                    throw new customException("OTP verification failed with status code " + response.getStatusCode());
                                }
                            } catch (Exception e) {
                                LogUtils.error("Error: OTP verification process failed - " + e.getMessage());
                                throw new customException("OTP verification process failed" + e.getMessage());
                            }
                        }
                    
                        private static String valueOf(int int1) {
                            // TODO Auto-generated method stub
                            throw new UnsupportedOperationException("Unimplemented method 'valueOf'");
                        }
                    
                        public static void setJwtToken(String jwtToken) {
		TokenManagers.jwtToken = jwtToken;
	}

	
}
