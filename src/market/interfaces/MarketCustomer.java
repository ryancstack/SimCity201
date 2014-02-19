package market.interfaces;

import java.util.Map;

public interface MarketCustomer {	
	public void msgHereIsBill(double price);
	
	public void msgHereAreYourGroceries(Map<String, Integer> groceries);
	
	public void msgCantFillOrder(Map<String, Integer> groceries);
}
