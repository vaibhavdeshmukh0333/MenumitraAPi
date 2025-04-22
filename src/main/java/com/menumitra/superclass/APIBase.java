package com.menumitra.superclass;

import java.util.Map;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import com.menumitra.utilityclass.TokenManager;
import com.menumitra.utilityclass.LogUtils;

public class APIBase {
    
    @BeforeMethod
    public void beforeMethod() {
        // Verify user is logged in before each test
        if (!TokenManager.isLoggedIn()) {
            LogUtils.warn("User is not logged in. Please ensure login test runs first.");
        }
    }

    @AfterMethod
    public void afterMethod() {
        // Don't logout after each test, only after the test suite
    }

    @AfterClass
    public void afterClass() {
        // Logout after all tests in the class are complete
        if (TokenManager.isLoggedIn()) {
            TokenManager.logout();
        }
    }

    protected Map<String, String> getAuthHeaders() {
        return TokenManager.getAuthHeaders();
    }
} 