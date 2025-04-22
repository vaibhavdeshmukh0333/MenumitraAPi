package com.menumitratCommonAPITestScript;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import com.menumitra.apiRequest.StaffCreateRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;

import io.restassured.response.Response;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class StaffCreateTestScript extends APIBase {

    private StaffCreateRequest staffCreateRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private String userId;
    private String accessToken;
    private String deviceToken;
    
    @DataProvider(name="getStaffCreateUrl")
    public Object[][] getStaffCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Staff Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "ownerAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "staffCreate".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Staff Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Staff Create API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name="getStaffCreateData")
    public Object[][] getStaffCreateData() throws customException {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "StaffAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff create test scenario data found in Excel sheet");
                throw new customException("No staff create test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                    "staffCreate".equalsIgnoreCase(Objects.toString(row[0], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;   
        } catch(Exception e) {
            LogUtils.error("Error while reading staff create test scenario data from Excel sheet: " + e.getMessage());
            throw new customException("Error while reading staff create test scenario data from Excel sheet: " + e.getMessage());
        }
    }
    
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("Setting up test environment");
            //ExtentReport.getTest().log(Status.INFO, "Setting up test environment");
            
            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            
            // Get and set staff create URL
            Object[][] staffCreateData = getStaffCreateUrl();
            if (staffCreateData.length > 0) {
                String endpoint = staffCreateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Staff Create URL set to: " + baseUri);
            } else {
                throw new customException("No staff create URL found in test data");
            }
            
            // Get tokens from TokenManager
            accessToken = TokenManagers.getToken("access_token");
            deviceToken = TokenManagers.getToken("device_token");
            userId = TokenManagers.getToken("user_id");
            
            if (accessToken == null || deviceToken == null || userId == null) {
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        } catch (Exception e) {
            LogUtils.error("Error during setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }
   
    @Test(dataProvider="getStaffCreateData")
    private void createStaff(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting staff creation test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Starting staff creation test: " + description);
            
            if (apiName.contains("staffCreate")) {
                requestBodyJson = new JSONObject(requestBody);
                
                // For positive test cases, validate role and update user ID
                if ("positive".equalsIgnoreCase(testType)) {
                    String userRole = TokenManagers.getToken("user_role");
                    if (!isValidRole(userRole)) {
                        throw new customException("User with role " + userRole + " is not authorized to create staff");
                    }
                    requestBodyJson.put("created_by", userId);
                }
                
                staffCreateRequest = new StaffCreateRequest();
                
                // Handle different test scenarios based on test case ID
                switch(testCaseid) {
                    case "staff_001": // Valid staff creation
                        setValidStaffData(requestBodyJson);
                        break;
                    case "staff_002": // Duplicate staff mobile
                        setValidStaffData(requestBodyJson);
                        break;
                    case "staff_003": // Duplicate mobile with different user_id
                        setValidStaffData(requestBodyJson);
                        break;
                    case "staff_004": // Existing aadhar number
                        setValidStaffData(requestBodyJson);
                        break;
                    case "staff_005": // Missing name
                        setStaffDataWithoutName(requestBodyJson);
                        break;
                    case "staff_006": // Invalid mobile format
                        setStaffDataWithInvalidMobile(requestBodyJson);
                        break;
                    case "staff_007": // Invalid aadhar format
                        setStaffDataWithInvalidAadhar(requestBodyJson);
                        break;
                    case "staff_008": // Invalid DOB format
                        setStaffDataWithInvalidDOB(requestBodyJson);
                        break;
                    case "staff_009": // Missing device token
                        setStaffDataWithoutDeviceToken(requestBodyJson);
                        break;
                    default:
                        setValidStaffData(requestBodyJson);
                }
                
                // Add headers for authentication
                response = ResponseUtil.getResponseWithAuth(baseUri, staffCreateRequest, httpsmethod, accessToken, deviceToken);
                
                // Verify response based on expected status code
                expectedResponse = new JSONObject(expectedResponseBody);
                actualResponseBody = new JSONObject(response.body().asString());
                verifyStaffCreateResponse(actualResponseBody, expectedResponse, Integer.parseInt(statusCode));
                
                ExtentReport.getTest().log(Status.INFO, "Staff Create API response: " + response.asString());
                LogUtils.info("Staff Create API response: " + response.asString());
            }
        } catch (Exception e) {
            LogUtils.error("Error during staff creation: " + e.getMessage());
            throw new customException("Error during staff creation: " + e.getMessage());
        }
    }

    private void setValidStaffData(JSONObject requestBody) {
        staffCreateRequest.setName(requestBody.get("name").toString());
        staffCreateRequest.setMobile(requestBody.get("mobile").toString());
        staffCreateRequest.setRole(requestBody.get("role").toString());
        staffCreateRequest.setCreated_by(userId);
        if (requestBody.has("aadhar_number")) {
            staffCreateRequest.setAadharNumber(requestBody.get("aadhar_number").toString());
        }
        if (requestBody.has("dob")) {
            staffCreateRequest.setDob(requestBody.get("dob").toString());
        }
    }

    private void setStaffDataWithoutName(JSONObject requestBody) {
        staffCreateRequest.setMobile(requestBody.get("mobile").toString());
        staffCreateRequest.setRole(requestBody.get("role").toString());
        staffCreateRequest.setCreated_by(userId);
    }

    private void setStaffDataWithInvalidMobile(JSONObject requestBody) {
        staffCreateRequest.setName(requestBody.get("name").toString());
        staffCreateRequest.setMobile(requestBody.get("mobile").toString()); // Invalid mobile format
        staffCreateRequest.setRole(requestBody.get("role").toString());
        staffCreateRequest.setCreated_by(userId);
    }

    private void setStaffDataWithInvalidAadhar(JSONObject requestBody) {
        setValidStaffData(requestBody);
        staffCreateRequest.setAadharNumber(requestBody.get("aadhar_number").toString()); // Invalid aadhar format
    }

    private void setStaffDataWithInvalidDOB(JSONObject requestBody) {
        setValidStaffData(requestBody);
        staffCreateRequest.setDob(requestBody.get("dob").toString()); // Invalid DOB format
    }

    private void setStaffDataWithoutDeviceToken(JSONObject requestBody) {
        setValidStaffData(requestBody);
        // Device token will be missing in the request
    }

    private boolean isValidRole(String role) {
        return role != null && (role.equals("owner") || role.equals("manager") || role.equals("captain"));
    }

    private void verifyStaffCreateResponse(JSONObject actualResponse, JSONObject expectedResponse, int statusCode) throws customException {
        try {
            LogUtils.info("Verifying staff create response body");
            ExtentReport.getTest().log(Status.INFO, "Verifying staff create response body");
            
            Assert.assertEquals(actualResponse.get("st"), expectedResponse.get("st"), "Status code mismatch");
            Assert.assertEquals(actualResponse.get("msg"), expectedResponse.get("msg"), "Message mismatch");
            
            // For successful creation (status code 200)
            if (statusCode == 200) {
                JSONObject actualStaff = actualResponse.getJSONObject("data");
                JSONObject expectedStaff = expectedResponse.getJSONObject("data");
                
                Assert.assertEquals(actualStaff.get("name"), expectedStaff.get("name"), "Staff name mismatch");
                Assert.assertEquals(actualStaff.get("mobile"), expectedStaff.get("mobile"), "Staff mobile mismatch");
                Assert.assertEquals(actualStaff.get("role"), expectedStaff.get("role"), "Staff role mismatch");
                
                LogUtils.info("Successfully validated staff create response");
                ExtentReport.getTest().log(Status.PASS, "Successfully validated staff create response");
            }
            // For error cases (status code 400, 401, etc.)
            else {
                LogUtils.info("Validated error response: " + actualResponse.get("msg"));
                ExtentReport.getTest().log(Status.PASS, "Validated error response: " + actualResponse.get("msg"));
            }
        } catch (AssertionError e) {
            LogUtils.error("Assertion failed during staff create response validation: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Assertion failed during staff create response validation: " + e.getMessage());
            throw new customException("Assertion failed during staff create response validation: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.error("Error during staff create response validation: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during staff create response validation: " + e.getMessage());
            throw new customException("Error during staff create response validation: " + e.getMessage());
        }
    }
    
    @AfterClass
    private void cleanup() {
        try {
            LogUtils.info("Starting cleanup process");
            ExtentReport.getTest().log(Status.INFO, "Starting cleanup process");
            
            // Execute logout to clear tokens
            TokenManagers.clearTokens();
            
            // Verify tokens are cleared
            if (TokenManagers.getToken("access_token") != null || TokenManagers.getToken("device_token") != null) {
                LogUtils.warn("Tokens were not properly cleared during logout");
            }
            
            LogUtils.info("Cleanup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Cleanup completed successfully");
        } catch (Exception e) {
            LogUtils.error("Error during cleanup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during cleanup: " + e.getMessage());
        }
    }
} 