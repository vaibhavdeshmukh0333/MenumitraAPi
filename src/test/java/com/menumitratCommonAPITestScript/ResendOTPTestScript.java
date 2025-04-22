package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.menumitra.apiRequest.ResendOTPRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.response.Response;

public class ResendOTPTestScript extends APIBase
{
    private String baseURI;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private ResendOTPRequest resendOTPRequest;
    private Response response;
    
    @DataProvider(name="getResendURl")
    private Object[][] getResendURl() throws customException
    {
    	try
    	{
    		LogUtils.info("Starting to read resendOTP API endpoint data from Excel sheet");
    		Object[][] readData=DataDriven.readExcelData(excelSheetPathForGetApis,property.getProperty("commonAPI"));
    		LogUtils.info("Excel data read successfully for resendOTP API endpoint");
    		return Arrays.stream(readData)
    				.filter(row -> "resendOTP".contains(row[0].toString()))
    				.toArray(Object[][]::new);
    	}
    	catch (Exception e)
    	{
    		LogUtils.error("Failed to read resendOTP API endpoint data: " + e.getMessage());
            throw new customException("Error reading resendOTP API endpoint data from Excel sheet: " + e.getMessage());
		}
    }
    @DataProvider(name="getPositiveinputData")
    private Object[][] getPositiveinputData() throws customException
    {
        try
        {
            Object[][] readExcelData=DataDriven.readExcelData(excelSheetPathForGetApis,"CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0)
            {
                LogUtils.error("No positive test scenario data found in Excel sheet");
                throw new customException("No positive test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 2 &&
                    "resendotp".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
    	catch (Exception e) 
    	{
			LogUtils.error("Failed to read positive input data: " + e.getMessage());
            throw new customException("Error reading positive input data from Excel sheet: " + e.getMessage());
		}
    }

    
    
    
    @Test(dataProvider="getResendURl",priority=0)
    private void getResendOTPURL(String apiName,String method,String endpoint,String requestBody, String responseBody,
    	    String statusCode) throws customException
    {
    	baseURI=EnviromentChanges.getBaseUrl();
       	baseURI=RequestValidator.buildUri(endpoint,baseURI);
       	LogUtils.info("Success:get ResendOTP Base URI");
       			
    }

    @Test(dataProvider="getPositiveinputData",priority=1)
    private void verifyRessendOTPUSingPositiveInputData(String apiName,String testCaseid, String testType, String description,
    	    String httpsmethod,String requestBody,String expectedResponseBody,String statusCode) throws customException
    {
    	try
    	{
    		if(apiName.equalsIgnoreCase("resendotp") && testType.equalsIgnoreCase("positive"))
    		{
    			requestBodyJson = new JSONObject(requestBody);
    			resendOTPRequest = new ResendOTPRequest();
    			resendOTPRequest.setMobile(requestBodyJson.getString("mobile"));
    			resendOTPRequest.setRole(requestBodyJson.getString("role"));
    			response=ResponseUtil.getResponse(baseURI,resendOTPRequest, httpsmethod);
    			
    			if(response.getStatusCode()==200)
    			{
    				actualResponseBody = new JSONObject(response.getBody().asString());
					expectedResponse=new JSONObject(expectedResponseBody);
					verifyResponseBody(actualResponseBody, expectedResponse, response.getStatusCode());
					
    			}
    			
    		}
    	}
    	catch (Exception e) {
			LogUtils.error("Error verifying resend OTP using positive input data: " + e.getMessage());
            throw new customException("Error verifying resend OTP using positive input data: " + e.getMessage());
		}
        
    }
    
    
    
    
    private void verifyResponseBody(JSONObject actualResponseBody, JSONObject expectedResponse, int statusCode)
    {
		try{
			if(statusCode == 200)
    		{
				
			}
			else if(statusCode == 400)
			{

			}
		}
		catch (JSONException e)
		{
			LogUtils.error("Error verifying response body: " + e.getMessage());
			Assert.fail("Error verifying response body: " + e.getMessage());
		}
    }
}