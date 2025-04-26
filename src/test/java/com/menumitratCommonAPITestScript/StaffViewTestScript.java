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

import io.restassured.response.Response;

public class StaffViewTestScript extends APIBase
{
    
    private String baseUri;
    private String accessToken;
    private String userId;
    private String deviceToken;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private staffRequest staffViewRequest;
    private com.menumitra.utilityclass.validateResponseBody validateResponseBody;
    private URL url;

    /**
     * Setup method to initialize test data
     */
    @BeforeClass
    private void staffViewsetUp() throws customException 
    {
        try {
            LogUtils.info("Setting up test environment");

            TokenManagers.login();
            TokenManagers.verifyOtp();
            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);

            // Get and set staff view URL
            Object[][] staffViewData = getStaffViewURL();
            if (staffViewData.length > 0) {
                String endpoint = staffViewData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Staff View URL set to: " + baseUri);
            } else {
                LogUtils.error("No staff view URL found in test data");
                throw new customException("No staff view URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            deviceToken = TokenManagers.getDeviceToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty() || deviceToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            staffViewRequest = new staffRequest();
            validateResponseBody = new com.menumitra.utilityclass.validateResponseBody();
            LogUtils.info("Staff view setup completed successfully");

        } catch (Exception e) {
            LogUtils.error("Error during staff view setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during staff view setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @DataProvider(name="getStaffViewURL")
    public Object[][] getStaffViewURL() throws customException
    {
        try{
            Object[][] readData=DataDriven.readExcelData("excelSheetPathForGetApis","commonAPI");
            if(readData==null)
            {
            	LogUtils.error("Error: Getting an error while read Staff URL Excel File");
            	throw new customException("Error: Getting an error while read Staff URL Excel File");
            }
            
            return Arrays.stream(readData)
            		.filter(row -> "staffview".equalsIgnoreCase(row[0].toString()))
            		.toArray(Object[][]::new);
        }
        catch (Exception e) {
            LogUtils.error("Error: Getting an error while read Staff URL Excel File");
            throw new customException("Error: Getting an error while read Staff URL Excel File");
		}
    }
    
    @DataProvider(name = "getStaffViewData")
    public static Object[][] getStaffViewData() throws customException {
        try {
            LogUtils.info("Reading staff view test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff view test scenario data found in Excel sheet");
                throw new customException("No staff view test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "staffview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading staff view test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading staff view test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading staff view test scenario data from Excel sheet: " + e.getMessage());
        }
    }



    
    /**
     * Test method for positive scenarios in staff view
     */
    @Test(dataProvider="getStaffViewData")
    public void verifyStaffViewUsingPositiveData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting staff view test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Starting staff view test: " + description);
            
            if (apiName.equalsIgnoreCase("staffView") && testType.equalsIgnoreCase("positive")) {
                requestBodyJson = new JSONObject(requestBody);
                
                switch (testCaseid) {
                    case "staff_view_001":
                        try {
                            staffViewRequest.setUser_id(userId);
                            staffViewRequest.setDevice_token(deviceToken);
                            staffViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                            
                            response = ResponseUtil.getResponseWithAuth(baseUri, staffViewRequest, httpsmethod, accessToken);
                            actualResponseBody = new JSONObject(response.body().toString());
                            expectedResponse = new JSONObject(expectedResponseBody);

                            validateResponseBody.handleResponseBody(actualResponseBody.get("st").toString(),
                                                                expectedResponse.get("status").toString(),
                                                                response.getStatusCode());
                            validateResponseBody.handleResponseBody(actualResponseBody.get("msg").toString(),
                                                                expectedResponse.get("msg").toString(),
                                                                response.getStatusCode());
                        } catch (Exception e) {
                            LogUtils.error("Error during staff view test: " + e.getMessage());
                            throw new customException("Error during staff view test: " + e.getMessage());
                        }
                        break;

                    default:
                        break;
                }
            }
        } 
        catch (Exception e) 
        {
        	LogUtils.error("Error occurred during staff view test case: " + testCaseid + " - " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error occurred during staff view test case: " + testCaseid + " - " + e.getMessage());
            throw new customException("Error during staff view: " + e.getMessage());
        }
    }
}
