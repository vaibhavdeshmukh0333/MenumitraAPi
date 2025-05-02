package com.menumitratCommonAPITestScript;

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
import com.menumitra.apiRequest.sectionListViewRequest;
import com.menumitra.apiRequest.sectionRequest;
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
import io.restassured.response.Response;

@Listeners(Listener.class)
public class sectionListViewTestScript extends APIBase
{

	private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedJson; 
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private sectionRequest sectionrequest;
    private sectionListViewRequest sectionListViewRequest;
    Logger logger=LogUtils.getLogger(sectionCreateTestScript.class); 	
    
    
    @DataProvider(name="getSectionListViewURL")
    public Object[][] getSectionListViewURL() throws customException
    {
        try{
            Object[][] readData=DataDriven.readExcelData(excelSheetPathForGetApis,"commonAPI");
            if(readData==null)
            {
                LogUtils.failure(logger, "Error: Getting an error while read Section URL Excel File");
                throw new customException("Error: Getting an error while read Section URL Excel File");
            }
            
            return Arrays.stream(readData)
                    .filter(row -> "sectionlistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Error: Getting an error while read Section URL Excel File", e);
            throw new customException("Error: Getting an error while read Section URL Excel File");
        }
    }
	
@DataProvider(name="getSectionListViewPositiveInputData")
private Object[][] getSectionListViewPositiveInputData() throws customException {
    try {
        LogUtils.info("Reading positive test scenario data for section list view API from Excel sheet");
        Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, property.getProperty("CommonAPITestScenario"));
        
        if (testData == null || testData.length == 0) {
            LogUtils.failure(logger, "No Section List View API positive test scenario data found in Excel sheet");
            throw new customException("No Section List View API Positive test scenario data found in Excel sheet");
        }         
        
        List<Object[]> filteredData = new ArrayList<>();
        
        for (int i = 0; i < testData.length; i++) {
            Object[] row = testData[i];

            // Ensure row is not null and has at least 3 columns
            if (row != null && row.length >= 3 &&
                "sectionlistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                filteredData.add(row); // Add the full row (all columns)
            }
        }

        Object[][] obj = new Object[filteredData.size()][];
        for (int i = 0; i < filteredData.size(); i++) {
            obj[i] = filteredData.get(i);
        }

        // Optional: print to verify
        /*for (Object[] row : obj) {
            System.out.println(Arrays.toString(row));
        }*/
        return obj;
    }
    catch (Exception e) {
        LogUtils.exception(logger, "Failed to read Section List View API positive test scenario data: " + e.getMessage(), e);
        throw new customException("Error reading Section List View API positive test scenario data from Excel sheet: " + e.getMessage());
    }
}

@DataProvider(name="getSectionListViewNegativeInputData")
private Object[][] getSectionListViewNegativeInputData() throws customException 
{
    try {
        LogUtils.info("Reading negative test scenario data for section list view API");
        Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
        
        if (testData == null || testData.length == 0) 
        {
            LogUtils.failure(logger, "No section list view API negative test scenario data found in Excel sheet");
            throw new customException("No section list view API negative test scenario data found in Excel sheet");
        }
        
        List<Object[]> filteredData = new ArrayList<>();
        
        // Filter for section list view API negative test cases
        for (int i = 0; i < testData.length; i++) {
            Object[] row = testData[i];
            if (row != null && row.length >= 3 &&
                "sectionlistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                
                filteredData.add(row);
            }
        }

        Object[][] obj = new Object[filteredData.size()][];
        for (int i = 0; i < filteredData.size(); i++) {
            obj[i] = filteredData.get(i);
        }
        
        return obj;
    } catch (Exception e) {
        LogUtils.exception(logger, "Failed to read section list view API negative test scenario data: " + e.getMessage(), e);
        throw new customException("Error reading section list view API negative test scenario data from Excel sheet: " + e.getMessage());
    }
}

@BeforeClass
private void sectionListViewSetup() throws customException 
{
    try {
        LogUtils.info("Setting up test environment");
        ExtentReport.createTest("Start Section List View");
        ActionsMethods.login();
        ActionsMethods.verifyOTP();

        // Get base URL
        baseUri = EnviromentChanges.getBaseUrl();
        LogUtils.info("Base URI set to: " + baseUri);

        // Get and set section list view URL
        Object[][] sectionListViewData = getSectionListViewURL();
        if (sectionListViewData.length > 0) {
            String endpoint = sectionListViewData[0][2].toString();
            url = new URL(endpoint);
            baseUri = RequestValidator.buildUri(endpoint, baseUri);
            LogUtils.info("Section List View URL set to: " + baseUri);
        } else {
            LogUtils.failure(logger, "No section list view URL found in test data");
            throw new customException("No section list view URL found in test data");
        }

        // Get tokens from TokenManager
        accessToken = TokenManagers.getJwtToken();
        userId = TokenManagers.getUserId();

        if (accessToken.isEmpty()) {
            LogUtils.failure(logger, "Error: Required tokens not found. Please ensure login and OTP verification is completed");
            throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
        }

        sectionrequest = new sectionRequest();
        
        LogUtils.info("Section list view setup completed successfully");

    } catch (Exception e) {
        LogUtils.exception(logger, "Error during section list view setup: " + e.getMessage(), e);
        throw new customException("Error during setup: " + e.getMessage());
    }
}


