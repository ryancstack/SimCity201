package restaurant.nakamuraRestaurant.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Menu {
	public ArrayList<String> choices;
	public Map<String, Double> prices;
	public Menu() {
		choices = new ArrayList<String>();
		choices.add("Steak");
		choices.add("Chicken");
		choices.add("Salad");
		choices.add("Pizza");
		
		prices = new HashMap<String, Double>();
		prices.put("Steak", 20.00);
		prices.put("Chicken", 15.00);
		prices.put("Pizza", 15.00);
		prices.put("Salad", 10.00);
	}
}
