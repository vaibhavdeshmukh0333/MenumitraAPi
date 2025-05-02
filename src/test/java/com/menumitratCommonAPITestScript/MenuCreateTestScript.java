package com.menumitratCommonAPITestScript;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.bson.types.Symbol;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.MenuRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.Listener;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;

@Listeners(Listener.class)
public class MenuCreateTestScript extends APIBase {

    private MenuRequest menuRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private String accessToken;
    private int userId;
    private RequestSpecification request;
    private static Logger logger = LogUtils.getLogger(MenuCreateTestScript.class);

    /**
     * Data provider for menu create API endpoint URLs
     */
    @DataProvider(name = "getMenuCreateUrl")
    public static Object[][] getMenuCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Menu Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData("src\\test\\resources\\excelsheet\\apiEndpoint.xlsx",
                    "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "menuCreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.exception(logger, "Error While Reading Menu Create API endpoint data from Excel sheet", e);
            ;
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Menu Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Menu Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for menu create test scenarios
     */
    @DataProvider(name = "getMenuCreateData")
    public static Object[][] getMenuCreateData() throws customException {
        try {
            LogUtils.info("Reading menu create test scenario data");

            LogUtils.info("Reading positive test scenario data for login API from Excel sheet");
            Object[][] testData = DataDriven.readExcelData("src\\test\\resources\\excelsheet\\apiEndpoint.xlsx",
                    "CommonAPITestScenario");

            if (testData == null || testData.length == 0) {
                LogUtils.error("No Login Api positive test scenario data found in Excel sheet");
                throw new customException("No Login APi Positive test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                        "menucreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            // Optional: print to verify
            /*
             * for (Object[] row : obj) {
             * System.out.println(Arrays.toString(row));
             * }
             */
            return obj;
        } catch (Exception e) {
            LogUtils.exception(logger, "Error while reading menu create test scenario data from Excel sheet", e);
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading menu create test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading menu create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("=====Verify Menu Create Test Script=====");
            ExtentReport.createTest("Verify Menu Create Test Script");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();

            // Get and set menu create URL
            Object[][] menuCreateData = getMenuCreateUrl();

            if (menuCreateData.length > 0) {
                String endpoint = menuCreateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                baseUri = endpoint;
                LogUtils.success(logger, "Constructed Menu Create Base URI: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Constructed Menu Create Base URI: " + baseUri);
            } else {

                LogUtils.failure(logger, "Failed constructed Menu Create Base URI.");
                ExtentReport.getTest().log(Status.ERROR, "Failed constructed Menu Create Base URI.");
                throw new customException("Failed constructed Menu Create Base URI.");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error(
                        "Required tokens not found. Please ensure login and OTP verification is completed");
                ExtentReport.getTest().log(Status.FAIL,
                        "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException(
                        "Required tokens not found. Please ensure login and OTP verification is completed");
            }
            menuRequest = new MenuRequest();
            LogUtils.success(logger, "Menu create test script Setup completed successfully");
            ExtentReport.getTest().log(Status.INFO, "Menu create test script Setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during menu create test script setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during menu create test script setup " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getMenuCreateData")
    private void createMenuUsigValidInputData(String apiName,String testCaseid, String testType, String description,
    		String httpsmethod,String requestBodyPayload,String expectedResponseBody,String statusCode)
            throws customException {

        try {
            LogUtils.info("Starting menu creation test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Menu Creation Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            expectedResponse=new JSONObject(expectedResponseBody);

            requestBodyJson=new JSONObject(requestBodyPayload.replace("\\","\\\\"));
      
            request=RestAssured.given();
            request.header("Authorization", "Bearer " + accessToken);
            request.contentType("multipart/form-data");
            		
            if(requestBodyJson.has("images") && !requestBodyJson.getString("images").isEmpty()) {
                LogUtils.info("Processing image attachments");
                File imageFile=new File(requestBodyJson.getString("images"));
                if(imageFile.exists()) {
                    for(int i=0;i<5;i++) {
                        request.multiPart("images",imageFile);
                    }
                    LogUtils.info("Successfully attached 5 image files");
                    ExtentReport.getTest().log(Status.INFO, "Successfully attached 5 image files");
                } else {
                    LogUtils.warn("Image file not found at path: " + requestBodyJson.getString("images"));
                    ExtentReport.getTest().log(Status.WARNING, "Image file not found at specified path");
                }
            }

            LogUtils.info("Setting up request form parameters");
            ExtentReport.getTest().log(Status.INFO, "Setting up request form parameters");
            
            request.multiPart("user_id", userId);
            request.multiPart("outlet_id", requestBodyJson.getString("outlet_id")); 
            request.multiPart("menu_cat_id", requestBodyJson.getString("menu_cat_id"));
            request.multiPart("name", requestBodyJson.getString("name"));
            request.multiPart("food_type", requestBodyJson.getString("food_type"));
            request.multiPart("description", requestBodyJson.getString("description"));
            request.multiPart("spicy_index", requestBodyJson.getString("spicy_index"));
            request.multiPart("portion_data", requestBodyJson.getJSONArray("portion_data").toString());
            request.multiPart("ingredients", requestBodyJson.getString("ingredients"));
            request.multiPart("offer", requestBodyJson.getString("offer"));
            request.multiPart("rating", requestBodyJson.getString("rating")); 
            request.multiPart("cgst", requestBodyJson.getString("cgst"));
            request.multiPart("sgst", requestBodyJson.getString("sgst"));
            		
            LogUtils.info("Sending POST request to endpoint: " + baseUri);
            ExtentReport.getTest().log(Status.INFO, "Sending POST request to create menu item");
            response=request.when().post(baseUri).then().extract().response();
            
            LogUtils.info("Received response with status code: " + response.getStatusCode());
            LogUtils.info("Response body: " + response.asPrettyString());
            
            if(response.getStatusCode()==200) {
                LogUtils.success(logger, "Menu item created successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu item created successfully", ExtentColor.GREEN));
                validateResponseBody.handleResponseBody(response, expectedResponse);
                LogUtils.info("Response validation completed successfully");
                ExtentReport.getTest().log(Status.PASS, "Response validation completed successfully");
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            } else {
                LogUtils.failure(logger, "Menu creation failed with status code: " + response.getStatusCode());
                LogUtils.error("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Menu creation failed", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
            }

        } catch (Exception e) {
            LogUtils.error("Error during menu creation test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during menu creation test execution: " + e.getMessage());
        }
    }

    @AfterClass
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
