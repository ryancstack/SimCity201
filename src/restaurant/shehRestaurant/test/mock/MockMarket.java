package restaurant.shehRestaurant.test.mock;


import java.util.Vector;

import restaurant.shehRestaurant.ShehCashierAgent;
import restaurant.shehRestaurant.helpers.Bill;
import restaurant.shehRestaurant.interfaces.Cashier;
import restaurant.shehRestaurant.interfaces.Cook;
import restaurant.shehRestaurant.interfaces.Customer;
import restaurant.shehRestaurant.interfaces.Market;
import restaurant.shehRestaurant.interfaces.Waiter;

public class MockMarket extends Mock implements Market {
	
	public MockMarket(String name) {
		super(name);
	}
	
	public EventLog log = new EventLog();
	public ShehCashierAgent cashier;

	@Override
	public void msgHereIsPayment(Bill b) {
		log.add(new LoggedEvent("Received msgHereIsPayment from cashier. Payment = " + b.m));
		
	}

	@Override
	public void msgOrderForReplenishment(Vector<String> lowItems,
			Cook cookAgent, Cashier cashier) {
		//not related to cashier test
		
	}

}
