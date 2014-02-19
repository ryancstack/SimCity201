package restaurant.shehRestaurant.helpers;

import java.util.HashMap;
import java.util.Map;

public class Menu {
	
	public Object[] choices;
	public int size, lowestprice, secondlowestprice, cookTime;

	public Menu() {
		FoodData steak = new FoodData("Steak", 20, 2000, 1);
		FoodData chicken = new FoodData("Chicken", 15, 2000, 1);
		FoodData pizza = new FoodData("Pizza", 20, 2000, 1);
		FoodData salad = new FoodData("Salad", 20, 2000, 1);
		
		Map<String, FoodData> inventory = new HashMap<String, FoodData>(); {
			inventory.put("Steak", steak);
			inventory.put("Chicken", chicken);
			inventory.put("Pizza", pizza);
			inventory.put("Salad", salad);
			
		this.choices = inventory.keySet().toArray();
		this.size = inventory.size();
		this.lowestprice = 15;
		this.secondlowestprice = 20;
		/*for(int i = 0; i < inventory.keySet().size(); i++) {
			this.lowestprice = inventory.keySet().toArray();
			
		}*/
		} //change this so that lowest price is adjustable
			//requires unifying the menus
	}
}

