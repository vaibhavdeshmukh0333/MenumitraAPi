package com.menumitraOwnerAPITestScript;

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
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import com.menumitra.apiRequest.verifyOTPRequest;
import com.menumitra.superclass.APIBase;

import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;

import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;

import io.restassured.response.Response;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class OwnerverifyOtpTestScript extends APIBase {

    verifyOTPRequest verifyOTPRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri=null;
    private URL url;
    
    @DataProvider(name="getVerifyotpUrl")
    public Object[][] getVerifyotpUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading Verify OTP API endpoint data from Excel sheet");
            Object[][] readExcelData=DataDriven.readExcelData(excelSheetPathForGetApis,"ownerAPI");

            return Arrays.stream(readExcelData)
            .filter(row -> "ownerVerifyotp".equalsIgnoreCase(row[0].toString()))
            .toArray(Object[][]::new);

        }
        catch(Exception e)
        {
            LogUtils.error("Error While Reading Verify OTP API endpoint data from Excel sheet");
            throw new customException("Error While Reading Verify OTP API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name="getPositiveInputData")
    public Object[][] getPositiveInputData() throws customException
    {
        try
        {
            Object[][] readExcelData=DataDriven.readExcelData(excelSheetPathForGetApis,"OwnerAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0)
            {
                LogUtils.error("No positive test scenario data found in Excel sheet");
                throw new customException("No positive test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                    "ownerVerifyOTP".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;   
            
        }
        catch(Exception e)
        {
            LogUtils.error("Error while reading positive test scenario data from Excel sheet: " + e.getMessage());
            throw new customException("Error while reading positive test scenario data from Excel sheet: " + e.getMessage());
        }
    }
    
    @BeforeClass
    private void setup() {
        LogUtils.info("Setting up base URI based on environment");
        
        baseUri = EnviromentChanges.getBaseUrl(); // Using utility method to get base URL
        LogUtils.info("Base URI set to: " + baseUri);
    }
    
 
    @Test(dataProvider="getVerifyotpUrl",priority = 0)
    private void getOwnerUrl(String apiName,String method,String endpoint,String requestBody, String responseBody,
    String statusCode) throws MalformedURLException, customException
    {
    	url=new URL(endpoint);
    	
    	if(apiName.contains("ownerVerifyotp"))
    	{
    		baseUri=RequestValidator.buildUri(endpoint,baseUri);
    	}
    }
   
    @Test(dataProvider="getPositiveInputData")
    private void verifyOTPAPiUsingValidInputData(String apiName,String testCaseid, String testType, String description,
    String httpsmethod,String requestBody,String expectedResponseBody,String statusCode) throws customException
    {
        try {
            if(apiName.contains("ownerVerifyOTP") && testType.contains("positive")) {
                requestBodyJson = new JSONObject(requestBody);
                verifyOTPRequest = new verifyOTPRequest();
                verifyOTPRequest.setMobile(requestBodyJson.get("mobile").toString());
                verifyOTPRequest.setOtp(requestBodyJson.get("otp").toString());
                verifyOTPRequest.setFcm_token(requestBodyJson.get("fcm_token").toString());
                verifyOTPRequest.setDevice_id(requestBodyJson.get("device_id").toString());
                verifyOTPRequest.setDevice_model(requestBodyJson.get("device_model").toString());

                response = ResponseUtil.getResponse(baseUri, verifyOTPRequest, httpsmethod);
                
                if(response.getStatusCode() == 200) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    actualResponseBody = new JSONObject(response.body().asString());
                    verifyOwnerVerifyResponseBody(actualResponseBody, expectedResponse, Integer.parseInt(statusCode));
                }
            }
        } catch(Exception e) {
            throw new customException("OTP verification failed: " + e.getMessage());
        }
    }

    private void verifyOwnerVerifyResponseBody(JSONObject actualResponse,JSONObject expectedResponse, int statusCode) throws customException
    {
        try {
            LogUtils.info("Verifying owner verify OTP response body");
            ExtentReport.getTest().log(Status.INFO, "Verifying owner verify OTP response body");
            if(statusCode == 200) {
                Assert.assertEquals(actualResponse.get("st"), expectedResponse.get("st"), "Status code mismatch");
                Assert.assertEquals(actualResponse.get("msg"), expectedResponse.get("msg"), "Message mismatch");
                LogUtils.info("Successfully validated owner verify OTP response");
                ExtentReport.getTest().log(Status.PASS, "Successfully validated owner verify OTP response");
            } 
            else if(statusCode == 400) {
                
            }
            else if(statusCode == 401) {
              
            }
            
        } catch (AssertionError e) {
            LogUtils.error("Assertion failed during owner verify OTP response validation: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Assertion failed during owner verify OTP response validation: " + e.getMessage());
            throw new customException("Assertion failed during owner verify OTP response validation: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.error("Error during owner verify OTP response validation: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during owner verify OTP response validation: " + e.getMessage());
            throw new customException("Error during owner verify OTP response validation: " + e.getMessage());
        }
    }
    
   
   
}
