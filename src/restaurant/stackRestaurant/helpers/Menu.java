package restaurant.stackRestaurant.helpers;

import java.util.*;

public class Menu {
	@SuppressWarnings("serial")
	private static List<Food> menu = new ArrayList<Food>() {
		{
            add(new Food("Steak", 15.99, true));
            add(new Food("Chicken", 10.99, true));
            add(new Food("Pizza", 8.99, true));
            add(new Food("Salad", 5.99, true));
            
        }
    };
    
    private static Menu sharedInstance = null;
    
    private Menu() {
    	
    }
    
    public static Menu sharedInstance() {
    	if(sharedInstance == null) {
    		sharedInstance = new Menu();
    	}
    	return sharedInstance;
    }
    
    public List<Food> getMenu() {
    	return menu;
    }
    
    public double getInventoryPrice(String name) {
    	for(Food food : menu) {
    		if(food.name == name) {
    			return food.getPrice();
    		}
    	}
    	return 0;
    }
    
    public boolean getInventoryStock(String name) {
    	for(Food food : menu) {
    		if(food.name == name) {
    			return food.getInStock();
    		}
    	}
    	return false;
    }
    
    public void setInventoryStock(String name, boolean inStock) {
    	for(Food food : menu) {
    		if(food.name == name) {
    			food.setInStock(inStock);
    		}
    	}
    }
    
    public static class Food {
    	public Food(String name, double price, boolean inStock) {
    		this.name = name;
    		this.price = price;
    		this.inStock = inStock;
    	}
    	String name;
    	double price;
    	boolean inStock;
    	
    	public double getPrice() {
    		return price;
    	}
    	
    	public boolean getInStock() {
    		return inStock;
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	public void setInStock(boolean inStock) {
    		this.inStock = inStock;
    	}
    }
}
