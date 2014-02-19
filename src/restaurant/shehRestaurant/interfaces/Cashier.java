package restaurant.shehRestaurant.interfaces;

import restaurant.CashierInterface;
import restaurant.shehRestaurant.helpers.Bill;
import restaurant.shehRestaurant.test.mock.EventLog;



public interface Cashier extends CashierInterface {

	void msgProcessThisBill(String o, Customer c, Waiter w);
	
	void msgHereToPay(Customer customer, double total);

//	void msgHereIsMarketBill(Bill bill, Market marketAgent);
}