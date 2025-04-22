package com.menumitra.apiRequest;

public class LogoutRequest
{
	private String user_id;
	private String role;
	private String app;
	private String device_token;
	
	
	public String getUser_id() 
	{
		return user_id;
	}
	public void setUser_id(String user_id) 
	{
		this.user_id = user_id;
	}
	public String getRole() 
	{
		return role;
	}
	public void setRole(String role) 
	{
		this.role = role;
	}
	public String getApp()
	{
		return app;
	}
	public void setApp(String app)
	{
		this.app = app;
	}
	public String getDevice_token() 
	{
		return device_token;
	}
	public void setDevice_token(String device_token) 
	{
		this.device_token = device_token;
	}
}
