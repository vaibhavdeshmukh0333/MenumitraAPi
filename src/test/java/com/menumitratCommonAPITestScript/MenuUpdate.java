package com.menumitratCommonAPITestScript;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.Listener;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(Listener.class)
public class MenuUpdate extends APIBase {
    private Response response;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(MenuUpdate.class);

    /**
     * Data provider for menu update API endpoint URLs
     */
    @DataProvider(name = "getMenuUpdateUrl")
    public static Object[][] getMenuUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Menu Update API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "menuUpdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Menu Update API endpoint data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Menu Update API endpoint data from Excel sheet: " + e.getMessage());
            throw new customException("Error While Reading Menu Update API endpoint data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * Data provider for menu update test scenarios
     */
    @DataProvider(name = "getMenuUpdateData")
    public static Object[][] getMenuUpdateData() throws customException {
        try {
            LogUtils.info("Reading menu update test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No menu update test scenario data found in Excel sheet");
                throw new customException("No menu update test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "menuupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for menu update");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading menu update test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading menu update test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading menu update test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for menu update test====");
            ExtentReport.createTest("Menu Update Setup");

            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();

            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);

            // Get and set menu update URL
            Object[][] menuUpdateData = getMenuUpdateUrl();
            if (menuUpdateData.length > 0) {
                String endpoint = menuUpdateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for menu update: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No menu update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No menu update URL found in test data");
                throw new customException("No menu update URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            LogUtils.success(logger, "Menu Update Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Menu Update Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during menu update setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during menu update setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to update menu
     */
    @Test(dataProvider = "getMenuUpdateData")
    private void updateMenu(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        
        RequestSpecification request = null;
        
        try {
            LogUtils.info("Starting menu update test case: " + testCaseid);
            ExtentReport.createTest("Menu Update Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            // Parse request payload
            LogUtils.info("Parsing request payload from Excel");
            try {
                requestBodyJson = new JSONObject(requestBodyPayload);
                LogUtils.info("Request payload parsed successfully");
            } catch (Exception e) {
                LogUtils.error("Failed to parse request payload: " + e.getMessage());
                ExtentReport.getTest().log(Status.ERROR, "Failed to parse request payload: " + e.getMessage());
                throw new customException("JSON parsing error: " + e.getMessage());
            }
            
            // Initialize request
            LogUtils.info("Initializing multipart request");
            request = RestAssured.given()
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType("multipart/form-data");
            
            // Add form fields from Excel data
            try {
                LogUtils.info("Adding form fields from Excel data");
                
                // Add user_id (use from token if not in payload)
                if (requestBodyJson.has("user_id")) {
                    request.multiPart("user_id", requestBodyJson.getString("user_id"));
                } else {
                    request.multiPart("user_id", String.valueOf(user_id));
                }
                
                // Add required fields
                request.multiPart("outlet_id", requestBodyJson.getString("outlet_id"));
                request.multiPart("menu_id", requestBodyJson.getString("menu_id"));
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
                if (requestBodyJson.has("existing_image_ids")) {
                    JSONArray array = requestBodyJson.getJSONArray("existing_image_ids");
                    for (int i = 0; i < array.length(); i++) {
                        request.multiPart("existing_image_ids[]", array.get(i).toString());
                    }
                }
                
                // Process images if present
                if (requestBodyJson.has("images") && !requestBodyJson.isNull("images")) {
                    JSONArray imagesArray = requestBodyJson.getJSONArray("images");
                    processImageArray(request, imagesArray);
                }
                
                LogUtils.info("All form fields added successfully");
            } catch (Exception e) {
                LogUtils.error("Error adding form fields: " + e.getMessage());
                ExtentReport.getTest().log(Status.ERROR, "Error adding form fields: " + e.getMessage());
                throw new customException("Error adding form fields: " + e.getMessage());
            }
            
            // Log the prepared request
            LogUtils.info("Request prepared successfully");
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
            
            // Send the request
            LogUtils.info("Sending POST request to " + baseURI);
            response = request.post(baseURI);
            
            // Log response details
            int actualStatusCode = response.getStatusCode();
            String responseBody = response.asPrettyString();
            LogUtils.info("Response Status Code: " + actualStatusCode);
            LogUtils.info("Response Body: " + responseBody);
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + actualStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + responseBody);
            
            // Validate response status code
            if (actualStatusCode == Integer.parseInt(statusCode)) {
                LogUtils.success(logger, "Menu update test passed - Status Code matches: " + actualStatusCode);
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu updated successfully", ExtentColor.GREEN));
                
                // Validate response body if expected
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    JSONObject expectedJson = new JSONObject(expectedResponseBody);
                    JSONObject actualJson = new JSONObject(responseBody);
                    
                    // Check for success message
                    if (actualJson.has("message")) {
                        LogUtils.info("Response message: " + actualJson.getString("message"));
                        ExtentReport.getTest().log(Status.INFO, "Response message: " + actualJson.getString("message"));
                    }
                }
            } else {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + actualStatusCode;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
        } catch (Exception e) {
            String errorMsg = "Menu update failed: " + e.getMessage();
            LogUtils.error(errorMsg);
            e.printStackTrace();
            
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
    
    /**
     * Helper method to process a JSON array of image paths
     */
    private void processImageArray(RequestSpecification request, JSONArray imagesArray) {
        for (int i = 0; i < imagesArray.length(); i++) {
            try {
                String imagePath = imagesArray.getString(i);
                addImageToRequest(request, imagePath);
            } catch (Exception e) {
                LogUtils.error("Error processing image at index " + i + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Helper method to add a single image to the request
     */
    private void addImageToRequest(RequestSpecification request, String imagePath) {
        try {
            LogUtils.info("Processing image path: " + imagePath);
            
            // Normalize path by replacing backslashes
            String normalizedPath = imagePath.replace("\\", "/").trim();
            
            File imgFile = new File(normalizedPath);
            if (imgFile.exists() && imgFile.isFile()) {
                LogUtils.info("Image file exists, adding to request: " + imgFile.getAbsolutePath());
                request.multiPart("images", imgFile);
            } else {
                LogUtils.warn("Image file does not exist or is not a file: " + normalizedPath);
                ExtentReport.getTest().log(Status.WARNING, "Image file not found: " + normalizedPath);
            }
        } catch (Exception e) {
            LogUtils.error("Error adding image to request: " + e.getMessage());
        }
    }
}