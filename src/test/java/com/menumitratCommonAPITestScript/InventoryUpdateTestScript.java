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
import com.menumitra.apiRequest.InventoryRequest;
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
public class InventoryUpdateTestScript extends APIBase
{
    private InventoryRequest inventoryUpdateRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(InventoryUpdateTestScript.class);

    /**
     * Data provider for inventory update API endpoint URLs
     */
    @DataProvider(name = "getInventoryUpdateUrl")
    public static Object[][] getInventoryUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Inventory Update API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "inventoryUpdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Inventory Update API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Inventory Update API endpoint data from Excel sheet");
            throw new customException("Error While Reading Inventory Update API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for inventory update test scenarios
     */
    @DataProvider(name = "getInventoryUpdateData")
    public static Object[][] getInventoryUpdateData() throws customException {
        try {
            LogUtils.info("Reading inventory update test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No inventory update test scenario data found in Excel sheet");
                throw new customException("No inventory update test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "inventoryupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for inventory update");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading inventory update test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading inventory update test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading inventory update test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @DataProvider(name = "getInventoryUpdateNegativeData")
    public Object[][] getInventoryUpdateNegativeData() throws customException {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No inventory update test scenario data found in Excel sheet");
                ExtentReport.getTest().log(Status.ERROR, "No inventory update test scenario data found in Excel sheet");
                throw new customException("No inventory update test scenario data found in Excel sheet");
            }
            List<Object[]> filterData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && "inventoryupdate".equalsIgnoreCase(Objects.toString(row[0], ""))
                        && "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filterData.add(row);
                }
            }
            Object[][] obj = new Object[filterData.size()][];
            for (int i = 0; i < filterData.size(); i++) {
                obj[i] = filterData.get(i);
            }
            LogUtils.info("Successfully retrieved " + obj.length + " negative test scenarios for inventory update");
            ExtentReport.getTest().log(Status.INFO, "Successfully retrieved " + obj.length + " negative test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading inventory update negative test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading inventory update negative test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading inventory update negative test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for inventory update test====");
            ExtentReport.createTest("Inventory Update Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set inventory update URL
            Object[][] inventoryUpdateData = getInventoryUpdateUrl();
            if (inventoryUpdateData.length > 0) {
                String endpoint = inventoryUpdateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for inventory update: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No inventory update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No inventory update URL found in test data");
                throw new customException("No inventory update URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            inventoryUpdateRequest = new InventoryRequest();
            LogUtils.success(logger, "Inventory Update Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Inventory Update Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during inventory update setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during inventory update setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to update inventory
     */
    @Test(dataProvider = "getInventoryUpdateData")
    private void updateInventory(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting inventory update test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Inventory Update Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            inventoryUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            inventoryUpdateRequest.setUser_id(String.valueOf(user_id));
            inventoryUpdateRequest.setName(requestBodyJson.getString("name"));
            inventoryUpdateRequest.setSupplier_id(requestBodyJson.getString("supplier_id"));
            inventoryUpdateRequest.setCategory_id(requestBodyJson.getString("category_id"));
            inventoryUpdateRequest.setDescription(requestBodyJson.getString("description"));
            inventoryUpdateRequest.setUnit_price(requestBodyJson.getString("unit_price"));
            inventoryUpdateRequest.setQuantity(requestBodyJson.getString("quantity"));
            inventoryUpdateRequest.setUnit_of_measure(requestBodyJson.getString("unit_of_measure"));
            inventoryUpdateRequest.setReorder_level(requestBodyJson.getString("reorder_level"));
            inventoryUpdateRequest.setBrand_name(requestBodyJson.getString("brand_name"));
            inventoryUpdateRequest.setTax_rate(requestBodyJson.getString("tax_rate"));
            inventoryUpdateRequest.setIn_or_out(requestBodyJson.getString("in_or_out"));
            inventoryUpdateRequest.setIn_date(requestBodyJson.getString("in_date"));
            inventoryUpdateRequest.setExpiration_date(requestBodyJson.getString("expiration_date"));
            inventoryUpdateRequest.setInventory_id(requestBodyJson.getString("inventory_id"));
            
            LogUtils.info("Final Request Body prepared for inventory update");

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, inventoryUpdateRequest, httpsmethod, accessToken); 
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validation
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                actualResponseBody = new JSONObject(response.asString());
                
                if (!actualResponseBody.isEmpty()) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                    LogUtils.info("Starting response body validation");
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                    LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                    LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));
                    
                    ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                    LogUtils.info("Performing detailed response validation");
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                    
                    ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                    LogUtils.success(logger, "Response body validation passed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Inventory updated successfully", ExtentColor.GREEN));
                } else {
                    ExtentReport.getTest().log(Status.INFO, "Response body is empty");
                    LogUtils.info("Response body is empty");
                }
            } else {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                LogUtils.failure(logger, errorMsg);
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Test execution failed: " + e.getMessage();
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            LogUtils.error(errorMsg);
            LogUtils.error("Stack trace: " + Arrays.toString(e.getStackTrace()));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body:\n" + response.asPrettyString());
                LogUtils.error("Failed Response Status Code: " + response.getStatusCode());
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
    
    //@AfterClass
    private void tearDown() {
        try {
            LogUtils.info("===Test environment tear down started===");
            ExtentReport.createTest("Inventory Update Test Teardown");
            
            LogUtils.info("Logging out user");
            ActionsMethods.logout();
            
            LogUtils.info("Clearing tokens");
            TokenManagers.clearTokens();
            
            LogUtils.info("===Test environment tear down completed successfully===");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }
}