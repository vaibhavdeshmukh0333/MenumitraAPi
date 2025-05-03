package com.menumitratCommonAPITestScript;



	import java.net.URL;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.List;
	import java.util.Objects;

	import org.apache.log4j.Logger;
	import org.json.JSONObject;
	import org.testng.annotations.BeforeClass;
	import org.testng.annotations.DataProvider;
	import org.testng.annotations.Listeners;
	import org.testng.annotations.Test;

	import com.aventstack.extentreports.Status;
	import com.aventstack.extentreports.markuputils.ExtentColor;
	import com.aventstack.extentreports.markuputils.MarkupHelper;
	import com.menumitra.apiRequest.SupplierViewRequest;
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

	import io.restassured.response.Response;

	@Listeners(Listener.class)
	public class supplierViewTestScript extends APIBase
	{
	    private JSONObject requestBodyJson;
	    private Response response;
	    private String baseURI;
	    private String accessToken;
	    private SupplierViewRequest supplierViewRequest;
	    private URL url;
	    Logger logger = LogUtils.getLogger(supplierViewTestScript.class);

	    @DataProvider(name = "getSupplierViewUrl")
	    private Object[][] getSupplierViewUrl() throws customException
	    {
	        try {
	            LogUtils.info("Reading supplier view URL from Excel sheet");
	            ExtentReport.getTest().log(Status.INFO, "Reading supplier view URL from Excel sheet");

	            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
	            if (readExcelData == null) {
	                String errorMsg = "Error fetching data from Excel sheet - Data is null";
	                LogUtils.failure(logger, errorMsg);
	                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                throw new customException(errorMsg);
	            }

	            Object[][] filteredData = Arrays.stream(readExcelData)
	                    .filter(row -> "supplierview".equalsIgnoreCase(row[0].toString()))
	                    .toArray(Object[][]::new);

	            if (filteredData.length == 0) {
	                String errorMsg = "No supplier view URL data found after filtering";
	                LogUtils.failure(logger, errorMsg);
	                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                throw new customException(errorMsg);
	            }

	            LogUtils.info("Successfully retrieved supplier view URL data");
	            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved supplier view URL data");
	            return filteredData;
	        } catch (Exception e) {
	            String errorMsg = "Error in getSupplierViewUrl: " + e.getMessage();
	            LogUtils.exception(logger, errorMsg, e);
	            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	            throw new customException(errorMsg);
	        }
	    }

	    @DataProvider(name = "getSupplierViewValidData")
	    public Object[][] getSupplierViewValidData() throws customException {
	        try {
	            LogUtils.info("Reading supplier view valid data from Excel sheet");
	            ExtentReport.getTest().log(Status.INFO, "Reading supplier view valid data from Excel sheet");

	            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
	            if (readExcelData == null) {
	                String errorMsg = "Error fetching data from Excel sheet - Data is null";
	                LogUtils.failure(logger, errorMsg);
	                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                throw new customException(errorMsg);
	            }
	            List<Object[]> filteredData = new ArrayList<>();

	            for (int i = 0; i < readExcelData.length; i++) {
	                Object[] row = readExcelData[i];
	                if (row != null && row.length >= 3 &&
	                        "supplierview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
	                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

	                    filteredData.add(row);
	                }
	            }

	            if (filteredData.isEmpty()) {
	                String errorMsg = "No valid supplier view test data found after filtering";
	                LogUtils.failure(logger, errorMsg);
	                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                throw new customException(errorMsg);
	            }

	            Object[][] obj = new Object[filteredData.size()][];
	            for (int i = 0; i < filteredData.size(); i++) {
	                obj[i] = filteredData.get(i);
	            }

	            LogUtils.info("Successfully retrieved " + obj.length + " supplier view test scenarios");
	            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " supplier view test scenarios");
	            return obj;
	        } catch (Exception e) {
	            String errorMsg = "Error in getSupplierViewValidData: " + e.getMessage();
	            LogUtils.exception(logger, errorMsg, e);
	            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	            throw new customException(errorMsg);
	        }
	    }

	    @BeforeClass
	    private void supplierViewSetUp() throws customException {
	        try {
	            LogUtils.info("Setting up supplier view test");
	            ExtentReport.createTest("Supplier View Test Setup");
	            ExtentReport.getTest().log(Status.INFO, "Initializing supplier view test setup");

	            ActionsMethods.login();
	            ActionsMethods.verifyOTP();
	            baseURI = EnviromentChanges.getBaseUrl();

	            Object[][] supplierViewData = getSupplierViewUrl();
	            if (supplierViewData.length > 0) {
	                String endpoint = supplierViewData[0][2].toString();
	                url = new URL(endpoint);
	                baseURI = RequestValidator.buildUri(endpoint, baseURI);
	                LogUtils.info("Constructed base URI: " + baseURI);
	                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
	            } else {
	                String errorMsg = "No supplier view URL found in test data";
	                LogUtils.failure(logger, errorMsg);
	                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                throw new customException(errorMsg);
	            }

	            accessToken = TokenManagers.getJwtToken();
	            if (accessToken.isEmpty()) {
	                String errorMsg = "Required tokens not found. Please ensure login and OTP verification is completed";
	                LogUtils.failure(logger, errorMsg);
	                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                throw new customException(errorMsg);
	            }

	            supplierViewRequest = new SupplierViewRequest();
	            LogUtils.info("Supplier view test setup completed successfully");
	            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier view test setup completed successfully", ExtentColor.GREEN));
	        } catch (Exception e) {
	            String errorMsg = "Error in supplier view setUp: " + e.getMessage();
	            LogUtils.exception(logger, errorMsg, e);
	            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	            throw new customException(errorMsg);
	        }
	    }

	    @Test(dataProvider = "getSupplierViewValidData")
	    private void verifySupplierView(String apiName, String testCaseid, String testType, String description,
	            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
	        try {
	            LogUtils.info("Starting supplier view test case: " + testCaseid);
	            ExtentReport.createTest("Supplier View Test - " + testCaseid);
	            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

	            if (apiName.equalsIgnoreCase("supplierview")) {
	                requestBodyJson = new JSONObject(requestBody);
	                supplierViewRequest.setSupplier_id(String.valueOf(requestBodyJson.getString("supplier_id")));
	                supplierViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));

	                LogUtils.info("Request Body: " + requestBodyJson.toString());
	                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

	                response = ResponseUtil.getResponseWithAuth(baseURI, supplierViewRequest, httpsmethod, accessToken);

	                LogUtils.info("Response Status Code: " + response.getStatusCode());
	                LogUtils.info("Response Body: " + response.asString());
	                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
	                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

	                // Validate status code
	                if (response.getStatusCode() != Integer.parseInt(statusCode)) {
	                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
	                    LogUtils.failure(logger, errorMsg);
	                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                    throw new customException(errorMsg);
	                }

	                LogUtils.success(logger, "Supplier view test completed successfully\nResponse: " + response.asPrettyString());
	                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier view test completed successfully", ExtentColor.GREEN));
	                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
	            }
	        } catch (Exception e) {
	            String errorMsg = "Error in supplier view test: " + e.getMessage();
	            LogUtils.exception(logger, errorMsg, e);
	            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	            if (response != null) {
	                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
	                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
	            }
	            throw new customException(errorMsg);
	        }
	    }
	}

