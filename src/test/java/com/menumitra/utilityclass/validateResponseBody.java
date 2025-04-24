package com.menumitra.utilityclass;

import org.json.JSONObject;
import org.testng.Assert;


/**
 * Utility class for handling HTTP response codes and validating responses
 */
public class validateResponseBody {

	/**
	 * * This method handles the error response based on the status code and
	 * validates it against the expected response.
	 * 
	 * @param actualresponse
	 * @param expectedResponse
	 * @param statusCode
	 * @throws customException
	 */
	public static void handleResponseBody(String actualresponse, String expectedResponse, int statusCode)
			throws customException {

		try {
			// Log the status code and response for debugging
			switch (statusCode) 
			{
				case 200:	
							try{
								Assert.assertEquals(actualresponse, expectedResponse);
								LogUtils.info("200 OK response validated successfully");
							}
							catch (AssertionError e) {
								LogUtils.error("200 OK validation failed: " + e.getMessage());
								throw new customException("200 OK validation failed: " + e.getMessage());
							}
							
					break;
				case 400:
					try {
						Assert.assertEquals(actualresponse, expectedResponse);
						LogUtils.info("400 Bad Request response validated successfully");
					} catch (AssertionError e) {
						LogUtils.error("400 Bad Request validation failed: " + e.getMessage());
						throw new customException("400 Bad Request validation failed: " + e.getMessage());
					}
					break;
				case 500:
					try {
						Assert.assertEquals(actualresponse, expectedResponse);
						LogUtils.info("500 Internal Server Error response validated successfully");
					} catch (AssertionError e) {
						LogUtils.error("500 Internal Server Error validation failed: " + e.getMessage());
						throw new customException("500 Internal Server Error validation failed: " + e.getMessage());
					}
					break;
				case 401:
					try {
						Assert.assertEquals(actualresponse, expectedResponse);
						LogUtils.info("401 Unauthorized response validated successfully");
					} catch (AssertionError e) {
						LogUtils.error("401 Unauthorized validation failed: " + e.getMessage());
						throw new customException("401 Unauthorized validation failed: " + e.getMessage());
					}
					break;
				default:
					LogUtils.error("Unhandled status code: " + statusCode);
					throw new customException("Unhandled status code: " + statusCode);
			}
		} catch (AssertionError e) {
			LogUtils.error("Assertion failed: " + e.getMessage());
			throw new customException("Assertion failed: " + e.getMessage());
		} catch (Exception e) {
			LogUtils.error("Error in response validation: " + e.getMessage());
			throw new customException("Error in response validation: " + e.getMessage());
		}
	}
}
