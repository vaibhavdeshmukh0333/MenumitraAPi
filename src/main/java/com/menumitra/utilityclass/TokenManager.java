package com.menumitra.utilityclass;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import java.util.Properties;
import io.restassured.response.Response;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ResponseUtil;

public class TokenManager {
    private static Map<String, String> tokenStore = new HashMap<>();
    private static final Properties prop = new Properties();
    private static final String LOGOUT_ENDPOINT = "/api/v1/owner/logout"; // Update with your actual logout endpoint

    public static void setToken(String key, String value) {
        tokenStore.put(key, value);
    }

    public static String getToken(String key) {
        return tokenStore.get(key);
    }

    public static void clearTokens() {
        tokenStore.clear();
    }

    public static void setTokens(JSONObject response) {
        try {
            if (response.has("data")) {
                JSONObject data = response.getJSONObject("data");
                if (data.has("access_token")) {
                    setToken("access_token", data.getString("access_token"));
                }
                if (data.has("device_token")) {
                    setToken("device_token", data.getString("device_token"));
                }
                if (data.has("user_id")) {
                    setToken("user_id", data.getString("user_id"));
                }
                if (data.has("role")) {
                    setToken("user_role", data.getString("role"));
                }
            }
        } catch (Exception e) {
            LogUtils.error("Error setting tokens: " + e.getMessage());
        }
    }

    public static boolean logout() {
        try {
            String accessToken = getToken("access_token");
            String deviceToken = getToken("device_token");
            
            if (accessToken == null || deviceToken == null) {
                LogUtils.error("Cannot logout: Access token or Device token is missing");
                return false;
            }

            String logoutEndpoint = prop.getProperty("owner.logout.endpoint", "/api/v1/owner/logout");
            String baseUrl = EnviromentChanges.getBaseUrl();
            String logoutUrl = baseUrl + logoutEndpoint;

            // Create logout request
            JSONObject logoutRequest = new JSONObject();
            logoutRequest.put("device_token", deviceToken);

            // Make logout API call using your existing ResponseUtils with auth headers
            ResponseUtil.getResponseWithAuth(logoutUrl, logoutRequest, "POST", accessToken, deviceToken);
            
            // Clear tokens after successful logout
            clearTokens();
            LogUtils.info("Logout successful - Tokens cleared");
            return true;

        } catch (Exception e) {
            LogUtils.error("Error during logout: " + e.getMessage());
            return false;
        }
    }

    public static boolean isLoggedIn() {
        String accessToken = getToken("access_token");
        String deviceToken = getToken("device_token");
        return accessToken != null && deviceToken != null;
    }
} 