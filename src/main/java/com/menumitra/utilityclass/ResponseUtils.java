package com.menumitra.utilityclass;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

public class ResponseUtils {
    
    public static Response getResponseWithAuth(String url, Object requestBody, String method, 
            String accessToken, String deviceToken) {
        try {
            RequestSpecification request = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .header("Device-Token", deviceToken);

            if (requestBody != null) {
                if (requestBody instanceof JSONObject) {
                    request.body(((JSONObject) requestBody).toString());
                } else {
                    request.body(requestBody);
                }
            }

            Response response;
            switch (method.toUpperCase()) {
                case "GET":
                    response = request.get(url);
                    break;
                case "POST":
                    response = request.post(url);
                    break;
                case "PUT":
                    response = request.put(url);
                    break;
                case "DELETE":
                    response = request.delete(url);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }

            LogUtils.info("API Response for " + url + ": " + response.getStatusCode());
            return response;

        } catch (Exception e) {
            LogUtils.error("Error in API call: " + e.getMessage());
            throw new RuntimeException("API call failed: " + e.getMessage());
        }
    }
} 