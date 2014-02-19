package restaurant.nakamuraRestaurant;

import restaurant.FoodInformation;
import restaurant.Restaurant;


public class NakamuraRestaurant extends Restaurant {

	private String name;
	NakamuraHostAgent host;
	NakamuraCashierAgent cashier;
	ProducerConsumerMonitor myMonitor;

	public NakamuraRestaurant(String name) {
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
		cashier = new NakamuraCashierAgent("NakamuraRestaurant Cashier");
		cashier.setRestaurant(this);
		cashier.startThread();
		host = new NakamuraHostAgent("NakamuraRestaurant Host");
		host.startThread();
		myMonitor = new ProducerConsumerMonitor();
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public NakamuraHostAgent getHost() {
		return host;
	}
	
	public NakamuraCashierAgent getCashier() {
		return cashier;
	}
	
	public ProducerConsumerMonitor getMyMonitor() {
		return myMonitor;
	}

}
