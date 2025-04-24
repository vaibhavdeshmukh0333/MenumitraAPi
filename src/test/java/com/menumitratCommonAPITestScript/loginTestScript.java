package com.menumitratCommonAPITestScript;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import com.menumitra.apiRequest.loginRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.validateResponseBody;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import io.restassured.response.Response;


/**
 * Test class for Login API functionality
 * This class extends superclass to inherit common functionality
 */
@Listeners(com.menumitra.utilityclass.Listener.class)
public class loginTestScript extends APIBase 
{

    private String baseUri=null;
    //private String method;
    JSONObject jsonRequestBody;
    private URL url;
    private loginRequest lr;
    JSONObject actualResponseBody;
    JSONObject expectedResponse;
    
    @DataProvider(name="getLoginUrl")
    private Object[][] getLoginUrl() throws customException
    {
        try {
            LogUtils.info("Reading API endpoint data from Excel sheet");
            Object[][] apiData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
           
            return Arrays.stream(apiData)
                    .filter(row -> "login".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

           
        }
        catch (Exception e) {
            LogUtils.error("Failed to read Login API endpoint data: " + e.getMessage());
            throw new customException("Error reading Login API endpoint data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * 
     * @return
     * @throws customException
     */
    /**
     * @return
     * @throws customException
     */
    @DataProvider(name="getInputData") 
    private Object[][] getInputData() throws customException {
        try {
            LogUtils.info("Reading test scenario data from Excel sheet");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis,property.getProperty("CommonAPITestScenario"));
            
            
            
            if (testData == null || testData.length == 0) {
            	LogUtils.error("No test scenario data found in Excel sheet");
                throw new customException("No test scenario data found in Excel sheet");
            }         
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                    "login".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
            // return Arrays.stream(testData)
            // 		.filter(row -> "login".equalsIgnoreCase(row[0].toString()) && "positive".equalsIgnoreCase(row[2].toString()))
            // 		.toArray(Object[][]::new);
            
           
        }
        catch (Exception e) {
            LogUtils.error("Failed to read test scenario data: " + e.getMessage());
            throw new customException("Error reading test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void testLoginApi() {
        LogUtils.info("Setting up base URI based on environment");
        //ExtentReport.getTest().log(Status.INFO, "Environment: " + EnviromentChange.getCurrentEnvironment().getName());
        baseUri = EnviromentChanges.getBaseUrl(); // Using utility method to get base URL
        LogUtils.info("Base URI set to: " + baseUri);
    }
    
    @Test(dataProvider="getLoginUrl",priority = 0)
    private void getUrl(String apiName,String method,String endpoint,String requestBody, String responseBody,
    String statusCode) throws MalformedURLException, customException
    {
    	url=new URL(endpoint);
    	
    	if(apiName.contains("login"))
    	{
    		baseUri=RequestValidator.buildUri(endpoint,baseUri);
    	}
    }

    @Test(dataProvider = "getInputData",priority = 1)
    private void verifyloginUsingValidInputData(String apiName,String testCaseid, String testType, String description,
    		String httpsmethod,String requestBody, String statusCode,String expectedResponseBody ) throws customException
    {
        try{
            ExtentReport.getTest().log(Status.INFO,"start login api using valid input data");
            if(apiName.contains("login")&&testType.contains("positive"))
            {
            	jsonRequestBody=new JSONObject(requestBody);
            	lr=new loginRequest();
            	lr.setMobile(jsonRequestBody.get("mobile").toString());
                
                Response response=ResponseUtil.getResponse(baseUri,lr,httpsmethod);
                ExtentReport.getTest().log(Status.INFO,"login api response: "+response.getBody().asString());

                if(!(response.getStatusCode()==500))
                {
                    actualResponseBody=new JSONObject(response.getBody().asString());
                    expectedResponse=new JSONObject(expectedResponseBody);
                    verifyApiResponseBody(actualResponseBody, expectedResponse,response.getStatusCode());
                    LogUtils.info("Successfully Validate login response");
                    ExtentReport.getTest().log(Status.PASS,"Successfully Validate login response");
                }
                
                LogUtils.info("Success check login using valid input data");
                ExtentReport.getTest().log(Status.PASS,"Success check login using valid input data");
            }

        }
        catch(Exception e)
        {
            LogUtils.error("An error occurred during login verification: "+e.getMessage());
            ExtentReport.getTest().log(Status.FAIL,"An error occurred during login verification: "+e.getMessage());
            throw new customException("An error occurred during login verification");
        }
    }


    public void verifyApiResponseBody(JSONObject actualResponse,JSONObject expectedResponse, int statusCode) throws customException
    {
        try{
            LogUtils.info("Verifying API response body");
            ExtentReport.getTest().log(Status.INFO,"Verifying API response body");
            if(statusCode==200)
            {
                validateResponseBody.handleResponseBody(actualResponse.get("st").toString(), expectedResponse.get("st").toString(), statusCode);
                validateResponseBody.handleResponseBody(actualResponse.get("msg").toString(),expectedResponse.get("msg").toString(),statusCode);
                LogUtils.info("Successfully Validate login response");
                ExtentReport.getTest().log(Status.PASS,"Successfully Validate login response");
            }
            else if(statusCode==400)
            {
                
                LogUtils.error("Failed to validate login response. getting an 400 erroe message");
                ExtentReport.getTest().log(Status.FAIL,"Failed to validate login response. getting an 400 erroe message");
            }
            else if(statusCode==500)
            {
                //validateResponseBody.handleErrorResponse(actualResponse,expectedResponse,statusCode);
                LogUtils.error("Failed to validate login response. getting an 500 erroe message");
                ExtentReport.getTest().log(Status.FAIL,"Failed to validate login response. getting an 500 erroe message");
            }
        }
        catch(Exception e)
        {
            LogUtils.error("An error occurred during login verification: "+e.getMessage());
            ExtentReport.getTest().log(Status.FAIL,"An error occurred during login verification: "+e.getMessage());
            throw new customException("An error occurred during login verification");
        }
    }


}