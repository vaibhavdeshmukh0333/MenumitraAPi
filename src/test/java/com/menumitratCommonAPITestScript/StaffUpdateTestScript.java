package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import com.menumitra.apiRequest.staffRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.response.Response;

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
    private String userId;
    private String accessToken;
    private String deviceToken;

    /**
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
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("Setting up test environment for staff update");
            
            TokenManagers.login();
            TokenManagers.verifyOtp();
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            //ExtentReport.getTest().log(Status.INFO, "Base URI set to: " + baseUri);
            
            Object[][] staffUpdateData = getStaffUpdateUrl();
            if (staffUpdateData.length > 0) 
            {
          
                String endpoint = staffUpdateData[0][2].toString();
                url = new URL(endpoint);
                baseUri =baseUri+""+url.getPath()+"?"+url.getQuery();
                LogUtils.info("Staff Update URL set to: " + baseUri);
            } else {
                LogUtils.error("No staff update URL found in test data");
                throw new customException("No staff update URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            deviceToken = TokenManagers.getDeviceToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty() || deviceToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
            staffUpdateRequest = new staffRequest();
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
    @Test(dataProvider="getStaffUpdateData")
    private void updateStaff(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Executing staff update test case: " + testCaseid + " - " + description);
            ExtentReport.getTest().log(Status.INFO, "Executing staff update test case: " + testCaseid + " - " + description);
            
            if (apiName.contains("staffupdate")) {
                requestBodyJson = new JSONObject(requestBody);
                
                staffUpdateRequest.setUser_id(userId);
                staffUpdateRequest.setMobile(requestBodyJson.getString("mobile"));
                staffUpdateRequest.setName(requestBodyJson.getString("name"));
                staffUpdateRequest.setDob(requestBodyJson.getString("dob"));
                staffUpdateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
                staffUpdateRequest.setAddress(requestBodyJson.getString("address"));
                staffUpdateRequest.setRole(requestBodyJson.getString("role"));
                staffUpdateRequest.setDevice_token(deviceToken);
                staffUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));

                LogUtils.info("Sending staff update request to API with endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending staff update request to API with endpoint: " + baseUri);

                response = ResponseUtil.getResponseWithAuth(baseUri, staffUpdateRequest, httpsmethod, accessToken);
                LogUtils.error(String.valueOf(response.getStatusCode()));
                LogUtils.error(response.body().toString());
                
                actualResponseBody = new JSONObject(response.body().toString());
                expectedResponse=new JSONObject(expectedResponseBody);

                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.info("Staff update API responded successfully with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, "Staff update API responded successfully with status code: " + response.getStatusCode());
                    
                    validateResponseBody.handleResponseBody(actualResponseBody.get("st").toString(), 
                                                         expectedResponse.get("st").toString(), 
                                                         response.getStatusCode());
                    validateResponseBody.handleResponseBody(actualResponseBody.get("msg").toString(),
                                                         expectedResponse.get("msg").toString(),
                                                         response.getStatusCode());
                    
                    LogUtils.info("Response body validation passed for test case: " + testCaseid);
                    ExtentReport.getTest().log(Status.PASS, "Response body validation passed for test case: " + testCaseid);
                } else {
                    LogUtils.error("Staff update API failed. Expected status code: " + statusCode + ", but received: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Staff update API failed. Expected status code: " + statusCode + ", but received: " + response.getStatusCode());
                    throw new customException("Staff update API failed with incorrect status code");
                }
            }
        } catch (Exception e) {
            LogUtils.error("Error occurred during staff update test case: " + testCaseid + " - " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error occurred during staff update test case: " + testCaseid + " - " + e.getMessage());
            throw new customException("Error during staff update: " + e.getMessage());
        }
    }

    /**
     * Test method for negative scenarios in staff update
     */
    //@Test(dataProvider="getStaffUpdateData")
    public void verifyStaffUpdateUsingNegativeData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting staff update negative test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Starting staff update negative test: " + description);
            
            if (apiName.equalsIgnoreCase("staffUpdate") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                
                switch (testCaseid) {
                    case "staff_update_002":
                        try {
                            staffUpdateRequest.setUser_id(userId);
                            staffUpdateRequest.setMobile(requestBodyJson.getString("mobile"));
                            staffUpdateRequest.setName(requestBodyJson.getString("name"));
                            staffUpdateRequest.setDob(requestBodyJson.getString("dob"));
                            staffUpdateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
                            staffUpdateRequest.setAddress(requestBodyJson.getString("address"));
                            staffUpdateRequest.setRole(requestBodyJson.getString("role"));
                            staffUpdateRequest.setDevice_token(deviceToken);
                            staffUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                            
                            response = ResponseUtil.getResponseWithAuth(baseUri, staffUpdateRequest, httpsmethod, accessToken);
                            actualResponseBody = new JSONObject(response.body().toString());
                            expectedResponse = new JSONObject(expectedResponseBody);

                            validateResponseBody.handleResponseBody(actualResponseBody.get("st").toString(), 
                                                                 expectedResponse.get("status").toString(),
                                                                 response.getStatusCode());
                            validateResponseBody.handleResponseBody(actualResponseBody.get("msg").toString(),
                                                                 expectedResponse.get("msg").toString(),
                                                                 response.getStatusCode());
                        } catch (Exception e) {
                            LogUtils.error("Error during negative staff update test: " + e.getMessage());
                            throw new customException("Error during negative staff update test: " + e.getMessage());
                        }
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LogUtils.error("Error during staff update negative test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during staff update negative test: " + e.getMessage());
            throw new customException("Error during staff update negative test: " + e.getMessage());
        }
    }

    /**
     * Cleanup method to perform post-test cleanup
     */
    @AfterClass
    private void tearDown() 
    {
    	
        LogUtils.info("Performing test cleanup");
        TokenManagers.logout();
        LogUtils.info("Cleanup completed successfully");
    }
}
