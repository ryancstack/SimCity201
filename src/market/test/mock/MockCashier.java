package market.test.mock;

import agent.Role;
import restaurant.stackRestaurant.helpers.Check;
import restaurant.stackRestaurant.interfaces.Cashier;
import restaurant.stackRestaurant.interfaces.Customer;
import restaurant.stackRestaurant.interfaces.Waiter;
import market.interfaces.MarketWorker;

public class MockCashier extends Mock implements Cashier {

	public EventLog log;
	public Check check;
	public MarketWorker market;

	public MockCashier(String name) {
		super(name);
		log = new EventLog();
	}
	
	@Override
	public void msgComputeCheck(Waiter waiter, Customer cust, String choice) {
		
	}
	
	@Override
	public void msgPayCheck(Customer cust, Check check, double money) {
		
	}

	@Override
	public void msgGiveBill(Check check, MarketWorker market) {
		log.add(new LoggedEvent("Received msgGiveBill from Market"));
		
		this.check = check;
		this.market = market;		
	}

	@Override
	public void msgNeedPaycheck(Role role) {
		// TODO Auto-generated method stub
		
	}

}
