package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.xml.transform.sax.SAXTransformerFactory;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Log;
import com.menumitra.apiRequest.staffRequest;
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

import io.restassured.response.Response;

@Listeners(Listener.class)
public class StaffDeleteTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private staffRequest staffDeleteRequest;
    private URL url;
    private int user_id;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(StaffDeleteTestScript.class);

    @DataProvider(name="getStaffDeleteUrl")
    private Object[][] getStaffDeleteUrl() throws customException {
        try {
            LogUtils.info("Reading Staff Delete API endpoint data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if(readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No Staff Delete API endpoint data found in Excel sheet");
                throw new customException("No Staff Delete API endpoint data found in Excel sheet");
            }

           
            LogUtils.info("Successfully retrieved Staff Delete API endpoint data");
            return Arrays.stream(readExcelData)
            		.filter(row->"staffDelete".equalsIgnoreCase(row[0].toString()))
            		.toArray(Object[][]::new);
            
        }
        catch(Exception e) {
            LogUtils.error("Error while reading Staff Delete API endpoint data from Excel sheet: " + e.getMessage());
            throw new customException("Error while reading Staff Delete API endpoint data from Excel sheet: " + e.getMessage());
        }
    }

    @DataProvider(name = "getStaffDeleteData")
    public static Object[][] getStaffDeleteData() throws customException {
        try {
            LogUtils.info("Reading staff delete test scenario data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff delete test scenario data found in Excel sheet");
                throw new customException("No staff delete test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "staffdelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading staff delete test scenario data from Excel sheet: " + e.getMessage());
            throw new customException("Error while reading staff delete test scenario data from Excel sheet: " + e.getMessage());
        }
    }

   

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====start setup delete staff====");
            ExtentReport.createTest("Delete Staff Setup");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] staffDeleteData = getStaffDeleteUrl();
            if (staffDeleteData.length > 0) 
            {
                String endpoint = staffDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseURI=RequestValidator.buildUri(endpoint, baseURI);
                
            }
            
            accessToken = TokenManagers.getJwtToken();
            user_id=TokenManagers.getUserId();
            staffDeleteRequest = new staffRequest();
            
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        } catch (Exception e) {
            LogUtils.error("Error in setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in setup: " + e.getMessage());
            throw new customException("Error in setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getStaffDeleteData")
    public void testStaffDelete(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            ExtentReport.createTest("Staff Delete Test - " + testCaseid);
            LogUtils.info("Executing staff delete test for scenario: " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Executing staff delete test "+description);
            
            requestBodyJson = new JSONObject(requestBody);
            staffDeleteRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            staffDeleteRequest.setStaff_id(String.valueOf(requestBodyJson.getInt("staff_id")));
            staffDeleteRequest.setUser_id(user_id);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            response = ResponseUtil.getResponseWithAuth(baseURI, staffDeleteRequest, httpsmethod, accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

            // Validate status code
            if(response.getStatusCode() != Integer.parseInt(statusCode)) {
                String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            // Validate response body
            actualJsonBody = new JSONObject(response.asString());
            if(expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                expectedJsonBody = new JSONObject(expectedResponseBody);
                
                if(!expectedJsonBody.similar(actualJsonBody)) {
                    String errorMsg = "Response body mismatch\nExpected: " + expectedJsonBody.toString(2) + "\nActual: " + actualJsonBody.toString(2);
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
            }

            // Success case
            String successMsg = "Staff deleted successfully";
            LogUtils.success(logger, successMsg + "\nResponse: " + response.asPrettyString());
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(successMsg, ExtentColor.GREEN));
            ExtentReport.getTest().log(Status.PASS, "Expected Response: " + (expectedJsonBody != null ? expectedJsonBody.toString(2) : "No expected response provided"));
            ExtentReport.getTest().log(Status.PASS, "Actual Response: " + actualJsonBody.toString(2));
            
        }
        catch (Exception e) 
        {
            String errorMsg = "Error during staff deletion test execution: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
}
