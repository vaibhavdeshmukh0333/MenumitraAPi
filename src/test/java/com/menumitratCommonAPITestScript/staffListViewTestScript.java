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
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.StaffListViewRequest;
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
public class staffListViewTestScript extends APIBase
{

    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private StaffListViewRequest staffListViewRequest;
    private URL url;
    Logger logger=LogUtils.getLogger(staffListViewTestScript.class);
   
    @BeforeClass
    private void StaffListViewsetUp() throws customException
    {
    	try
    	{
    		LogUtils.info("Staff List View SetUp");
            ExtentReport.createTest("Staff List View SetUp");
            ExtentReport.getTest().log(Status.INFO,"Staff List View SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI=EnviromentChanges.getBaseUrl();
            
            
            Object[][] getUrl=getStaffListViewUrl();
            if (getUrl.length > 0) 
            {
          
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
            	 LogUtils.failure(logger, "No staff create URL found in test data");
                 ExtentReport.getTest().log(Status.FAIL, "No staff create URL found in test data");
                 throw new customException("No staff list view URL found in test data");
            }
            
            accessToken=TokenManagers.getJwtToken();
            if(accessToken.isEmpty())
            {
                ActionsMethods.login();
                ActionsMethods.verifyOTP();
                accessToken=TokenManagers.getJwtToken();
            	LogUtils.failure(logger,"Access Token is Empty check access token");
            	ExtentReport.getTest().log(Status.FAIL,MarkupHelper.createLabel("Access Token is Empty check access token",ExtentColor.RED));
                throw new customException("Access Token is Empty check access token");
            }
            
            staffListViewRequest=new StaffListViewRequest();
          
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
            Object[][] readData=DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

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

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
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
        
    
        try
        {
            LogUtils.info("staff list view test execution: "+description);
            ExtentReport.createTest("staff list view test execution");
            ExtentReport.getTest().log(Status.INFO,"staff list view test execution: "+description);

            if(apiName.equalsIgnoreCase("stafflistview"))
            {
                requestBodyJson=new JSONObject(requestBody);

               staffListViewRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
               LogUtils.info("Constructed staff list view request"); 
               ExtentReport.getTest().log(Status.INFO,"Constructed staff list view request");

               response=ResponseUtil.getResponseWithAuth(baseURI, staffListViewRequest, httpsmethod, accessToken);
               LogUtils.info("Received response with status code: "+response.getStatusCode());
               ExtentReport.getTest().log(Status.INFO,"Received response with status code: "+response.getStatusCode());

               if(response.getStatusCode()==200)
               {
                LogUtils.info("Staff list view Api executed successfully: "+response.asPrettyString());
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Staff list View API executed successfully:"+response.asPrettyString(), ExtentColor.GREEN));
               }
               else{
                LogUtils.failure(logger,"Staff list view Api executed failed: "+response.asPrettyString());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Staff list View API executed failed:"+response.asPrettyString(), ExtentColor.RED));
               }
            }
        }
        catch(Exception e)
        {
            LogUtils.exception(logger,"Error in staff list view test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in staff list view test: "+e.getMessage());
            throw new customException("Error in staff list view test: "+e.getMessage());
        }
    }
}
