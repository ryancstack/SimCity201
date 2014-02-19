package market.test.mock;

import java.util.Map;

import restaurant.CashierInterface;
import restaurant.CookInterface;
import restaurant.stackRestaurant.interfaces.Cashier;
import restaurant.stackRestaurant.interfaces.Cook;
import market.interfaces.MarketWorker;
import market.interfaces.MarketCustomer;

public class MockMarket extends Mock implements MarketWorker {

	public MarketCustomer customer;
	public CookInterface cook;
	public CashierInterface cashier;
	public EventLog log;
	public Map<String, Integer> groceries;
	public double money;

	public MockMarket(String name) {
		super(name);
		log = new EventLog();
	}
	
	@Override
	public void msgGetGroceries(MarketCustomer customer, Map<String, Integer> groceryList) {
		log.add(new LoggedEvent("Received msgGetGroceries from MarketCustomer"));
		groceries = groceryList;
	}

	@Override
	public void msgHereIsMoney(MarketCustomer customer, double money) {
		log.add(new LoggedEvent("Received msgHereIsMoney from MarketCustomer. Money = $" + money));
		this.money = money;
	}

	@Override
	public void msgCantAffordGroceries(MarketCustomer customer) {
		log.add(new LoggedEvent("Received msgCantAffordGroceries from MarketCustomer"));
	}

	@Override
	public void msgOrderFood(CookInterface cook, CashierInterface cashier, String choice) {
		log.add(new LoggedEvent("Received msgOrderFood from cook. Ordered: " + choice));
		this.cook = cook;
		this.cashier = cashier;
	}

	@Override
	public void msgPayForOrder(CashierInterface cashier, double funds) {
		log.add(new LoggedEvent("Received msgPayForOrder from cook. Money = $" + funds));
		this.money = funds;		
	}

	@Override
	public void msgOrderFood(CookInterface cook, CashierInterface cashier,
			String choice, int amount) {
		log.add(new LoggedEvent("Received msgOrderFood"));
		
	}

	@Override
	public void msgCannotPay(CashierInterface cashier, double funds) {
		log.add(new LoggedEvent("Received msgCannotPay"));
		
	}

	@Override
	public void msgCancelOrder(CookInterface cook) {
		log.add(new LoggedEvent("Received msgCancelOrder"));
		
	}
}
