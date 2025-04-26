package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.menumitra.apiRequest.StaffListViewRequest;
import com.menumitra.apiRequest.staffRequest;
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
public class staffListViewTestScript 
{
    private JSONObject actualResponse;
    private JSONObject expectedResponse;
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private static int staffId;
    private String access;
    private String device_token;
    private String outlet_id;
    private staffRequest staffrequest;
    private StaffListViewRequest staffListViewRequest;
    private validateResponseBody validateResponseBody;
  
   
    @BeforeClass
    private void StaffListViewsetUp() throws customException
    {
    	try
    	{
    		LogUtils.info("Start Staff List View setUp..");
            staffrequest=new staffRequest();

            TokenManagers.login();
            TokenManagers.verifyOtp();

            access=TokenManagers.getJwtToken();
            device_token=TokenManagers.getDeviceToken();
            outlet_id=staffrequest.getOutlet_id();
            Object[][] getUrl=getStaffListViewUrl();
            String uri=getUrl[0][2].toString();
            URL url=new URL(uri);
            
    		baseURI=RequestValidator.buildUri(EnviromentChanges.getBaseUrl(),url.toString());
            staffListViewRequest=new StaffListViewRequest();
            validateResponseBody=new validateResponseBody();
    		LogUtils.info("setup completed successfully");
    	}
        catch(Exception e)
        {
            throw new customException("Error staff setUp");
        }
    }
    

    @DataProvider(name="getStaffListViewUrl")
    private Object[][] getStaffListViewUrl() throws customException
    {
        try
        {
            LogUtils.info("Start get Staff list view URl from excelSheet");
            Object[][] readData=DataDriven.readExcelData("excelSheetPathForGetApis", "commonAPI");

            if(readData==null)
            {
                LogUtils.error("Error featching data from excel sheet");
                throw new customException("Error featching data from excel sheet");
            }
            else
            {
              return  Arrays.stream(readData)
                .filter(row ->"stafflistview".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
            }
        }
        catch(Exception e)
        {
            LogUtils.error("Error: featching data from excelSheet. check excelsheet path");
            throw new customException("Error: featching data from excelSheet. check excelsheet path");
        }
    }
    
    /**
     * Data provider for staff list view test scenarios
     */
    @DataProvider(name = "getStaffListViewPositiveData") 
    public static Object[][] getStaffListViewPositiveData() throws customException {
        try {
            LogUtils.info("Reading staff list view test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData("excelSheetPathForGetApis", "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff list view test scenario data found in Excel sheet");
                throw new customException("No staff list view test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "stafflistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
            LogUtils.error("Error while reading staff list view test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading staff list view test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading staff list view test scenario data from Excel sheet: " + e.getMessage());
        }
    }
   
    
    @Test(dataProvider = "getStaffListViewPositiveData")
    private void verifyStaffListViewUsingPositiveData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting staff list view test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Starting staff list view test: " + description);

            if (apiName.contains("stafflistview")) {
                requestBodyJson = new JSONObject(requestBody);

                staffListViewRequest = new StaffListViewRequest();
                staffListViewRequest.setDevice_token(device_token);
                staffListViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));

                response = ResponseUtil.getResponseWithAuth(baseURI, staffListViewRequest, httpsmethod, access);
                actualResponse = new JSONObject(response.body().toString());
                expectedResponse = new JSONObject(expectedResponseBody);

                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.info("Staff list view successful with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS,
                            "Staff list view successful with status code: " + response.getStatusCode());

                    validateResponseBody.handleResponseBody(actualResponse.get("st").toString(),
                            expectedResponse.get("status").toString(),
                            response.getStatusCode());
                    validateResponseBody.handleResponseBody(actualResponse.get("msg").toString(),
                            expectedResponse.get("msg").toString(),
                            response.getStatusCode());

                    if (actualResponse.has("lists")) {
                        JSONObject staffList = actualResponse.getJSONObject("lists");
                        if (!staffList.isEmpty()) {
                            staffId = staffList.getInt("staff_id");
                            LogUtils.info("Staff ID retrieved: " + staffId);
                        } else {
                            staffId = 0;
                            LogUtils.info("No staff found in response");
                        }
                    }
                } else {
                    LogUtils.error("Staff list view failed. Expected status code: " + statusCode + ", Actual: "
                            + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Staff list view failed. Expected status code: " + statusCode
                            + ", Actual: " + response.getStatusCode());
                }
            }
        } catch (Exception e) {
            LogUtils.error("Error during staff list view: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during staff list view: " + e.getMessage());
            throw new customException("Error during staff list view: " + e.getMessage());
        }
    }
}
