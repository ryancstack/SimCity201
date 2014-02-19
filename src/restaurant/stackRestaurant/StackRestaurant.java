package restaurant.stackRestaurant;

import restaurant.FoodInformation;
import restaurant.Restaurant;

public class StackRestaurant extends Restaurant {

	private String name;
	private StackHostAgent host;
	private StackCashierAgent cashier;
	
	public StackRestaurant(String name) {
		super();
		FoodInformation steak = new FoodInformation(6000, 100);
		getFoodInventory().put("Steak", steak);
		
		FoodInformation chicken = new FoodInformation(4000, 100);
		getFoodInventory().put("Chicken", chicken);
		
		FoodInformation salad = new FoodInformation(7000, 100);
		getFoodInventory().put("Salad", salad);
		
		FoodInformation pizza = new FoodInformation(12000, 100);
		getFoodInventory().put("Pizza", pizza);
		
		this.name = name;
		host = new StackHostAgent();
		cashier = new StackCashierAgent();
		cashier.setRestaurant(this);
		host.setRestaurant(this);
		host.startThread();
		cashier.startThread();	
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public StackHostAgent getHost() {
		return host;
	}
	
	public StackCashierAgent getCashier() {
		return cashier;
	}

}
