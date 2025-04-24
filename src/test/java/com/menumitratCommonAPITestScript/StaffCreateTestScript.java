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

import com.menumitra.apiRequest.staffRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;

import io.restassured.response.Response;

/**
 * Test class for Staff Creation API functionality
 */
@Listeners(com.menumitra.utilityclass.Listener.class)
public class StaffCreateTestScript extends APIBase {

    private staffRequest staffCreateRequest;
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
     * Data provider for staff create API endpoint URLs
     */
    @DataProvider(name="getStaffCreateUrl")
    public Object[][] getStaffCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Staff Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading Staff Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "staffCreate".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Staff Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Staff Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Staff Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for staff create test scenarios
     */
    @DataProvider(name="getStaffCreateData")
    public Object[][] getStaffCreateData() throws customException {
        try {
            LogUtils.info("Reading staff create test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading staff create test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff create test scenario data found in Excel sheet");
                ExtentReport.getTest().log(Status.ERROR, "No staff create test scenario data found in Excel sheet");
                throw new customException("No staff create test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "staffcreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios");
            ExtentReport.getTest().log(Status.INFO, "Successfully retrieved " + obj.length + " test scenarios");
            return obj;   
        } catch(Exception e) {
            LogUtils.error("Error while reading staff create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading staff create test scenario data: " + e.getMessage());
            throw new customException("Error while reading staff create test scenario data from Excel sheet: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getStaffNegativeData")
    public Object[][] getStaffNegativeData() throws customException 
    {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if(readExcelData==null || readExcelData.length==0)
            {
                LogUtils.error("No staff create test scenario data found in Excel sheet");
                ExtentReport.getTest().log(Status.ERROR, "No staff create test scenario data found in Excel sheet");
                throw new customException("No staff create test scenario data found in Excel sheet");
            }
            List<Object[]> filterData=new ArrayList<>();
            for(int i=0;i<readExcelData.length;i++)
            {
                Object[] row=readExcelData[i];
                if(row!=null && "staffcreate".equalsIgnoreCase(Objects.toString(row[0],"")) && "negative".equalsIgnoreCase(Objects.toString(row[2],"")))
                {
                    filterData.add(row);
                }
            }
            Object[][] obj=new Object[filterData.size()][];
            for(int i=0;i<filterData.size();i++)
            {
                obj[i]=filterData.get(i);
            }
            LogUtils.info("Successfully retrieved "+obj.length+" test scenarios");
            ExtentReport.getTest().log(Status.INFO, "Successfully retrieved "+obj.length+" test scenarios");
            return obj;
        }
        catch(Exception e)
        {   
            LogUtils.error("Error while reading staff create test scenario data from Excel sheet: "+e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading staff create test scenario data: "+e.getMessage());
            throw new customException("Error while reading staff create test scenario data from Excel sheet: "+e.getMessage());
        }
    }
    
    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("Setting up test environment");
            
            
            TokenManagers.login();
            TokenManagers.verifyOtp();
            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            ExtentReport.getTest().log(Status.INFO, "Base URI set to: " + baseUri);
            
            // Get and set staff create URL
            Object[][] staffCreateData = getStaffCreateUrl();
            if (staffCreateData.length > 0) {
                String endpoint = staffCreateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Staff Create URL set to: " + baseUri);
               
            } else {
                LogUtils.error("No staff create URL found in test data");
                throw new customException("No staff create URL found in test data");
            }
            
            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            deviceToken = TokenManagers.getDeviceToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty() || deviceToken.isEmpty())
            {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            staffCreateRequest=new staffRequest();
            LogUtils.info("Staff Setup completed successfully");
          
        } catch (Exception e) {
            LogUtils.error("Error during staff setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during staff setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }
   
    /**
     * Test method to create staff member
     */
    @Test(dataProvider="getStaffCreateData")
    private void createStaff(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting staff creation test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Starting staff creation test: " + description);
            
            if (apiName.contains("staffCreate")) 
            {
                requestBodyJson=new JSONObject(requestBody);
                
                staffCreateRequest.setUser_id(userId);
                staffCreateRequest.setMobile(requestBodyJson.getString("mobile"));
                staffCreateRequest.setName(requestBodyJson.getString("name"));
                staffCreateRequest.setDob(requestBodyJson.getString("dob"));
                staffCreateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
                staffCreateRequest.setAddress(requestBodyJson.getString("address"));
                staffCreateRequest.setRole(requestBodyJson.getString("role"));
                staffCreateRequest.setDevice_token(deviceToken);
                staffCreateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));

                response=ResponseUtil.getResponseWithAuth(baseUri, staffCreateRequest, httpsmethod, accessToken);
                
                if(response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.info("Staff creation successful with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, "Staff creation successful with status code: " + response.getStatusCode());
                } else {
                    LogUtils.error("Staff creation failed. Expected status code: " + statusCode + ", Actual: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Staff creation failed. Expected status code: " + statusCode + ", Actual: " + response.getStatusCode());
                }
            }
        } catch (Exception e) {
            LogUtils.error("Error during staff creation: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during staff creation: " + e.getMessage());
            throw new customException("Error during staff creation: " + e.getMessage());
        }
    } 
    
    public void verifystaffcreationUsingNegativeData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException
    {
       try
       {
        LogUtils.info("Starting staff creation using negative data test: "+description);
        ExtentReport.getTest().log(Status.INFO, "Starting staff creation using negative data test: "+description);
        requestBodyJson=new JSONObject(requestBody);
        if(apiName.equalsIgnoreCase("staffcreate") && testType.equalsIgnoreCase("negative"))
        {
           switch (testCaseid) {
           case "staff_002":
               try
               {
               		staffCreateRequest.setUser_id(userId);
               		staffCreateRequest.setMobile(requestBodyJson.getString("mobile"));
               		staffCreateRequest.setName(requestBodyJson.getString("name"));
               		staffCreateRequest.setDob(requestBodyJson.getString("dob"));
               		staffCreateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
               		staffCreateRequest.setAddress(requestBodyJson.getString("address"));
               		staffCreateRequest.setRole(requestBodyJson.getString("role"));
               		staffCreateRequest.setDevice_token(deviceToken);
               		staffCreateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                   
                   response=ResponseUtil.getResponseWithAuth(baseUri, staffCreateRequest, httpsmethod, accessToken);
                   actualResponseBody=new JSONObject(response.body().toString());
                   expectedResponse=new JSONObject(expectedResponseBody);

                   if(response.getStatusCode()==200)
                   {
                        
                       validateResponseBody.handleResponseBody(actualResponseBody.get("st").toString(), expectedResponse.get("st").toString(),200);
                       validateResponseBody.handleResponseBody(actualResponseBody.get("msg").toString(),expectedResponse.get("msg").toString(),200);
                   }
                   else if(response.getStatusCode()==400)
                   {
                      
                       validateResponseBody.handleResponseBody(actualResponseBody.get("st").toString(), expectedResponse.get("status").toString(),400);
                       validateResponseBody.handleResponseBody(actualResponseBody.get("msg").toString(),expectedResponse.get("msg").toString(),400);
                   }
                   else if(response.getStatusCode()==401)
                   {
                       validateResponseBody.handleResponseBody(actualResponseBody.get("st").toString(), expectedResponse.get("status").toString(),401);
                       validateResponseBody.handleResponseBody(actualResponseBody.get("msg").toString(),expectedResponse.get("msg").toString(),401);
                   }
                   else 
                   {
                       LogUtils.error("Error: Getting 500 message code.");
                   }
               }
               catch(Exception e)
               {
                   LogUtils.error("Error occurred during staff creation using invalid input data: " + e.getMessage());
                   throw new customException("Error occurred during staff creation using invalid input data: " + e.getMessage());
               }
               break;

		default:
			break;
		}
        }
       }
       catch(Exception e)
       {
        LogUtils.error("Error during staff creation using negative data: "+e.getMessage());
        ExtentReport.getTest().log(Status.FAIL, "Error during staff creation using negative data: "+e.getMessage());
        throw new customException("Error during staff creation using negative data: "+e.getMessage());
       }
    }

    
   

    /**
     * Cleanup method to perform post-test cleanup
     */
    @AfterClass
    private void tearDown()
    {
        LogUtils.info("Performing test cleanup - logging out");
        //ExtentReport.getTest().log(Status.INFO, "Performing test cleanup - logging out");
        TokenManagers.logout();
        LogUtils.info("Cleanup completed successfully");
        //ExtentReport.getTest().log(Status.PASS, "Cleanup completed successfully");
    }
} 