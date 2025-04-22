package com.menumitra.apiRequest;

public class StaffCreateRequest {
    private String name;
    private String mobile;
    private String role;
    private String created_by;
    private String aadhar_number;
    private String dob;
    private String device_token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getAadharNumber() {
        return aadhar_number;
    }

    public void setAadharNumber(String aadhar_number) {
        this.aadhar_number = aadhar_number;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
} 