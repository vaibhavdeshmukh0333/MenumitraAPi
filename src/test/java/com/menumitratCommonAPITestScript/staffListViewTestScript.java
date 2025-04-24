package com.menumitratCommonAPITestScript;

import java.util.Arrays;
import java.util.Objects;

import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.menumitra.apiRequest.StaffListViewRequest;
import com.menumitra.apiRequest.staffRequest;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;

import io.restassured.response.Response;

public class staffListViewTestScript 
{
    private JSONObject actualResponse;
    private JSONObject expectedResponse;
    private Response response;
    private String baseURI;
    private static int staffId;
    private String access;
    private String device_token;
    private String outlet_id;
    private staffRequest staffrequest;
    private StaffListViewRequest staffListViewRequest;

    public static int getStaffId() throws customException
    {
        if(staffId!=0)
        {
        	return staffId;
        }
        else
        {
        	throw new customException("Staff id is zero");
        }
    }

    @DataProvider(name="getStaffUrl")
    private Object[][] getStaffUrl() throws customException
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
    
    @BeforeClass
    private void setUp() throws customException
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
    		
    	}
        catch(Exception e)
        {
            throw new customException("Error staff setUp");
        }
    }
    
    
    @Test()
    private void verifyStaffListView() throws customException
    {
    	try
    	{
            staffListViewRequest=new StaffListViewRequest();
            staffListViewRequest.setOutlet_id(outlet_id);
            staffListViewRequest.setDevice_token(device_token);
            
            response=ResponseUtil.getResponseWithAuth(baseURI,staffListViewRequest,"post",access);
            
            if(response.getStatusCode()==200)
            {
            	actualResponse=new JSONObject(response.body().toString());
            	if(actualResponse.has("lists"))
            	{
            		JSONObject convertResponsetoArray=new JSONObject(actualResponse.getJSONObject("lists"));
            		if(!convertResponsetoArray.isEmpty())
            		{
            			staffListViewRequest.setStaffId(convertResponsetoArray.getInt("staff_id"));
            			staffId=convertResponsetoArray.getInt("staff_id");
            			
            		}
            		else 
            		{
            			staffId=0;
            		}
            	}
            }
    	}
    	catch (Exception e) 
    	{
			LogUtils.error("Error: Staff List view response is getting properly");
			throw new customException("Error: Staff List view response is getting properly");
		}
    }
}
