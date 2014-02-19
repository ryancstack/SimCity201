package market.test.mock;

import restaurant.stackRestaurant.interfaces.Cook;
import market.interfaces.MarketWorker;
import restaurant.stackRestaurant.interfaces.Waiter;

public class MockCook extends Mock implements Cook {
	
	public EventLog log;
	public String choice;
	public int inventory;
	
	public MockCook(String name) {
		super(name);
		log = new EventLog();
	}
	
	public void msgCookOrder(Waiter waiter, String choice, int table, int seat) {
		
	}
	
	public void msgInventoryOut(MarketWorker market, String choice) {
		log.add(new LoggedEvent("Recieved msgInventoryOut from Market"));
	}
	
	public void msgMarketDeliveringOrder(int inventory, String choice) {
		log.add(new LoggedEvent("Received msgMarketDeliveringOrder."));
		this.choice = choice;
		this.inventory = inventory;
	}
	
	public void msgAddMarket(MarketWorker market) {
		
	}
	
	public void msgAtCooktop() {
		
	}

	public void msgAtPlating() {
		
	}
	
	public void msgAtFridge() {
		
	}

}
