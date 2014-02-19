package restaurant.stackRestaurant.test.mock;

import agent.Role;
import restaurant.stackRestaurant.helpers.Check;
import restaurant.stackRestaurant.interfaces.Cashier;
import restaurant.stackRestaurant.interfaces.Customer;
import restaurant.stackRestaurant.interfaces.Waiter;
import market.MarketCheck;
import market.interfaces.MarketWorker;

public class MockCashier extends Mock implements Cashier {
	

	public MockCashier(String name) {
		super(name);
	}
	
	public void msgComputeCheck(Waiter waiter, Customer cust, String choice) {
		
	}
	
	public void msgPayCheck(Customer cust, Check check, double money) {
		
	}

	public void msgGiveBill(Check check, MarketWorker market) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgNeedPaycheck(Role role) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGiveBill(MarketCheck marketcheck) {
		// TODO Auto-generated method stub
		
	}

}
