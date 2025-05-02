package com.menumitra.apiRequest;

import java.util.List;

public class MenuRequest 
{
    private String outlet_id;
    private String menu_cat_id;
    private int user_id;
    private String name;
    private String food_type;
    private String description;
    private String spicy_index;
    private double full_price;
    private Object images;
    private Object portion_data;
    private String ingredients;
    private String offer;
    private String rating;
    private String cgst;
    private String sgst;
    
    // Default constructor
    public MenuRequest() {
    }
    
    
    

    public Object getPortion_data() {
		return portion_data;
	}




	public void setPortion_data(Object object) {
		this.portion_data = object;
	}




	// Getters and Setters
    public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public String getMenu_cat_id() {
        return menu_cat_id;
    }

    public void setMenu_cat_id(String menu_cat_id) {
        this.menu_cat_id = menu_cat_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpicy_index() {
        return spicy_index;
    }

    public void setSpicy_index(String spicy_index) {
        this.spicy_index = spicy_index;
    }

    public double getFull_price() {
        return full_price;
    }

    public void setFull_price(double full_price) {
        this.full_price = full_price;
    }

    public Object getImages() {
        return images;
    }

    public void setImages(Object images) {
        this.images = images;
    }

    
    
    public String getIngredients() {
		return ingredients;
	}




	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}




	public String getOffer() {
		return offer;
	}




	public void setOffer(String offer) {
		this.offer = offer;
	}




	public String getRating() {
		return rating;
	}




	public void setRating(String rating) {
		this.rating = rating;
	}




	public String getCgst() {
		return cgst;
	}




	public void setCgst(String cgst) {
		this.cgst = cgst;
	}




	public String getSgst() {
		return sgst;
	}




	public void setSgst(String sgst) {
		this.sgst = sgst;
	}




	// Override toString() method for debugging and logging
    @Override
    public String toString() {
        return "MenuRequest{" +
                "outlet_id='" + outlet_id + '\'' +
                ", menu_cat_id='" + menu_cat_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                ", food_type='" + food_type + '\'' +
                ", description='" + description + '\'' +
                ", spicy_index='" + spicy_index + '\'' +
                ", full_price=" + full_price +
                ", images='" + images + '\'' +
                '}';
    }
}
