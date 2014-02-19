package restaurant.shehRestaurant.test.mock;


import restaurant.shehRestaurant.helpers.Bill;
import restaurant.shehRestaurant.interfaces.Cashier;
import restaurant.shehRestaurant.interfaces.Customer;
import restaurant.shehRestaurant.interfaces.Waiter;

public class MockWaiter extends Mock implements Waiter {
	
	public MockWaiter(String name) {
		super(name);
	}
	
	public EventLog log = new EventLog();

	public void msgCollectBill(Bill bill) {
		log.add(new LoggedEvent("Received msgCollectBill from cashier."));
		
	}
}