@Test(dataProvider = "getSectionListViewPositiveInputData", priority = 1)
private void verifySectionListViewUsingValidInputData(String apiName, String testCaseId,
        String testType, String description, String httpsMethod,
        String requestBody, String expectedResponseBody, String statusCode) throws customException {
    try {
        LogUtils.info("Start section list view API using valid input data");
        ExtentReport.createTest("Verify Section List View API: " + description);
        ExtentReport.getTest().log(Status.INFO, "====Start section list view using positive input data====");
        ExtentReport.getTest().log(Status.INFO, "Constructed Base URI: " + baseUri);

        if (apiName.contains("sectionlistview") && testType.contains("positive"))
        {
        	
            requestBodyJson = new JSONObject(requestBody);
            expectedJson=new JSONObject(expectedResponseBody);
            sectionListViewRequest=new sectionListViewRequest();
            sectionListViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            LogUtils.info("Verify section list view payload prepared with mobile");
            ExtentReport.getTest().log(Status.INFO, "Verify section list view payload prepared with mobile: ");
            
            response=ResponseUtil.getResponseWithAuth(baseUri,sectionListViewRequest,httpsMethod,accessToken);
            LogUtils.info("Section list view API response");
            ExtentReport.getTest().log(Status.INFO, "Section list view API response: " + response.getBody().asString());
            System.out.println(response.getStatusCode());
            if (response.getStatusCode() == 200) 
            {
            	
                String responseBody = response.getBody().asString();
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    expectedJson = new JSONObject(expectedResponseBody);

                    validateResponseBody.handleResponseBody(response, expectedJson);
                    LogUtils.success(logger, "Successfully validated section list view API using positive input data");
                    ExtentReport.getTest().log(Status.PASS, "Successfully validated section list view API using positive input data");
                } else {
                    LogUtils.failure(logger, "Empty response body received");
                    ExtentReport.getTest().log(Status.FAIL, "Empty response body received");
                    throw new customException("Response body is empty");
                }
            } else {
                LogUtils.failure(logger, "Invalid status code for section list view API using positive input data: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Invalid status code for section list view API using positive input data: " + response.getStatusCode());
                throw new customException("In section list view API using positive input test case expected status code 200 but got " + response.getStatusCode());
            }
        }
    } catch (Exception e) {
        LogUtils.exception(logger, "An error occurred during section list view verification: " + e.getMessage(), e);
        ExtentReport.getTest().log(Status.FAIL, "An error occurred during section list view verification: " + e.getMessage());
        throw new customException("An error occurred during section list view verification");
    }
}

/**
 * Test method for negative scenarios
 */
@Test(dataProvider = "getSectionListViewNegativeInputData", priority = 2)
private void verifySectionListViewUsingInvalidData(String apiName, String testCaseId,
        String testType, String description, String httpsMethod,
        String requestBody, String expectedResponseBody, String statusCode) throws customException {

    try {
        LogUtils.info("=====Starting section list view API negative test=====");
        ExtentReport.createTest("Verify section list view using Invalid Input data: " + description);
        ExtentReport.getTest().log(Status.INFO, "====Verify section list view using Invalid Input data====");
        ExtentReport.getTest().log(Status.INFO, "Constructed section list view Base URI: " + baseUri);

        if (apiName.contains("sectionlistview") && testType.contains("negative")) {
            // Parse request and expected response
            requestBodyJson = new JSONObject(requestBody);
            expectedJson = new JSONObject(expectedResponseBody);

            sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            sectionrequest.setUser_id(userId);
            LogUtils.info("Section list view payload prepared");
            ExtentReport.getTest().log(Status.INFO, "Section list view payload prepared");

            // Make API call
            response = ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsMethod, accessToken);
            LogUtils.info("GET request executed for section list view API");
            ExtentReport.getTest().log(Status.INFO, "GET request executed for section list view API");

            // Validate response
            validateResponseBody.handleResponseBody(response, expectedJson);
            LogUtils.success(logger, "Section list view API responded with expected status code");
            ExtentReport.getTest().log(Status.PASS, "Section list view API responded with expected status code");

            LogUtils.info("Successfully validated section list view API negative test case: " + testCaseId);
            ExtentReport.getTest().log(Status.PASS, "Successfully validated section list view API negative test case: " + testCaseId);
        }
    } catch (Exception e) {
        LogUtils.exception(logger, "Error in negative test case " + testCaseId + ": " + e.getMessage(), e);
        ExtentReport.getTest().log(Status.FAIL, "Error in negative test case " + testCaseId + ": " + e.getMessage());
        throw new customException("Error in negative test case " + testCaseId + ": " + e.getMessage());
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
