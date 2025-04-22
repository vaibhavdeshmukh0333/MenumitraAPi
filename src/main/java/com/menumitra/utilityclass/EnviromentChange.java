package com.menumitra.utilityclass;

import java.io.FileInputStream;
import java.util.Properties;

public class EnviromentChange {
    private static Properties prop;
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    static {
        try {
            prop = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_PATH);
            prop.load(fis);
        } catch (Exception e) {
            LogUtils.error("Error loading environment configuration: " + e.getMessage());
        }
    }

    public static String getBaseUrl() {
        String env = System.getProperty("env", "qa"); // default to qa if not specified
        String baseUrl;
        
        switch(env.toLowerCase()) {
            case "prod":
                baseUrl = prop.getProperty("prod.base.url");
                break;
            case "stage":
                baseUrl = prop.getProperty("stage.base.url");
                break;
            case "dev":
                baseUrl = prop.getProperty("dev.base.url");
                break;
            default:
                baseUrl = prop.getProperty("qa.base.url");
        }
        
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new RuntimeException("Base URL not found for environment: " + env);
        }
        
        return baseUrl;
    }

    public static String getEnvironment() {
        return System.getProperty("env", "qa");
    }
} 