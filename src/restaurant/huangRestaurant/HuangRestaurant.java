package restaurant.huangRestaurant;




import restaurant.FoodInformation;
import restaurant.Restaurant;
import restaurant.huangRestaurant.ProducerConsumerMonitor;



public class HuangRestaurant extends Restaurant {

	private String name;
	HuangHostAgent host;
	HuangCashierAgent cashier;
	double till = 10000;
	ProducerConsumerMonitor myMonitor;
	
	

	public HuangRestaurant(String name) {
		super();
		this.name = name;
		cashier = new HuangCashierAgent("Money Machine 9001");
		cashier.setRestaurant(this);
		host = new HuangHostAgent("Host", cashier);
		host.setRestaurant(this);
		host.startThread();
		cashier.startThread();	
		myMonitor = new ProducerConsumerMonitor();
		FoodInformation steak = new FoodInformation(6000, 100);
		getFoodInventory().put("Steak", steak);
		
		FoodInformation chicken = new FoodInformation(4000, 100);
		getFoodInventory().put("Chicken", chicken);
		
		FoodInformation salad = new FoodInformation(7000, 100);
		getFoodInventory().put("Salad", salad);
		
		FoodInformation pizza = new FoodInformation(12000, 100);
		getFoodInventory().put("Pizza", pizza);
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public HuangHostAgent getHost() {
		return host;
	}
	
	public HuangCashierAgent getCashier() {
		return cashier;
	}
	
	public double getTill() {
		return till;
	}

	public void setTill(double till) {
		this.till = till;
	}
	public ProducerConsumerMonitor getMyMonitor() {
		return myMonitor;
	}
}
