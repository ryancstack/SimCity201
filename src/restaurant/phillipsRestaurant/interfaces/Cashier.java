package restaurant.phillipsRestaurant.interfaces;

import agent.Role;
import restaurant.CashierInterface;
import restaurant.phillipsRestaurant.MarketAgent;
import restaurant.stackRestaurant.helpers.Check;
import market.MarketCheck;
import market.interfaces.*;

public interface Cashier extends CashierInterface{

	void msgPayBill(int table, double moneyOwed);

	void msgHereIsCheck(String choice, int table,
			Waiter waiter);

	void msgPayMarket(MarketAgent market, String food, int amount);
	

}
