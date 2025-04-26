package com.menumitratCommonAPITestScript;

import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;

public class staffCRUD {

    private int staffId;
    private JSONObject actaulStaffbody;
    private JSONObject expectedStaffBody;
    private String createStafBaseURI;
    private String UpdatestaffBaseURI;
    private String staffViewBaseURI;
    private String staffListViewBaseURI;
    private String staffDeleteBaseURI;
    private StaffCreateTestScript staffCreate;

    @BeforeClass
    void staffCRUDSetup() {

        try {
            LogUtils.info("Staff CRUD Test Setup");
            Object[][] getCreateStffURL = StaffCreateTestScript.getStaffCreateUrl();
            String createStaff = (String) getCreateStffURL[0][2];
            createStafBaseURI = RequestValidator.buildUri(EnviromentChanges.getBaseUrl(),
                    createStaff);
            LogUtils.info("Staff Create Base URI: " + createStafBaseURI);
        } catch (Exception e) {
            LogUtils.error("Error in staffCRUDSetup: " + e.getMessage());
        }

    }

    
    private void VerifyStaffCRUD()
    {
    	
    }
    
    
}
