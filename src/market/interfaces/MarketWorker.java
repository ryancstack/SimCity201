package market.interfaces;

import java.util.List;
import java.util.Map;

import restaurant.CashierInterface;
import restaurant.CookInterface;

public interface MarketWorker {
	public void msgGetGroceries(MarketCustomer customer, Map<String, Integer> groceryList);
	
	public void msgHereIsMoney(MarketCustomer customer, double money);
	
	public void msgCantAffordGroceries(MarketCustomer customer);
	
	public void msgOrderFood(CookInterface cook, CashierInterface cashier, List<String> choice, int amount);
	
	public void msgOrderFood(CookInterface cook, CashierInterface cashier, String choice, int amount);

	public void msgOrderFood(CookInterface cook, CashierInterface cashier, String choice);
	
	public void msgPayForOrder(CashierInterface cashier, double funds);
	
	public void msgCannotPay(CashierInterface cashier, double funds);

	public void msgCancelOrder(CookInterface cook);
}
