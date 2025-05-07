package com.menumitratCommonAPITestScript;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.staffRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class StaffUpdateTestScript extends APIBase 
{
    private staffRequest staffUpdateRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private String deviceToken;
    private RequestSpecification request;
    Logger logger = Logger.getLogger(StaffUpdateTestScript.class);
    
    
    
    
    /**
     * 
     * 
     * Data provider for staff update API endpoint URLs
     */
    @DataProvider(name="getStaffUpdateUrl")
    public Object[][] getStaffUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Staff Update API endpoint data from Excel sheet");
            //ExtentReport.getTest().log(Status.INFO, "Reading Staff Update API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "staffupdate".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Staff Update API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Staff Update API endpoint data from Excel sheet");
            throw new customException("Error While Reading Staff Update API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for staff update test scenarios
     */
    @DataProvider(name="getStaffUpdateData")
    public Object[][] getStaffUpdateData() throws customException {
        try {
            LogUtils.info("Reading staff update test scenario data");
           //ExtentReport.getTest().log(Status.INFO, "Reading staff update test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff update test scenario data found in Excel sheet");
                //ExtentReport.getTest().log(Status.ERROR, "No staff update test scenario data found in Excel sheet");
                throw new customException("No staff update test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "staffupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        } catch(Exception e) {
            LogUtils.error("Error while reading staff update test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading staff update test scenario data: " + e.getMessage());
            throw new customException("Error while reading staff update test scenario data: " + e.getMessage());
        }
    }

    
    /**
     * Data provider for staff update negative test scenarios
     */
    @DataProvider(name="getStaffUpdateNegativeData")
    public Object[][] getStaffUpdateNegativeData() throws customException {
        try {
            LogUtils.info("Reading staff update negative test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff update test scenario data found in Excel sheet");
                throw new customException("No staff update test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                    "staffupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        } catch(Exception e) {
            LogUtils.error("Error while reading staff update negative test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading staff update negative test scenario data: " + e.getMessage());
            throw new customException("Error while reading staff update negative test scenario data: " + e.getMessage());
        }
    }
    
    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("Update Staff SetUp");
            ExtentReport.createTest("Update Staff Setup");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            //ExtentReport.getTest().log(Status.INFO, "Base URI set to: " + baseUri);
            
            Object[][] staffurl = getStaffUpdateUrl();
            if (staffurl.length > 0) 
            {
          
                String endpoint = staffurl[0][2].toString();
                url = new URL(endpoint);
                baseUri =baseUri+""+url.getPath()+"?"+url.getQuery();
                LogUtils.info("Staff Update URL set to: " + baseUri);
            } else {
                LogUtils.error("No staff update URL found in test data");
                throw new customException("No staff update URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
           
            LogUtils.info("Staff Update Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.error("Error during staff update setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during staff update setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to update staff member
     */
   //@Test(dataProvider="getStaffUpdateData")
    private void updateStaffUsingValidInputData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting staff update test: " + description);
            ExtentReport.createTest("Staff Update Using Valid Input Data");
            ExtentReport.getTest().log(Status.INFO, "Starting staff update test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if (apiName.equalsIgnoreCase("staffUpdate") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing staff update request");
                requestBodyJson = new JSONObject(requestBody.replace("\\", "\\\\"));
                expectedResponse = new JSONObject(expectedResponseBody);
                System.out.println(accessToken);
                request = RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.header("Content-Type", "multipart/form-data");

                // Set multipart form data
                request.multiPart("user_id", userId);
                request.multiPart("mobile", requestBodyJson.getInt("mobile"));
                request.multiPart("name", requestBodyJson.getString("name")); 
                request.multiPart("dob", requestBodyJson.getString("dob"));
                request.multiPart("aadhar_number", requestBodyJson.getInt("aadhar_number"));
                request.multiPart("address", requestBodyJson.getString("address"));
                request.multiPart("role", requestBodyJson.getString("role"));
                request.multiPart("outlet_id", requestBodyJson.getInt("outlet_id"));
                request.multiPart("staff_id",requestBodyJson.getInt("staff_id"));
                if (requestBodyJson.has("photo") && !requestBodyJson.getString("photo").isEmpty())
                {
                    File profileImage = new File(requestBodyJson.getString("photo"));
                    if(profileImage.exists())
                    {
                        request.multiPart("photo", profileImage);
                    }
                }
                LogUtils.info("Constructing request body");
                ExtentReport.getTest().log(Status.INFO, "Constructing request body");
                LogUtils.info("Sending PUT request to endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending PUT request to update staff");
                response = request.when().post(baseUri).then().extract().response();

                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response body: " + response.asPrettyString());
                if (response.getStatusCode() == 200) 
                {
                    LogUtils.success(logger, "Staff updated successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Staff updated successfully", ExtentColor.GREEN));
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                    LogUtils.info("Response validation completed successfully");
                    ExtentReport.getTest().log(Status.PASS, "Response validation completed successfully");
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                } 
                else 
                {
                    LogUtils.failure(logger, "Staff update failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Staff update failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
            }

        } catch (Exception e) {
            LogUtils.error("Error during staff update test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during staff update test execution: " + e.getMessage());
        }
    }

    
    /**
     * Test method to verify negative cases for staff update
     */
   // @Test(dataProvider="getStaffUpdateNegativeData")
    private void verifyStaffUpdateNegativeCases(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Staff Update Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("staffupdate") && testType.equalsIgnoreCase("negative")) {
                LogUtils.info("Preparing request body");
                ExtentReport.getTest().log(Status.INFO, "Preparing request body");
                
                // Handle potential JSON formatting issues in the test data
                requestBody = requestBody.replace("\\", "\\\\");
                requestBody = requestBody.replace(",}", "}"); // Fix potential trailing comma
                
                requestBodyJson = new JSONObject(requestBody);
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                LogUtils.info("Making API call to endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseUri);
                
                LogUtils.info("Using access token: " + accessToken.substring(0, Math.min(15, accessToken.length())) + "...");
                ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, Math.min(15, accessToken.length())) + "...");
                
                request = RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.header("Content-Type", "multipart/form-data");

                // Set multipart form data
                for (String key : requestBodyJson.keySet()) {
                    if (key.equals("image") && requestBodyJson.has("image") && !requestBodyJson.getString("image").isEmpty()) {
                        File profileImage = new File(requestBodyJson.getString("image"));
                        if (profileImage.exists()) {
                            request.multiPart("image", profileImage);
                        }
                    } else {
                        request.multiPart(key, requestBodyJson.get(key).toString());
                    }
                }
                
                response = request.when().post(baseUri).then().extract().response();

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                
                LogUtils.info("Response Body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                
                // Get expected status code from Excel
                int expectedStatusCode = Integer.parseInt(statusCode);
                
                // Check for server errors
                if (response.getStatusCode() == 500 || response.getStatusCode() == 502) {
                    LogUtils.failure(logger, "Server error detected with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Server error: " + response.getStatusCode(), ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
                // Validate status code
                else if (response.getStatusCode() != expectedStatusCode) {
                    LogUtils.failure(logger, "Status code validation failed - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Status code validation failed - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Test execution failed: Status code validation failed");
                } else {
                    LogUtils.success(logger, "Status code validation passed - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, "Status code validation passed - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                    
                    // Validate response body if expected response is provided
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        try {
                            // Fix potential issues in the expected response format
                            expectedResponseBody = expectedResponseBody.replace("\\", "\\\\");
                            
                            JSONObject expectedResponseJson = new JSONObject(expectedResponseBody);
                            actualResponseBody = new JSONObject(response.asString());
                            
                            // Check for specific error messages - first try "detail" field which appears in your test cases
                            if (expectedResponseJson.has("detail") && actualResponseBody.has("detail")) {
                                String expectedDetail = expectedResponseJson.getString("detail");
                                String actualDetail = actualResponseBody.getString("detail");
                                
                                if (expectedDetail.equals(actualDetail)) {
                                    LogUtils.success(logger, "Error message validation passed: " + actualDetail);
                                    ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualDetail);
                                } else {
                                    LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                                    ExtentReport.getTest().log(Status.FAIL, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                                }
                            }
                            // Also check for "msg" field as fallback
                            else if (expectedResponseJson.has("msg") && actualResponseBody.has("msg")) {
                                String expectedMsg = expectedResponseJson.getString("msg");
                                String actualMsg = actualResponseBody.getString("msg");
                                
                                if (expectedMsg.equals(actualMsg)) {
                                    LogUtils.success(logger, "Error message validation passed: " + actualMsg);
                                    ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualMsg);
                                } else {
                                    LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedMsg + ", Actual: " + actualMsg);
                                    ExtentReport.getTest().log(Status.FAIL, "Error message mismatch - Expected: " + expectedMsg + ", Actual: " + actualMsg);
                                }
                            }
                            
                            // Full response body validation using your utility
                            validateResponseBody.handleResponseBody(response, expectedResponseJson);
                            
                            LogUtils.success(logger, "Response body validation passed");
                            ExtentReport.getTest().log(Status.PASS, "Response body validation passed");
                        } catch (Exception e) {
                            LogUtils.failure(logger, "Response body validation failed: " + e.getMessage());
                            ExtentReport.getTest().log(Status.FAIL, "Response body validation failed: " + e.getMessage());
                        }
                    }
                    
                    LogUtils.success(logger, "Test execution completed successfully");
                    ExtentReport.getTest().log(Status.PASS, "Test execution completed successfully");
                }
                
                // Always log full response for reference
                ExtentReport.getTest().log(Status.INFO, "Full Response Body:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            }
        } catch (Exception e) {
            LogUtils.error("Error during staff update negative test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during staff update negative test execution: " + e.getMessage());
            
            if (response != null) {
                LogUtils.failure(logger, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asPrettyString());
            }
            
            throw new customException("Error during staff update negative test execution: " + e.getMessage());
        }
    }
   

    /**
     * Cleanup method to perform post-test cleanup
     * @throws customException 
     */
    @AfterClass
    private void tearDown() throws customException 
    {
    	
       /*LogUtils.info("Performing tear down");
       ExtentReport.getTest().log(Status.INFO, "Performing tear down");
       ActionsMethods.logout();
       TokenManagers.clearTokens();*/
    }
}
