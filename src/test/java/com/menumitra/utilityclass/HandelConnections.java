package com.menumitra.utilityclass;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;

public class HandelConnections {
	private static URL url;
	private static HttpURLConnection httpurlconnection;
	
	
	public static String checkConnection(String endpoint)  
	{
			int maxTime = 3000;  // Connection timeout
	        int intervalTime = 3000;  // Read timeout
	        int retries = 3;  // Number of retries if the connection is slow or there is an issue
	        int retryInterval = 5000;  // Time to wait between retries (in milliseconds)
	        HttpURLConnection httpURLConnection = null;
	        URL url = null;

	        try {
	            url = new URL(endpoint);
	            int attempt = 0;

	            while (attempt < retries) {
	                try {
	                    httpURLConnection = (HttpURLConnection) url.openConnection();
	                    httpURLConnection.setConnectTimeout(maxTime);
	                    httpURLConnection.setReadTimeout(intervalTime);

	                    // Check the response code
	                    int responseCode = httpURLConnection.getResponseCode();

	                    if (responseCode == 200) {
	                        return "Connection established successfully";
	                    } else if (responseCode == 500) {
	                        return "Server unable to handle the request.";
	                    } else if (responseCode == 503) {
	                        return "Server is down.";
	                    } else {
	                        return "Unexpected response code: " + responseCode;
	                    }
	                } catch (IOException e) {
	                    // Retry logic for connection failure
	                    if (attempt < retries - 1) {
	                       
	                        attempt++;
	                        Thread.sleep(retryInterval);  // Wait before retrying
	                    } else {
	                        // If all attempts fail, provide appropriate message
	                        return "Connection failed after " + retries + " attempts. Error: " + e.getMessage();
	                    }
	                }
	            }
	        } catch (IOException | InterruptedException e) {
	            return "Error occurred: " + e.getMessage();
	        } finally {
	            if (httpURLConnection != null) {
	                httpURLConnection.disconnect();
	            }
	        }
	        return "Unexpected error occurred.";
	    }

	
}
