package market.test.mock;

import java.util.Map;

import market.interfaces.MarketWorker;
import market.interfaces.MarketCustomer;


public class MockMarketCustomer extends Mock implements MarketCustomer {

	public MarketWorker market;
	public EventLog log;
	public double price;
	public Map<String, Integer> groceries;

	public MockMarketCustomer (String name) {
		super(name);
		log = new EventLog();

	}
	
	@Override
	public void msgHereIsBill(double price) {
		log.add(new LoggedEvent("Received msgHereIsBill from Market. Price = $" + price));
		this.price = price;
	}

	@Override
	public void msgHereAreYourGroceries(Map<String, Integer> groceries) {
		log.add(new LoggedEvent("Received msgHereAreYourGroceries from Market."));
		this.groceries = groceries;
	}

	@Override
	public void msgCantFillOrder(Map<String, Integer> groceries) {
		log.add(new LoggedEvent("Received msgCantFillOrder from Market."));
	}
}
