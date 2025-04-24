package com.menumitra.apiRequest;

public class staffRequest 
{
    private String user_id;
    private String name;
    private String  mobile;
    private String dob;
    private String address;
    private String role;
    private String aadhar_number;
    private String outlet_id;
    private String device_token;
	private String staff_id;
    
	public String getDevice_token() {
		return device_token;
	}
	public String getStaff_id() {
		return staff_id;
	}
	public void setStaff_id(String staff_id) {
		this.staff_id = staff_id;
	}
	public void setDevice_token(String device_token) {
		this.device_token = device_token;
	}
	public String getUser_id() 
	{
		return user_id;
	}
	public void setUser_id(String user_id) 
	{
		this.user_id = user_id;
	}
	public String getName() 
	{
		return name;
	}
	public void setName(String name) 
	{
		this.name = name;
	}
	public String getMobile() 
	{
		return mobile;
	}
	public void setMobile(String mobile) 
	{
		this.mobile = mobile;
	}
	public String getDob() 
	{
		return dob;
	}
	public void setDob(String dob)
	{
		this.dob = dob;
	}
	public String getAddress() 
	{
		return address;
	}
	public void setAddress(String address)
	{
		this.address = address;
	}
	public String getRole() 
	{
		return role;
	}
	public void setRole(String role)
	{
		this.role = role;
	}
	public String getAadhar_number() 
	{
		return aadhar_number;
	}
	public void setAadhar_number(String aadhar_number) 
	{
		this.aadhar_number = aadhar_number;
	}
	public String getOutlet_id() 
	{
		return outlet_id;
	}
	public void setOutlet_id(String outlet_id) 
	{
		this.outlet_id = outlet_id;
	}
    
    
}
