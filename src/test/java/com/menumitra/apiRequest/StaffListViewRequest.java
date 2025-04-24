package com.menumitra.apiRequest;

public class StaffListViewRequest
{
    private String outlet_id;
    private String device_token;
    private int staffId;
    
    
    public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) 
    {
        this.device_token = device_token;
    }



}
