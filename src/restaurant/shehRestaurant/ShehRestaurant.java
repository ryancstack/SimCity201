package restaurant.shehRestaurant;

import restaurant.FoodInformation;
import restaurant.Restaurant;
import restaurant.shehRestaurant.ProducerConsumerMonitor;


public class ShehRestaurant extends Restaurant {

	private String name;
	ShehHostAgent host;
	ShehCashierAgent cashier;
	ProducerConsumerMonitor myMonitor;

	
	public ShehRestaurant(String name) {
		super();
		this.name = name;
		
		//FOODDATA

		FoodInformation steak = new FoodInformation(6000, 100);
		getFoodInventory().put("Steak", steak);
		
		FoodInformation chicken = new FoodInformation(4000, 100);
		getFoodInventory().put("Chicken", chicken);
		
		FoodInformation salad = new FoodInformation(7000, 100);
		getFoodInventory().put("Salad", salad);
		
		FoodInformation pizza = new FoodInformation(12000, 100);
		getFoodInventory().put("Pizza", pizza);
		
		host = new ShehHostAgent();
		cashier = new ShehCashierAgent();
		host.setRestaurant(this);
		cashier.setRestaurant(this);
		
		host.startThread();
		cashier.startThread();	
		
		myMonitor = new ProducerConsumerMonitor();
	}
	
	public String getName() {
		return name;
	}
	
	public ShehHostAgent getHost() {
		return host;
	}
	
	public ShehCashierAgent getCashier() {
		return cashier;
	}
	
	public ProducerConsumerMonitor getMyMonitor() {
		return myMonitor;
	}
}
