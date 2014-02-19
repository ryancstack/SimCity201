package restaurant.huangRestaurant.test.mock;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import restaurant.huangRestaurant.Check;
import restaurant.huangRestaurant.FoodBill;
import restaurant.huangRestaurant.Menu;
import restaurant.huangRestaurant.HuangWaiterRole;
import restaurant.huangRestaurant.MarketAgent.MarketFood;
import restaurant.huangRestaurant.MarketAgent.MyBill;
import restaurant.huangRestaurant.gui.CustomerGui;
import restaurant.huangRestaurant.interfaces.Cashier;
import restaurant.huangRestaurant.interfaces.Customer;
import restaurant.huangRestaurant.interfaces.Market;
import restaurant.huangRestaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockMarket extends Mock implements Market {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public Waiter waiter;
	public List<MyBill> bills;
	public List<MarketFood> inventory;
	public MockMarket(String name) {
		super(name);

	}
	@Override
	public void msgCancelOrder(String request) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgPlsDeliverRequest(String request) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgWhatIsYourStockState(String checkFood, int requirement) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgHereIsPayment(FoodBill b) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgCannotPay(FoodBill b) {
		// TODO Auto-generated method stub
		
	}

}
