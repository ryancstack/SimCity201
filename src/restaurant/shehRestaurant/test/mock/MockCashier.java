package restaurant.shehRestaurant.test.mock;

import restaurant.shehRestaurant.helpers.Bill;
import restaurant.shehRestaurant.interfaces.Cashier;
import restaurant.shehRestaurant.interfaces.Customer;
import restaurant.shehRestaurant.interfaces.Market;
import restaurant.shehRestaurant.interfaces.Waiter;

public class MockCashier extends Mock implements Cashier {
	
	public MockCashier(String name) {
		super(name);
	}
	
	public EventLog log = new EventLog();

	public void msgProcessThisBill(String o, Customer c, Waiter w) {
		log.add(new LoggedEvent("Received msgProcessThisBill from waiter."));
		
	}

	public void msgHereToPay(Customer customer, double total) {
		log.add(new LoggedEvent("Received msgHereToPay from customer."));
		
	}

	@Override
	public void msgHereIsMarketBill(Bill bill, Market marketAgent) {
		log.add(new LoggedEvent("Received msgHereIsMarketBill from customer."));
		
	}
}
