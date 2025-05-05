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
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;

import io.restassured.RestAssured;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Test class for Staff Creation API functionality
 */
@Listeners(com.menumitra.utilityclass.Listener.class)
public class StaffCreateTestScript extends APIBase {

    private staffRequest staffCreateRequest;
    private Response response;
   
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private String deviceToken;
    Logger logger=LogUtils.getLogger(StaffCreateTestScript.class);
    RequestSpecification request;

    /**
     * Data provider for staff create API endpoint URLs
     */
    @DataProvider(name = "getStaffCreateUrl")
    public static Object[][] getStaffCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Staff Create API endpoint data from Excel sheet");
            // ExtentReport.getTest().log(Status.INFO, "Reading Staff Create API endpoint
            // data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "staffCreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Staff Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Staff Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Staff Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for staff create test scenarios
     */
    @DataProvider(name = "getStaffCreateData")
    public static Object[][] getStaffCreateData() throws customException {
        try {
            LogUtils.info("Reading staff create test scenario data");
            // ExtentReport.getTest().log(Status.INFO, "Reading staff create test scenario
            // data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff create test scenario data found in Excel sheet");
                // ExtentReport.getTest().log(Status.ERROR, "No staff create test scenario data
                // found in Excel sheet");
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
            // ExtentReport.getTest().log(Status.INFO, "Successfully retrieved " +
            // obj.length + " test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading staff create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading staff create test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading staff create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @DataProvider(name = "getStaffNegativeData")
    public Object[][] getStaffNegativeData() throws customException {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff create test scenario data found in Excel sheet");
                ExtentReport.getTest().log(Status.ERROR, "No staff create test scenario data found in Excel sheet");
                throw new customException("No staff create test scenario data found in Excel sheet");
            }
            List<Object[]> filterData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && "staffcreate".equalsIgnoreCase(Objects.toString(row[0], ""))
                        && "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filterData.add(row);
                }
            }
            Object[][] obj = new Object[filterData.size()][];
            for (int i = 0; i < filterData.size(); i++) {
                obj[i] = filterData.get(i);
            }
            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios");
            ExtentReport.getTest().log(Status.INFO, "Successfully retrieved " + obj.length + " test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading staff create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading staff create test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading staff create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====start setup create staff====");
            ExtentReport.createTest("Create Staff Setup"); 
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
           

            // Get and set staff create URL
            Object[][] staffCreateData = getStaffCreateUrl();
            if (staffCreateData.length > 0) {
                String endpoint = staffCreateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Constructed base URI: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseUri);

            } else {
                LogUtils.failure(logger, "No staff create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No staff create URL found in test data");
                throw new customException("No staff create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error(
                        "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException(
                        "Required tokens not found. Please ensure login and OTP verification is completed");
            }
            staffCreateRequest = new staffRequest();
            LogUtils.success(logger, "Staff Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Staff Setup completed successfully");

        } catch (Exception e)
         {
            LogUtils.failure(logger, "Error during staff setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during staff setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to create staff member
     */
    @Test(dataProvider = "getStaffCreateData")
    private void createStaff(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting staff creation test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Staff Creation Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            expectedResponse=new JSONObject(expectedResponseBody);
            if (apiName.equalsIgnoreCase("staffcreate") && testType.equalsIgnoreCase("positive"))
            {
                LogUtils.info("Processing positive test case for staff creation");
                ExtentReport.getTest().log(Status.INFO, "Processing positive test case for staff creation");

                // Replace single backslashes with double backslashes to properly escape file paths in JSON
                requestBody=requestBody.replace("\\", "\\\\");
                JSONObject requestjsonBody=new JSONObject(requestBody);
                
                LogUtils.info("Setting up multipart form request");
                request=RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.contentType("multipart/form-data");

                if(requestjsonBody.has("image") && !requestjsonBody.getString("image").isEmpty())
                {
                    LogUtils.info("Processing image attachment");
                    File imageFile=new File(requestjsonBody.getString("image"));
                    if(imageFile.exists())
                    {
                        request.multiPart("image", imageFile);
                        LogUtils.info("Image file attached successfully");
                    } else {
                        LogUtils.warn("Image file not found at specified path");
                    }
                }

                LogUtils.info("Adding form data parameters to request");
                request.multiPart("outlet_id", requestjsonBody.getInt("outlet_id"));
                request.multiPart("user_id", userId);
                request.multiPart("mobile", requestjsonBody.getString("mobile"));
                request.multiPart("name", requestjsonBody.getString("name"));
                request.multiPart("dob", requestjsonBody.getString("dob"));
                request.multiPart("aadhar_number", requestjsonBody.getString("aadhar_number"));
                request.multiPart("address", requestjsonBody.getString("address"));
                request.multiPart("role", requestjsonBody.getString("role"));
                
                LogUtils.info("Sending POST request to " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending POST request to create staff member");
                response=request.when().post(baseUri).then().extract().response();
                
                if(response.getStatusCode()==201)
                {
                    LogUtils.success(logger, "Staff created successfully with status code 201");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Staff created successfully", ExtentColor.GREEN));
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                    LogUtils.info("Staff creation successful. Response: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.PASS, "Staff creation successful. Response: " + response.asPrettyString());
                }
                else{
                    LogUtils.failure(logger, "Staff creation failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Staff creation failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response body: " + response.asPrettyString());
                }
               
            }
        } 
        catch (Exception e) {
            LogUtils.error("Error during staff creation test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during staff creation test execution: " + e.getMessage());
        }
    }

    
   // @AfterClass
    private void tearDown()
    {
        try 
        {
            LogUtils.info("===Test environment tear down successfully===");
           
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
            
            ActionsMethods.logout();
            TokenManagers.clearTokens();
            
        } 
        catch (Exception e) 
        {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }




}